package deck;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Timer;

import manipulatives.AssetView;
import manipulatives.DoublePoint;
import manipulatives.ManModel;
import manipulatives.ManipInterface;
import basic.Constants;
import basic.Player;
import cards.Card;
import cards.CardView;
import cards.CardViewFactory;
import cards.ShadowCardView;
import cards.TeammateCard;
import cards.TrickCard;
import extras.Debug;
import extras.GameImages;
import extras.GraphicUtils;
import extras.RandomGenerator;

/* This will be a delegation style class
 * that will have a playdeck and all that it entails,
 * but will be primarily responsible for holding all of the CardViews
 */

public class DeckView {
	protected ArrayList<CardView> cardsPlayed;
	protected CardView teammateCard;
	protected PlayDeck deck;
	protected Player player;
	protected boolean legalAdd;
	protected boolean labelShown;
	protected boolean justFreshened;
	protected boolean hasLaunchedFreshPoof;
	private int numFrames;
	private int totalFrames;
	private FreshPoofer poofer;
	protected ArrayList<ManipInterface> manips;
	protected RandomGenerator rgen;
	protected boolean manipsDiscarded;
	protected ArrayList<AssetView> gamePanelManips;

	public boolean isManipsDiscarded() {
		return manipsDiscarded;
	}

	public void setManipsDiscarded(boolean manipsDiscarded) {
		this.manipsDiscarded = manipsDiscarded;
	}

	private boolean highlighted;

	public DeckView(TeammateCard c, Player p) {
		player = p;
		Debug.println(c);
		teammateCard = CardViewFactory.createCard(c);
		if(c.isShadowPlayer()) {
			teammateCard = new ShadowCardView(c);
		}
		cardsPlayed = new ArrayList<CardView>();
		deck = new PlayDeck(c);
		highlighted = false;
		justFreshened = false;
		labelShown = true;
		hasLaunchedFreshPoof = false;
		numFrames = -1;
		manips = new ArrayList<ManipInterface>();
		createManipModels();
		recalculateManips(false);
		rgen = RandomGenerator.getInstance();
	}

	public DeckView(PlayDeck pd, Player p) {
		this(pd.getTeammateCard(), p);
		ArrayList<Card> cards = pd.getAllCards();
		for(int i = 1; i < cards.size(); i++) {
			Card c = cards.get(i);
			CardView cv = CardViewFactory.createCard(c);
			cardsPlayed.add(cv);
		}
	}
	
	private void createManipModels() {
		TeammateCard tc = (TeammateCard) (teammateCard.getCard());
		int numManips = tc.getValue();
		for(int i = 0; i < numManips; i++) {
//			ManipInterface man = new AssetView(GameImages.getMan());
//			if(Constants.USE_CHARACTER_MANIPS_IN_GAME) {
			ManipInterface man = new AssetView(GameImages.getCharacterImage(i, tc));
//			}
			manips.add(man);
		}
	}

	public ArrayList<CardView> getAllCards() {
		ArrayList<CardView> wholeDeck = (ArrayList<CardView>) cardsPlayed.clone();
		wholeDeck.add(0, teammateCard);
		return wholeDeck;
	}

	public boolean addTrickCard(TrickCard c) {
		return addTrickCard(null, c);
	}

	public boolean addTrickCard(CardView cv) {
		return addTrickCard(cv, (TrickCard) cv.getCard());
	}

	public boolean addTrickCard(CardView cv, TrickCard tc) {
		boolean couldAddCard = deck.addTrickCard(tc);
		if(couldAddCard) {
			if(cv == null) {
				cv = CardViewFactory.createCard(tc);
			}
			cardsPlayed.add(cv);
			if(tc.isAir()) {
				justFreshened = true;
			}
		}
		return couldAddCard;
	}

	public boolean removeTrickCard(CardView cv) {
		boolean couldRemoveCard = deck.removeTrickCard((TrickCard) cv.getCard());
		if(couldRemoveCard) {
			cardsPlayed.remove(cv);
		}
		return couldRemoveCard;
	}

	public boolean couldAddTrickCard(CardView cv) {
		return couldAddTrickCard((TrickCard) cv.getCard());
	}

	/**
	 * Does not take into account player associated with deck or card.
	 * @param tc trick card trying to be placed on the deck
	 * @return boolean of whether or not the card could be placed
	 */
	public boolean couldAddTrickCard(TrickCard tc) {
		return deck.couldAddTrickCard(tc);
	}

	/* Right now it will not draw all cards itself
	 * since there needs to be more housekeeping for it to work this way in gamepanel.
	 * We need to change how trickCards are added and where they are kept, and how
	 * we make the little change of placing it above everything else in the z order for it to work.
	 */
	public void drawDeck(Graphics g) {
		ArrayList<CardView> allCards = getAllCards();
		CardView highlightedCard = null;
		for(CardView cv:allCards) {
			if(!cv.isHighlighted()) {
				cv.drawCard(g);
			}else{
				highlightedCard = cv;
			}
		}
		if(highlightedCard != null) {
			highlightedCard.drawCard(g);
		}
		if(isHighlighted() || isLegalAdd()) {
			Color rectColor = Constants.BAD_MOVE_COLOR;
			if(isLegalAdd()) {
				rectColor = Constants.GOOD_MOVE_COLOR;
			}
			drawDeckRectangle(g, rectColor);
		}
		/* something here to override the labelShown */
		if((labelShown || isManipsDiscarded()) && Constants.SHOW_DECK_MANIPS) {
			drawManipulatives(g);
		}
		if(labelShown && Constants.SHOW_DECK_LABEL_NUMBER) {
			drawDeckNumber(g);
		}
	}

	public void drawAbsoluteDeck(Graphics g, CardView cardPlayed, double width, double height) {
		String message = "";
		if(getPlayer().isHuman()) {
			message = Constants.MAN_FRAME_YOUR_DECK_TEXT;
		}else{
			message = Constants.MAN_FRAME_THEIR_DECK_TEXT;
		}
		Font origFont = g.getFont();
		g.setFont(Constants.FONT_SMALL);
		FontMetrics fm = g.getFontMetrics();
//		g.drawString(message, Constants.DECK_VIEW_MARGIN, (int) height-Constants.DECK_VIEW_MARGIN);
		int offset = fm.getHeight();
		g.drawString(message, Constants.DECK_VIEW_MARGIN, offset);
		
		g.setFont(origFont);
		ArrayList<CardView> allCards = getAllCards();
		if(cardPlayed != null) {
			allCards.add(cardPlayed);
		}
		double cardDelta = height/(allCards.size());
		cardDelta = Math.min(cardDelta, Constants.ORIG_CARD_HEIGHT);
		for(int i = 0; i < allCards.size(); i++) {
			CardView cv = allCards.get(i);
			cv.drawCard(g, 0, (int) (i*cardDelta)+offset*2, (int) width, (int) (height-2*offset));
		}
	}

	private void drawDeckNumber(Graphics g) {
		int x = getCardWidth()*2 + getX();
		int y = getCenterY();
		String text = "" + calculateDeck();
		Font oldFont = g.getFont();
		g.setFont(new Font("sans-serif", Font.BOLD, 32));
		g.drawString(text, x, y);
		g.setFont(oldFont);
	}
	
	public String toStream() {
		return deck.toString();
	}

	public int getX() {
		return getTeammateCard().getX();
	}

	public int getY() {
		return getTeammateCard().getY() ;
	}

	public int getCenterY(){
		return getY() + (int) calculateDeckHeight()/2;
	}

	public int getCenterX() {
		return getX() + (int) (getCardWidth()/2);
	}
	
	public ArrayList<ManipInterface> getManipulatives() {
		return manips;
	}

	/*private void drawManipulativesOld(Graphics g) {
		int x = getCardWidth() + getX();
		int y = getY();
		BufferedImage manip = TugImages.getMan();
		BufferedImage stinkManip = TugImages.getStinkyMan();
		BufferedImage freshManip = TugImages.getFreshenedMan();
		if(Constants.DEBUG_MODE) {
			//TODO: put this back in if needed
			//System.out.println("getCardWidth is: " + getCardWidth() + ", while manip width is " + manip.getWidth());
		}
		//TODO: Take away this hack to do something a little nicer.  Use something like the x coordinate to figure
		//out the spacing needed to be able to move around.
		int numOnSide = Math.max(4, getCardWidth()/manip.getWidth());
		int numTotal = deck.initialDeckValue();
		int numSoFar = 0;
		int numLeft = calculateDeck();
		int[] stinkAirs = deck.calculateStinksAndAirsSeparately(true);
		stinkAirs[1] = Math.min(numTotal, stinkAirs[1]);
		for(; numSoFar < numTotal-(stinkAirs[0]+stinkAirs[1]); numSoFar++) {
			drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), manip);
		}
		for(; numSoFar < numTotal-stinkAirs[0]; numSoFar++) {
			if(justFreshened) {
				drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), freshManip);
				launchFreshenedPoof();
			}else{
				drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), manip);
			}
		}
		for(; numSoFar < numTotal; numSoFar++) {
			drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), stinkManip);
		}
		//need to incorporate launchFreshenedPoof();
		if(numFrames >= 0) {
			drawFreshenedGraphic(g, x, y, numOnSide, manip);
		}
	}*/
	
	private void drawManipulatives(Graphics g) {
		int x = getCardWidth() + getX();
		int y = getY();
		BufferedImage manip = GameImages.getMan();
		int numOnSide = calculateNumOnSide(manip);
//		Debug.println("getCardWidth is: " + getCardWidth() + ", while manip width is " + manip.getWidth());
		for(ManipInterface man:manips) {
			drawOneMan(g, man);
		}
		
		//need to incorporate launchFreshenedPoof();
		if(numFrames >= 0) {
			drawFreshenedGraphic(g, x, y, numOnSide, manip);
		}
	}

	private void recalculateManModels(boolean isDesired, int numOnSide, int x, int y) {
		//int x = getCardWidth() + getX();
		//int y = getY();
		BufferedImage manImg = GameImages.getMan();
		int numTotal = deck.initialDeckValue();
		int numSoFar = 0;
		//This may need to be rewritten so that 
		//when you have a 1/2 stink and 3/4 air, you get 6 airs, not 4.
		int[] stinkAirs = deck.calculateStinksAndAirsSeparately(true);
		stinkAirs[1] = Math.min(numTotal, stinkAirs[1]);
		for(; numSoFar < numTotal-(stinkAirs[0]+stinkAirs[1]); numSoFar++) {
			setDesiredOrCurrentLocation(manips, x, y, numSoFar, numOnSide, manImg, isDesired);
		}
		for(; numSoFar < numTotal-stinkAirs[0]; numSoFar++) {
			setDesiredOrCurrentLocation(manips, x, y, numSoFar, numOnSide, manImg, isDesired);
			manips.get(numSoFar).setFresh();
		}
		for(; numSoFar < numTotal; numSoFar++) {
			setDesiredOrCurrentLocation(manips, x, y, numSoFar, numOnSide, manImg, isDesired);
			manips.get(numSoFar).setStinked();
		}
	}
	
	/* Start with the middle part of the list, then add
	 * the stinky manipulatives to the end while adding the regular
	 * ones up front.  Cuts down on writing an extra for loop.
	 */
	private void repositionAssetsInManips(boolean isDesired, int numOnSide, int x, int y) {
		ArrayList<AssetView> reOrdered = new ArrayList<AssetView>();
		for(AssetView manip: gamePanelManips) {
			if(manip.isFresh()) {
				reOrdered.add(manip);
			}
		}
		for(AssetView manip: gamePanelManips) {
			if(manip.isStinky()) {
				reOrdered.add(manip);
			}else if(!manip.isFresh()){
				reOrdered.add(0, manip);
			}
		}
		for(int i = 0; i < reOrdered.size(); i++) {
			AssetView manip = reOrdered.get(i);
			manips.set(i, manip);
			setDesiredOrCurrentLocation(manips, 0, 0, i, numOnSide, GameImages.getMan(), isDesired);
		}
	}
	
	public void setManipsDesiredLocation(int x, int y) {
		Debug.println("setting manips to x:" + x + ", " + y);
		for(ManipInterface m:manips) {
			m.setDesiredX(x);
			m.setDesiredY(y);
		}
	}
	
	/*currently this is not working correctly*/
	private int calculateNumOnSide(BufferedImage manImg) {
		Debug.printlnVerbose("numOnSide? card width:" + getCardWidth() + " , manimg.getwidth():" + manImg.getWidth() + " = " + getCardWidth()/manImg.getWidth());
//		return Math.max(4, getCardWidth()/manImg.getWidth());
		return Constants.NUM_MANIPS_PER_ROW;
	}

	private void setDesiredOrCurrentLocation(ArrayList<ManipInterface> manModels, int baseX, int baseY, int numSoFar, int numOnSide, BufferedImage manImage, boolean isDesired) {
		ManipInterface m = manModels.get(numSoFar);
		int newX = calcManipX(baseX, numSoFar, numOnSide, manImage.getWidth());
		int newY = calcManipY(baseY, numSoFar, numOnSide, manImage.getHeight());
		Debug.println("setting manipulative to: " + newX + ", " + newY);
		if(isDesired) {
			m.setDesiredX(newX);
			m.setDesiredY(newY);
			m.setX(m.getDesiredX()+Constants.PANEL_WIDTH);
			m.setY(m.getDesiredY()+rgen.nextInt(calculateDeckHeight()*-1, calculateDeckHeight()));
		}else{
			m.setX(newX);
			m.setY(newY);
		}
	}

	private void drawFreshenedGraphic(Graphics g, int x, int y, int numInLine, BufferedImage img) {
		int width = teammateCard.getWidth()+numInLine*img.getWidth();
		int centerX = x;
		int height = calculateDeckHeight();
		int centerY = y + height/3;
		int circleWidth = width/2-2*numFrames;
		Color orig = g.getColor();
		double ratio = 256.0 / totalFrames;
		g.setColor(new Color(40, 177, 221, 256 - (int)(ratio*numFrames)));
		DoublePoint centerPt = new DoublePoint(centerX, centerY);
		poofer.graphicDrawn();
		for(int i = 0; i < 5; i++) {
			DoublePoint newPt = GraphicUtils.getPolarProjectedPoint(centerPt, numFrames*2, (360/5)*i);
			int xCoord = (int) (newPt.getX()-circleWidth/2);
			int yCoord = (int) (newPt.getY()-circleWidth/2);
			g.fillOval(xCoord, yCoord, circleWidth, circleWidth);
		}
		g.setColor(orig);
	}

	public void beginFreshenedGraphic(int numTicks) {
		numFrames = 0;
		totalFrames = numTicks;
	}

	public void advanceFreshenedGraphic() {
		numFrames++;
		if(justFreshened && numFrames > totalFrames / 3) {
			justFreshened = false;
			unFreshModels(manips);
			player.fireDeckRepaint(this);
		}
	}
	
	private void unFreshModels(ArrayList<ManipInterface> models) {
		for(ManipInterface m:models) {
			if(m.isFresh()) {
				m.setRegular();
			}
		}
	}

	public void endFreshenedGraphic() {
		numFrames = -1;
		hasLaunchedFreshPoof = false;
		Debug.println("just finished the poof timer");
		player.fireDeckRepaint(this);
		poofer = null;
	}
	
	private void drawOneMan(Graphics g, ManipInterface man) {
		int x = getCardWidth() + getX();
		int y = getY();
		BufferedImage imgToUse = GameImages.getMan();
		BufferedImage origImg = imgToUse;
		if(Constants.USE_CHARACTER_MANIPS_IN_GAME) {
			imgToUse = ((AssetView) man).getImage();
		}
		BufferedImage addedLayer = null;
		if(man.isFresh() && justFreshened()) {
			addedLayer = GameImages.getFreshenedLayer();
		}else if(man.isStinky()) {
			addedLayer = GameImages.getStinkyLayer();
		}
		g.drawImage(imgToUse, x + man.getX(), y + man.getY(), origImg.getWidth(), origImg.getHeight(), null);
		if(addedLayer != null) {
			g.drawImage(addedLayer, x + man.getX(), y + man.getY(), origImg.getWidth(), origImg.getHeight(), null);
		}
	}

	private int calcManipX(int baseX, int numSoFar, int numOnSide, int width) {
		return baseX + (numSoFar%numOnSide)*width;
	}

	private int calcManipY(int baseY, int numSoFar, int numOnSide, int height) {
		return baseY + (numSoFar/numOnSide)*height;
	}

	private int getCardWidth() {
		return (int) getTeammateCard().getSize().getWidth();
	}

	public void launchFreshenedPoof() {
		if(hasLaunchedFreshPoof) return;
		hasLaunchedFreshPoof = true;
		poofer = new FreshPoofer(player, this);
		Timer poofTimer = new Timer(20, poofer);
		poofTimer.setInitialDelay((int) (Constants.BETWEEN_GAME_PAUSE*1.2));
		poofer.setTimer(poofTimer);
		poofTimer.start();
	}

	private void drawDeckRectangle(Graphics g, Color c) {
		int x = getTeammateCard().getX();
		int y = getTeammateCard().getY();
		int width = getCardWidth();
		int height = (int) calculateDeckHeight();
		GraphicUtils.drawThickRectangle(x, y, width, height, c, g);
	}

	public int calculateDeckHeight() {
		int topY = getTeammateCard().getY();
		CardView topCard = getTopMostCard();
		int y2 = topCard.getY();
		int bottomY = y2 + (int) topCard.getSize().getHeight();
		return bottomY - topY;	
	}

	/* 
	 * Returns the topmost card which also happens to be the lowest card 
	 * on the y axis in the pile.
	 */
	private CardView getTopMostCard() {
		ArrayList<CardView> cards = getAllCards();
		return cards.get(cards.size()-1);
	}

	public CardView getTeammateCard() {
		return teammateCard;
	}

	public CardView getTrickOnTop() {
		return cardsPlayed.get(cardsPlayed.size()-1);
	}

	public int calculateDeck() {
		return deck.calculateDeck();
	}

	public int calculateDeckMinusTop() {
		return deck.calculateDeckMinusTop();
	}

	public int initialDeckValue() {
		return deck.initialDeckValue();
	}

	public int getPotentialScoreChange(CardView cv) {
		TrickCard tc = (TrickCard) cv.getCard();
		int currentScore = calculateDeck();
		int teammates = deck.calculatePotentialScore(tc);
		return Math.abs(currentScore-teammates);
	}

	public PlayDeck getPlayDeck() {
		return deck;
	}

	public Player getPlayer() {
		return player;
	}

	public void setHighlighted(boolean h, boolean isLegal) {
		highlighted = h;
		legalAdd = isLegal;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public boolean isLegalAdd() {
		return legalAdd;
	}

	public void showLabel(boolean lS, boolean hasChanged) {
		labelShown = lS;
		/*might not be the appropriate place, if so, clear the hasChanged variable */
		/*if(hasChanged) {
			recalculateManModels();
		}*/
	}

	public void recalculateManips(boolean isDesired) {
		int numOnSide = calculateNumOnSide(GameImages.getMan());
		if(gamePanelManips != null) {
			numOnSide = calculateNumOnSide(gamePanelManips.get(0).getImage());
			repositionAssetsInManips(isDesired, numOnSide, 0, 0);
			gamePanelManips = null;
		}else{
			recalculateManModels(isDesired, numOnSide, 0, 0);
		}
	}
	
	public boolean justFreshened() {
		return justFreshened;
	}
	
	public void setAssetManips(ArrayList<AssetView> assets) {
		gamePanelManips = assets;
	}
	
	// Deprecated or unsupported methods now
	private void drawOneMan(Graphics g, int x, int y, int numSoFar, int numOnSide, BufferedImage manImage) {
		drawOneMan(g, x, y, numSoFar, numOnSide, manImage.getWidth(), manImage.getHeight(), manImage);
	}

	private void drawOneMan(Graphics g, int x, int y, int numSoFar, int numOnSide, int width, int height, BufferedImage manImage) {
		int manX = calcManipX(x, numSoFar, numOnSide, width);
		int manY = calcManipY(y, numSoFar, numOnSide, height);
		//Debug.println("Drawing men at " + manX + ", " + manY + "with dimensions: " + manImage.getWidth() + ", " + manImage.getHeight());
		g.drawImage(manImage, manX, manY, width, height, null);
	}
}
