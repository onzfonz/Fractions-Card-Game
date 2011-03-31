package pebblebag;
/*
 * PebblePanel will hold a couple of things
 * it will have.
 * An image of a pebble bag
 * The pebbles that are supposed to be in the bag.
 * (This will be determined by the PebbleBag object which it will have one of);
 * The pebble bag will be divided into two states.
 * One state will be where one play can shake the bag
 * The other state will be where the player can draw out a pebble.
 * Both of these actions will occur using mouse listeners.
 * Each pebble will be drawn in the bag, and will be a one to one correspondence
 * However, there must be when they draw out from the bag, a way of not letting them
 * get past a threshold so that they cheat.
 * After a certain point, we will replace the image that they have with the image given
 * Once that happens, we can denote that the game the turn is over simply by looking at the
 * image that is currently loaded on the bag.
 * 
 * PebbleModel object will be one that has an ice cream Card followed by the number of Radios that it has.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import network.NetDelegate;
import network.NetHelper;
import basic.Constants;
import cards.TeammateCard;
import cards.TrickCard;
import deck.DeckView;
import deck.PlayDeck;
import extras.Debug;
import extras.RandomGenerator;

public class PebblePanel extends JPanel implements PebbleListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6965341647940551092L;
	private int numIceCreamsPlayed;
	private int lastX, lastY;
	private int chooseX, chooseY;
	private ArrayList<PebbleBagView> plays;
	private PebbleView currentPebble;
	private RandomGenerator rgen;
	private JLabel statusLine1;
	private JLabel statusLine2;
	private boolean userCanClick;
	private boolean playerIsShakingBag;
	private boolean playerShouldStartShaking;
	private JFrame frame;
	private DeckView deckPlayedOn;
	private ArrayList<IceWindowListener> listeners;
	private NetDelegate netRep;
	private BagShaker shaker;
	private Semaphore shakingDone;
	private Semaphore pebbleMoving;
	private PebblePanelListener listener;
	private String participant;

	/* Need to have a good shake pebble method
	 * that will have the pebbles randomly move in their locations
	 * yet still stay inside the bag.
	 */
	public PebblePanel(DeckView dv, int w, int h, boolean playerStarts, JFrame f, NetDelegate nRep, PebblePanelListener l, String pName) {
		deckPlayedOn = dv;
		netRep = nRep;
		listener = l;
		participant = pName;
		shakingDone = new Semaphore(0, true);
		pebbleMoving = new Semaphore(0, true);
		PlayDeck pd = dv.getPlayDeck();
		setupPanel(pd, w, h, playerStarts, f);
	}

	//This is only really for testing purposes - should not support this!
	PebblePanel(PlayDeck pd, int w, int h, boolean playerStarts, JFrame f) {
		setupPanel(pd, w, h, playerStarts, f);
	}

	private void setupPanel(PlayDeck pd, int w, int h, boolean playerStarts, JFrame f) {
		rgen = RandomGenerator.getInstance();
		numIceCreamsPlayed = 0;
		plays = new ArrayList<PebbleBagView>();
		listeners = new ArrayList<IceWindowListener>();
		chooseX = 0;
		chooseY = 0;
		userCanClick = true;
		frame = f;
		playerIsShakingBag = playerStarts;
		setLayout(new BorderLayout());
		JPanel southBox = new JPanel();
		southBox.setLayout(new BoxLayout(southBox, BoxLayout.Y_AXIS));
		add(southBox, BorderLayout.SOUTH);
		statusLine1 = new JLabel(" ");
		statusLine1.setFont(Constants.FONT_SMALL);
		southBox.add(statusLine1);

		statusLine2 = new JLabel(" ");
		statusLine2.setFont(Constants.FONT_SMALL);
		southBox.add(statusLine2);

		setPreferredSize(new Dimension(w, h));
		ArrayList<TrickCard> cards = pd.getTrickCards();
		int index = firstIceInDeck(cards);
		if(index != -1) {
			createPebbleModelsForDeck(cards, index, w, h);

		}
		updateStatusMessage(playerStarts);
		beginInsertingAnimation();
		playerShouldStartShaking = playerStarts;

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(userCanClick) {
					if(e.getButton() == MouseEvent.BUTTON3) {

						resetBag();
					}
					if(getCurrentPebbleBag().timeToDrawAPebble()) {
						currentPebble = findPebble(e);
					}
					lastX = e.getX();
					lastY = e.getY();
				}
			}

			public void mouseReleased(MouseEvent e) {
				if(userCanClick) {
					PebbleBagView cpb = getCurrentPebbleBag();
					if(currentPebble != null && currentPebble.isHidden()) {
						repaintPebble(currentPebble);
						if(currentPebble.completelyOutsideOfBag()) {
							PebbleView peb = currentPebble;
							revealPebble(peb);
							NetHelper.sendNetChip(netRep, peb.isOrange());
						}else if(!currentPebble.insideOfBag()) {
							currentPebble.moveBackNearEdges();
						}
						repaint();
					}
					currentPebble = null;
				}
			}
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(userCanClick) {
					int currentX = e.getX();
					int currentY = e.getY();

					if(getCurrentPebbleBag().bagNeedsShaking()) {
						shakeTheBag(currentX, currentY);
					}else if(currentPebble != null) {
						movePebble(currentX, currentY);
					}

					lastX = currentX;
					lastY = currentY;
					repaint();
				}
			}
		});
	}

	public int numPlays() {
		return plays.size();
	}

	public int numPebbles() {
		return getCurrentPebbleBag().numPebbles();
	}

	public void makeAnOutsidePebbleCurrent() {
		turnOffUser();
		currentPebble = getCurrentPebbleBag().getOutsidePebble();
	}

	public void resetBag() {
		getCurrentPebbleBag().resetBag();
		repaint();
	}

	private void createPebbleModelsForDeck(ArrayList<TrickCard> cards, int index, int pWidth, int pHeight) {
		int numRadios = 0;
		for(int i = index+1; i < cards.size(); i++) {
			TrickCard tc = cards.get(i);
			if(tc.isIceCream()) {
				createPebbleModel(cards.get(index), numRadios, pWidth, pHeight);
				index = i;
				numRadios = 0;
			}else if(tc.isRadio()) {
				numRadios++;
			}
		}
		createPebbleModel(cards.get(index), numRadios, pWidth, pHeight);
	}

	private void createPebbleModel(TrickCard tc, int numRs, int pWidth, int pHeight) {
		PebbleBagView pm = new PebbleBagView(tc, numRs, pWidth/2-Constants.PEBBLE_BAG_SIZE/2, pHeight/2-Constants.PEBBLE_BAG_SIZE/2, this);
		plays.add(pm);
	}

	private int firstIceInDeck(ArrayList<TrickCard> cards) {
		for(int i = 0; i < cards.size(); i++) {
			TrickCard tc = cards.get(i);
			if(tc.isIceCream()) {
				return i;
			}
		}
		return -1;
	}

	private PebbleBagView getCurrentPebbleBag() {
		if(plays.size() > 0) {
			if(plays.size() > numIceCreamsPlayed) {
				return plays.get(numIceCreamsPlayed);
			}else{
				return plays.get(plays.size()-1);
			}
		}else{
			return null;
		}
	}

	public int getFuturePebbleX() {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getFutureX(this);
	}

	public int getFuturePebbleY(int xCoord) {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getFutureY(this, xCoord);
	}

	public int getPebbleX() {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getX() + currentPebble.getX();
	}

	public int getPebbleY() {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getY() + currentPebble.getY();
	}

	public int getInsideBagX() {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getX() + pbv.getWidth()/2 - Constants.PEBBLE_SIZE/2;
	}

	public int getInsideBagY() {
		PebbleBagView pbv = getCurrentPebbleBag();
		return pbv.getY() + pbv.getHeight()/2 - Constants.PEBBLE_SIZE/2;
	}

	public void setChooseCoords() {
		turnOffUser();
		chooseX = lastX;
		chooseY = lastY;
	}

	public void startInitialShaking() {
		if(!playerShouldStartShaking) {
			beginShakingAnimation();
		}else{
			turnOnUser();
		}
	}

	public void beginShakingAnimation() {
		shaker = new BagShaker(this);
		Timer bagTimer = new Timer(20, shaker);
		bagTimer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		shaker.setTimer(bagTimer);
		bagTimer.start();
		shakingDone.release();
		notifyBagShaking();
	}
	
	public void stopShakingAnimation() {
		boolean didIt = false;
		try{
			shakingDone.acquire();
			shaker.stopShaking();
			shaker = null;
			didIt = true;
		}catch(InterruptedException e) {
			e.printStackTrace();
			if(didIt) shakingDone.release();
			return;
		}
	}

	public void beginInsertingAnimation() {
		BagInserter bagI = new BagInserter(this);
		Timer bagTimer = new Timer(20, bagI);
		bagI.setTimer(bagTimer);
		bagTimer.start();
	}
	
	public void tellOppoToChoosePebbles() {
		NetHelper.sendNetShaked(netRep, this);
		turnOffUser();
		doneShakingTheBag(true);
	}

	public void beginPebbleAnimation(boolean isOrange) {
		if(userActive() && !Constants.NETWORK_MODE) {
			turnOffUser();
			forcePebbleAnimation(isOrange);
		}else if(Constants.NETWORK_MODE){
			forcePebbleAnimation(isOrange);
		}
	}
	
	private void forcePebbleAnimation(boolean isOrange) {
		boolean didIt = false;
		try{
			Debug.println("Waiting to acquire pebbleMover");
			pebbleMoving.acquire();
			Debug.println("acquired pebblemover");
			PebbleMover pebM = new PebbleMover(this, isOrange);
			Timer pebbleTimer = new Timer(20, pebM);
			pebM.setTimer(pebbleTimer);
			pebbleTimer.start();
			didIt = true;
		}catch(InterruptedException e) {
			e.printStackTrace();
			if(!didIt) pebbleMoving.release();
			Debug.println("released pebblemover from catch clause");
			return;
		}
	}
	
	public void pebbleMoverDone() {
		pebbleMoving.release();
		Debug.println("released pebbleMover");
	}

	public void compShakeBag() {
		chooseX = chooseX + rgen.nextInt(-1, 1)*20;
		chooseY = chooseY + rgen.nextInt(-1, 1)*20;
		shakeTheBag(chooseX, chooseY);
		lastX = chooseX;
		lastY = chooseY;
		repaint();
	}

	private void shakeTheBag(int x, int y) {
		Point curP = new Point(x, y);
		Point lastP = new Point(lastX, lastY);
		double distance = curP.distance(lastP);
		//Debug.println(distance);
		int dragX = (x - lastX)/5;
		int dragY = (y - lastY)/5;
		getCurrentPebbleBag().moveBy(dragX, dragY);
		if(distance > 15) {
			PebbleBagView curBag = getCurrentPebbleBag();
			curBag.shuffleBag();
		}
	}

	//compSelectPebble: 
	public PebbleView compSelectPebble() {
		turnOffUser();
		currentPebble = getCurrentPebbleBag().choosePebbleFromBag();
		return currentPebble;
	}
	
	/**
	 * Function used to notify panel and bag that a certain chip was drawn.
	 */
	public void chipDrawn(boolean isOrange) {
		beginPebbleAnimation(isOrange);
	}
	
	public PebbleView oppoSelectedPebble(boolean isOrange) {
		turnOffUser();
		currentPebble = getCurrentPebbleBag().choosePebbleFromBag(isOrange);
		return currentPebble;
	}

	public void movePebble(int x, int y) {
		int dx = x - lastX;
		int dy = y - lastY;
		movePebbleBy(dx, dy);
	}

	public void movePebbleBy(double dx, double dy) {
		repaintPebble(currentPebble);
		currentPebble.moveBy((int) dx, (int) dy);
		repaintPebble(currentPebble);
	}

	private PebbleView findPebble(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		return getCurrentPebbleBag().findPebble(x, y);
	}

	public void revealPebble(PebbleView peb) {
		peb.setHidden(false);
		repaint();
		if(!getCurrentPebbleBag().updateTurnCount()) {
			Timer temp = new Timer(3000, new MessageDisplay(this));
			temp.setRepeats(false);
			temp.start();
		}
		currentPebble = null;
		updateStatusMessage(!playerIsShakingBag);
	}

	public void wrapUpBag() {
		displayResult();
		advanceBag();
	}

	private void displayResult() {
		//JOptionPane.showConfirmDialog(frame, "Did Ice Cream Work? " + getCurrentPebbleBag().didIceCreamTruckWork());
	}

	private boolean kidsRanToATruck() {
		for(PebbleBagView pbv:plays) {
			if(pbv.didIceCreamTruckWork()) {
				return true;
			}
		}
		return false;
	}

	private void advanceBag() {
		numIceCreamsPlayed++;
		if(!noMorePlays()) {
			updateStatusMessage(!playerIsShakingBag);
		}else{
			//resetBag();
			updateStatusMessage(playerIsShakingBag);
		}
	}

	public boolean noMorePlays() {
		return numIceCreamsPlayed == plays.size();
	}

	public void doneShakingTheBag(boolean isHumanPlayer) {
		getCurrentPebbleBag().doneShakingTheBag();
		updateStatusMessage(!isHumanPlayer);
		listener.bagShakingDone();
		pebbleMoving.release();
		Debug.println("released pebblemover from doneshakingbag");
	}

	public void updateStatusMessage(boolean isPlayersTurn) {
		PebbleBagView pbv = getCurrentPebbleBag();
		String s1 = pbv.numTurnsLeft() + " draw" + ((pbv.numTurnsLeft()!=1)? "s":"") + " left.";
		String s2 = "";
		if(noMorePlays()){
			boolean kidsRan = kidsRanToATruck();
			if(kidsRan) {
				s1 = "The kids ran to the ice cream!";
			}else{
				s1 = "The kids are safe!";
			}
			pebbleBagWindowDone(kidsRan);
		}else if(pbv != null && pbv.bagNeedsShaking()) {
			if(isPlayersTurn) {
				s2 += participant + ", please shake the bag.";
				listener.bagShakingStarted();
			}else{
				s2 += "Opponent shakes the bag.";
			}
		}else if(pbv != null) {
			if(isPlayersTurn) {
				s2 += participant + ", please take a chip out.";
			}else{
				s2 += "Opponent takes a chip.";
			}
		}
		if(plays.size() == 0) {
			s1 = "No Ice Cream Trucks.";
		}
		statusLine1.setText(s1);
		statusLine2.setText(s2);
		repaint();
	}

	private void pebbleBagWindowDone(boolean kidsRan) {
		getCurrentPebbleBag().showPebbles();
		PebbleCloser pc = new PebbleCloser(this, deckPlayedOn, kidsRan);
		Timer t = new Timer(20, pc);
		t.setRepeats(false);
		t.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		t.start();
	}

	public boolean morePebblesNeedToBeRemoved() {
		return getCurrentPebbleBag().pebblesStillNeedToBeDrawn();
	}

	public boolean morePebblesNeedToBeMovedIn() {
		return !getCurrentPebbleBag().allPebblesInside();
	}

	public void turnOffUser() {
		userCanClick = false;
	}

	public void turnOnUser() {
		userCanClick = true;
	}

	public boolean userActive() {
		return userCanClick;
	}

	public void addBagListener(IceWindowListener bl) {
		listeners.add(bl);
	}

	public void removeBagListener(IceWindowListener bl) {
		listeners.remove(bl);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		PebbleBagView pv = getCurrentPebbleBag();
		if(pv != null) {
			pv.drawBag(g);
		}
	}

	private void repaintPebble(PebbleView pv) {
		repaint(pv.getX(), pv.getY(), Constants.PEBBLE_BAG_SIZE, Constants.PEBBLE_BAG_SIZE);
	}

	public void fireIceCreamTruckDone(DeckView dv, boolean didKidsRunToTruck) {
		for(IceWindowListener l:listeners) {
			l.iceCreamTruckDone(dv, didKidsRunToTruck);
		}
	}
	
	public void notifyBagShaking() {
		for(IceWindowListener l:listeners) {
			l.timeWhenBagShaking();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Pebble Panel");

		PlayDeck p1 = new PlayDeck(new TeammateCard("", "Johnson Family", "", 8));

		TrickCard halfStink = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 2, "Stink");
		TrickCard qtrStink = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 4, "Stink");
		TrickCard threeQtrStink = new TrickCard(Constants.HALF_STINK_FILENAME, 3, 4, "Stink");
		TrickCard twoThirdStink = new TrickCard(Constants.HALF_STINK_FILENAME, 2, 3, "Stink");
		TrickCard thirdStink = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 3, "Stink");

		TrickCard halfAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 2, "Air");
		TrickCard qtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 4, "Air");
		TrickCard threeQtrAir = new TrickCard(Constants.HALF_AIR_FILENAME, 3, 4, "Air");
		TrickCard twoThirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 2, 3, "Air");
		TrickCard thirdAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 3, "Air");
		TrickCard fifthAir = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 5, "Air");

		TrickCard two2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 2, 2, "Ice");
		TrickCard one2Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 2, "Ice");
		TrickCard one9Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 9, "Ice");
		TrickCard thirteen11Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 13, 11, "Ice");
		TrickCard one5Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 5, "Ice");
		TrickCard five1Ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 5, 1, "Ice");

		TrickCard radio = new TrickCard(Constants.RADIO_FILENAME, 1, 1, "Radio");
		//p1.addTrickCard(halfStink);
		p1.addTrickCard(one2Ice);
		p1.addTrickCard(radio);

		PebblePanel pPanel = new PebblePanel(p1, 400, 600, true, frame);
		frame.add(pPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
