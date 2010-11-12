package basic;
/*
 * This is going to be the main file that will be dealing with all of the views for the cards.  It will be a heavy file
 * that will contain all of the game states including the decks, the players hand, and so on.
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import manipulatives.ManFrame;
import manipulatives.ManListener;
import pebblebag.BagListener;
import pebblebag.PebbleFrame;
import cards.CardGameConstants;
import cards.CardView;
import cards.TeammateCard;
import cards.TrickCard;
import deck.DeckView;
import deck.PlayDeck;
import extras.PanelListener;

public class GamePanel extends JPanel implements PlayerListener, ComponentListener, BagListener, ManListener {

	/* Panel will have
	 * opponent's decks in play up top
	 * person's decks in play down below
	 * peson's hand of trick cards below that
	 */
	/* Instance Variables */
	private ArrayList<CardView> playersTrickCards; //Represents the initial hand of trick cards
	private ArrayList<CardView> currentTrickCardsLeft; //Represents the current cards that haven't been played yet
	private ArrayList<CardView> opponentsTrickCardsLeft; //Represents trick cards for computer.
	private ArrayList<CardView> opponentsTrickCards; //Represents the initial hand of trick cards for the opponent
	private Game currentGame;
	private ArrayList<CardView> cardImages;
	private ArrayList<DeckView> decksInPlay;
	private ArrayList<ManFrame> manWindows;

	private CardView currentCard;
	private CardView bigCard;
	private CardView cardHidden;
	private CardView cardUsed;
	private int cardUsedX;
	private int cardUsedY;
	private DeckView deckUsed;
	private DeckView currentDeck;
	private int curSelOrigIndex;
	private int lastX;
	private int lastY;
	private int origX;
	private int origY;
	private boolean dragging;
	private boolean userCanInteract;
	private JLabel status;
	private PebbleFrame pebbleWindow;
	private PanelListener panel;

	private static final int PLAYDECK_OFFSET = 60;
	//private static final int MINIMUM_CARD_DISPLACEMENT = 15;


	//Different booleans for maybe future settable options
	private boolean notCoolMan;

	public GamePanel(int w, int h, PanelListener ls, boolean ncl) {
		setPreferredSize(new Dimension(w, h));
		setOpaque(true);
		setBackground(Color.gray);
		setLayout(new BorderLayout(10, 10));
		notCoolMan = ncl;
		panel = ls;

		resetPanel();
		addStatusBox();

		//need to manually reshuffle them save the index to where it should be and then move it to the end.
		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				CardView cv = findCardView(e.getX(), e.getY());
				makeSelectedCard(cv);
				if(currentCard != null) {
					origX = currentCard.getX();
					origY = currentCard.getY();
					lastX = e.getX();
					lastY = e.getY();
					bigCard = currentCard;
				}else{
					bigCard = null;
				}
				//				if (print) System.out.println("press:" + e.getX() + " " + e.getY());
				//
				//				ManModel dotModel = findDot(e.getX(), e.getY());
				//				if (dotModel == null) {	// make a dot if nothing there
				//					dotModel = doAdd(e.getX(), e.getY());
				//				}
				//
				//				// invariant -- dotModel var is now set,
				//				// one way or another
				//
				//				// Note the starting setup to compute deltas later
				//				lastDot = dotModel;
				//				lastX = e.getX();
				//				lastY = e.getY();
				//
				//				// Change color of dot in some cases
				//				// shift -> change to black
				//				// double-click -> change to red
				//				if (e.isShiftDown()) {
				//					doSetColor(dotModel, Color.BLACK);
				//				}
				//				else if (e.getClickCount() == 2) {
				//					doSetColor(dotModel, Color.RED);
				//				}

			}

			public void mouseReleased(MouseEvent e) {
				//				repaintCard(findCardView(e.getX(), e.getY()));
				if(currentCard != null) {
					if(currentDeck != null && isInTrickHand(currentCard) && canInteract()) {
						if(CardGameConstants.ASK_USERS_FRACTION_QS && notCoolMan && (currentCard.isAir() || currentCard.isStink())) {
							launchManipWindow(currentDeck, currentCard);
							cardUsed = currentCard;
							deckUsed = currentDeck;
							cardUsedX = origX;
							cardUsedY = origY;
							repaint();
						}else{
							updateViewWithMove(currentDeck, currentCard);
						}
					}else{
						sendCardBack(currentCard);
						if(!isInTrickHand(currentCard)) {
							makeSelectedCard(null);
						}
					}
				}
				makeSelectedDeck(null, false);
				origX = origY = lastX = lastY = 0;
				dragging = false;
				repaint(0, 0, getWidth(), getHeight());
			}
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(CardGameConstants.DEBUG_MODE){
					System.out.println("mouse dragged");
				}
				dragging = true;
				if (currentCard != null && isInTrickHand(currentCard)) {
					// compute delta from last point
					int dx = e.getX()-lastX;
					int dy = e.getY()-lastY;
					lastX = e.getX();
					lastY = e.getY();

					// apply the delta to that dot model
					moveCard(currentCard, dx, dy);
					DeckView dv = findDeckView(lastX, lastY);
					boolean add = false;
					if(dv != null && !notCoolMan) {
						add = dv.getPlayer().couldAddToPlayDeck(dv, currentCard, currentGame.getHumanPlayer());
					}
					makeSelectedDeck(dv, add);
				}
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
	}

	public void startGame() {
		resetPanel();
		currentGame = new Game(this, this);
		recomputeAllByForce(getPanelSize());
	}

	public boolean gameStarted() {
		return currentGame != null;
	}

	private void resetPanel() {
		cardImages = new ArrayList<CardView>();
		playersTrickCards = new ArrayList<CardView>();
		currentTrickCardsLeft = new ArrayList<CardView>();
		opponentsTrickCardsLeft = new ArrayList<CardView>();
		opponentsTrickCards = new ArrayList<CardView>();
		manWindows = new ArrayList<ManFrame>();
		decksInPlay = new ArrayList<DeckView>();
		currentCard = null;
		currentDeck = null;
		origX = lastX = 0;
		origY = lastY = 0;
		dragging = false;
		curSelOrigIndex = -1;
		addComponentListener(this);
		enableUser();
		revalidate();
	}

	public void newRound() {
		currentGame.playOneRound();
	}

	public void addTeam(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.addExtraTeammate();
	}

	public void addTrick(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.addExtraTrickToHand();
	}

	public void setTrickHandDebug(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		TrickCard t1 = new TrickCard("cards_stink.jpg", 1, 2, "Stink");
		for(int i = 0; i < 4; i++) {
			p.addTrickCardToHand(t1);
		}
	}

	public void setTrickHandToAirs(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		TrickCard t1 = new TrickCard("cards_air_3qtrs.jpg", 3, 4, "Air");
		for(int i = 0; i < 4; i++) {
			p.addTrickCardToHand(t1);
		}
	}

	private Player getCorrectPlayer(boolean isPlayer) {
		if(isPlayer) {
			return currentGame.getHumanPlayer();
		}else{
			return currentGame.getOpposingPlayer();
		}
	}

	public void removeTeam(boolean isForPlayer, int i) {
		if(inRange(decksInPlay, i)) {
			DeckView dv = decksInPlay.get(i);
			removeTeam(isForPlayer, dv);
		}
	}

	public String calculateScoreForRound() {
		String message = currentGame.calculateScore();
		return message;
	}

	public int getOppositionPoints() {
		return currentGame.getOpposingPlayer().getPoints();
	}

	public int getPlayerPoints() {
		return currentGame.getHumanPlayer().getPoints();
	}

	public void removeTeam(boolean isForPlayer, DeckView dv) {
		Player p = getCorrectPlayer(isForPlayer);
		p.removePlayDeckFromHand(dv);
	}

	public void removeTeam(DeckView dv) {
		dv.getPlayer().removePlayDeckFromHand(dv);
	}

	private void removeIceRadiosFromDeck(DeckView dv) {
		ArrayList<CardView> tcs = getIceRadios(dv);
		removeCardsFromImages(tcs);
		dv.getPlayer().removeIceRadiosFromDeck(dv);
	}

	private ArrayList<CardView> getIceRadios(DeckView dv) {
		ArrayList<CardView> tcs = dv.getAllCards();
		ArrayList<CardView> iceRadios = new ArrayList<CardView>();
		for(CardView cv: tcs) {
			if(cv.isRadio() || cv.isIceCream()) {
				iceRadios.add(cv);
			}
		}
		return iceRadios;
	}

	public void removeLatestTeam(boolean isForPlayer) {
		removeTeam(isForPlayer, decksInPlay.size()-1);
	}

	private boolean inRange(ArrayList list, int num) {
		return num < list.size() && num >= 0;
	}

	public void eliminateTrickCompletely(boolean isForPlayer, int i) {
		if(inRange(playersTrickCards, i)) {
			CardView cv = playersTrickCards.get(i);
			eliminateTrickCompletely(isForPlayer, cv);
		}
	}

	public void eliminateLatestTrick(boolean isForPlayer) {
		eliminateTrickCompletely(isForPlayer, playersTrickCards.size()-1);
	}

	private void eliminateTrickCompletely(boolean isForPlayer, CardView cv) {
		Player p = getCorrectPlayer(isForPlayer);
		p.removeTrickFromHand(cv);
		if(cv == currentlySelected()) {
			makeSelectedCard(null);
		}
		playersTrickCards.remove(cv);
		cardImages.remove(cv);
	}

	private void moveCard(CardView card, int dx, int dy) {
		repaintCard(card);
		card.setLocation(card.getX()+dx, card.getY()+dy);
		repaintCard(card);
	}

	private void repaintCard(CardView card) {
		if(!card.isHighlighted()) {
			repaint(card.getX(), card.getY(), (int) card.getSize().getWidth()+1, (int) card.getSize().getHeight()+1);
		}else{
			repaint(card.getX()-2, card.getY()-2, (int) card.getSize().getWidth()+3, (int) card.getSize().getHeight()+3);
		}
	}

	private void repaintDeck(DeckView deck) {
		CardView topCard = deck.getTeammateCard();
		if(!deck.isHighlighted()) {
			repaint(topCard.getX(), topCard.getY(), (int) topCard.getSize().getWidth()+1, (int) deck.calculateDeckHeight()+1);
		}else{
			repaint(topCard.getX()-2, topCard.getY()-2, (int) topCard.getSize().getWidth()+3, (int) deck.calculateDeckHeight()+3);
		}
	}

	private CardView findCardView(int x, int y) {
		CardView onTop = null;
		for(int i = cardImages.size()-1; i >= 0; i--) {
			CardView cv = cardImages.get(i);
			if(cv.withinBounds(x, y) /*&& !cv.getCard().isTeammateCard()*/ && cardOnTopOf(cv, onTop)) {
				onTop = cv;
			}
		}
		return onTop;
	}

	private boolean cardOnTopOf(CardView card, CardView otherCard) {
		if(otherCard != null) {
			return card.getY() > otherCard.getY();
		}
		return card != null;
	}

	private DeckView findDeckView(int x, int y) {
		for(DeckView dv:decksInPlay) {
			ArrayList<CardView> cardDeck = dv.getAllCards();
			for(CardView card:cardDeck) {
				if(card.withinBounds(x, y)) {
					return dv;
				}
			}
		}
		return null;
	}

	private void makeSelectedCard(CardView cv) {
		if(CardGameConstants.DEBUG_MODE) {
			System.out.println("CardView " + cv + " selectable? " + isSelectable(cv));
		}
		if(isSelectable(cv)) {
			if(cv != currentCard && currentCard != null) {
				unSelectCard(currentCard);
				//repaintCard(currentSelection);
			}
			selectCard(cv);
		}else{
			if(currentCard != null) {
				unSelectCard(currentCard);
			}
			cv = null;
		}
		currentCard = cv;
	}

	private void unSelectCard(CardView cv) {
		if(cv != null) {
			cv.setHighlighted(false);
			//setComponentZOrder(cv, cv.getZOrder());
			if(!isTeammateCard(cv)) {
				int index = currentTrickCardsLeft.indexOf(cv);

				if(index != -1) {
					currentTrickCardsLeft.remove(cv);
					currentTrickCardsLeft.add(curSelOrigIndex, cv);
				}else{
					playersTrickCards.remove(cv);
					playersTrickCards.add(curSelOrigIndex, cv);
				}
			}else{
				playersTrickCards.remove(cv);
			}
			repaintCard(cv);
			curSelOrigIndex = -1;
		}

	}

	/* 
	 * Currently trickCards are only being drawn correctly when multiples are on top 
	 * of one Stack.  I need to get it so that it's on both stacks.
	 */
	private void selectCard(CardView cv) {
		if(cv != null) {
			cv.setHighlighted(true);
			if(!isTeammateCard(cv)) {
				ArrayList<CardView> hand = findStackForCard(cv);
				placeCardOnTopOfStack(cv, hand);
			}else{
				playersTrickCards.add(cv);
			}
			//setComponentZOrder(cv, 0);
			repaintCard(cv);
		}
	}

	private ArrayList<CardView> findStackForCard(CardView cv) {
		int index = currentTrickCardsLeft.indexOf(cv);
		if(index != -1) {
			return currentTrickCardsLeft;
		}else{
			index = playersTrickCards.indexOf(cv);
			if(index != -1) {
				return playersTrickCards;
			}else{
				return opponentsTrickCards;
			}
		}
	}

	private void placeCardOnTopOfStack(CardView cv, ArrayList<CardView> hand) {
		curSelOrigIndex = hand.indexOf(cv);
		hand.remove(curSelOrigIndex);
		hand.add(cv);
	}

	private void makeSelectedDeck(DeckView dv, boolean isLegal) {
		if(dv == null) {
			if(currentDeck != null) {
				unSelectDeck(currentDeck);
			}
		}else{
			if(dv != currentDeck && currentDeck != null) {
				unSelectDeck(currentDeck);
			}
			selectDeck(dv, isLegal);
		}
		currentDeck = dv;
	}

	private void unSelectDeck(DeckView dv) {
		dv.setHighlighted(false, false);
		repaintDeck(dv);
	}

	private void selectDeck(DeckView dv, boolean isLegal) {
		dv.setHighlighted(true, isLegal);
		repaintDeck(dv);
	}

	private CardView currentlySelected() {
		return currentCard;
	}

	/* Recomputes the position of the trick cards based on the size of the board and the number of trick cards present */
	/* Later on, I'd like to have something that would also resize the cards should there be too many based on the size
	 * of the window.
	 * Would be something about checking the numCards*width of cards > screensize, setPreferredSize(regularHeight, screensize/numCards
	 */
	private void recomputeTrickHandLocations(ArrayList<CardView> tHand, boolean isPlayer) {
		recomputeTrickHandSizes(tHand, isPlayer);
		recomputeTrickCardCoords(tHand, isPlayer);
		repaint();
	}

	private void recomputeTrickCardCoords(ArrayList<CardView> tHand, boolean isPlayer) {
		Dimension d = getPanelSize();

		int numCards = tHand.size();
		for(int i = 0; i < numCards; i++) {
			CardView cv = tHand.get(i);
			//Need this code to get the correct height at the start of the program, when we don't have heights and widths on the images yet.
			double h = cv.getHeight();
			if(h <= 0) {
				h = cv.getSize().getHeight();
			}
			int x = calculateXCoord(d, i, numCards, cv); 

			if(isPlayer) {
				cv.setLocation(x, (int) (d.getHeight()-h));
			}else{
				cv.setLocation(x, 0/*(int)-h/2*/);
			}
			//System.out.println(cv.getX() + ", " + cv.getY());
		}
	}

	private void recomputeAllTrickCardLocations() {
		recomputeAllTrickHandSizes();
		recomputeTrickCardCoords(currentTrickCardsLeft, true);
		recomputeTrickCardCoords(opponentsTrickCardsLeft, false);
	}

	private void recomputeDeckCards(ArrayList<DeckView> decks) {
		if(decks.size() > 0) {
			recomputeDeckCardSizes(decks);
			Dimension d = getPanelSize();
			//double maxDeckHeight = d.getHeight()-(2*teamCardHeightPlusMargin(decks))-oppoTrickHeight(); 
			double maxDeckHeight = deckHeight();
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("maxDeckHeight: " + maxDeckHeight + ", teamCardNMargin: " + teamCardHeightPlusMargin(decks) + ", totalHeight: " + getPanelSize().getHeight() + ", oppoTrickCard: " + oppoTrickHeight() + ", trickCard: " + playerTrickHeight());
			}
			d.setSize(d.getWidth(), maxDeckHeight);
			int numDecks = decks.size();
			int numDecksHuman = 0;
			int numDecksOppo = 0;
			int pos = 0;
			for(int i = 0; i < numDecks; i++) {
				DeckView dv = decks.get(i);
				int playersDecks = numPlayersDecks(decks, dv.getPlayer());
				if(dv.getPlayer().isHuman()) {
					pos = numDecksHuman++;
				}else{
					pos = numDecksOppo++;
				}
				recomputeDeck(dv, d, playersDecks, pos);
			}
			repaint();
		}
	}

	private double deckHeight() {
		Dimension d = getPanelSize();
		return (d.getHeight() - (oppoTrickHeight() + playerTrickHeight()))/2;
	}

	private double oppoTrickHeight() {
		return getCardHeight(opponentsTrickCardsLeft);
	}

	private double playerTrickHeight() {
		return getCardHeight(currentTrickCardsLeft);
	}

	private double getCardHeight(ArrayList<CardView> cards) {
		if(cards.size() > 0) {
			return cards.get(0).getHeight();
		}
		return 0;
	}

	private void recomputeDeck(DeckView dv, Dimension d, int playersDecks, int pos) {
		ArrayList<CardView> dHand = dv.getAllCards();
		int numCards = dHand.size();
		for(int j = 0; j < numCards; j++) {
			CardView cv = dHand.get(j);
			double h = cv.getHeight();
			int x = calculateXCoord(d, pos, playersDecks, cv);
			int y = (int) (j*Math.max(PLAYDECK_OFFSET, Math.min(h*(numCards-1), d.getHeight()-h))/(numCards-1));
			y += oppoTrickHeight();
			if(dv.getPlayer().isHuman()) {
				//y = y + (int) d.getHeight();
				y += (int) d.getHeight();
			}
			cv.setLocation(x, y);
		}
	}

	private int calculateXCoord(Dimension d, int pos, int numObjects, CardView cv) {
		int margin = (int) (d.getWidth()/numObjects);
		//return Math.max((pos * margin) + (margin/2-cv.getWidth()/2), 0);
		return (pos * margin) + (margin/2-cv.getWidth());
	}

	private int numPlayersDecks(ArrayList<DeckView> decks, Player p) {
		int count = 0;
		for(DeckView dv: decks) {
			if(dv.getPlayer() == p) {
				count++;
			}
		}
		return count;
	}

	private void recomputeAllTrickHandSizes() {
		recomputeTrickHandSizes(currentTrickCardsLeft, true);
		recomputeTrickHandSizes(opponentsTrickCardsLeft, false);
	}

	private void recomputeTrickHandSizes(ArrayList<CardView> hand, boolean isPlayer) {
		if(hand.size() > 0) {
			Dimension d = calculateTrickCardSize(hand);
			if(!isPlayer) {
				d.setSize(d.getWidth()*.5, d.getHeight()*.5);
			}
			setCardSizes(hand, d);
		}
	}

	private void recomputeDeckCardSizes(ArrayList<DeckView> decks) {
		Dimension d = calculateDeckCardSize(decks);
		setDeckSizes(decks, d);
	}

	private void setCardSizes(ArrayList<CardView> hand, Dimension d) {
		for(CardView cv: hand) {
			cv.setSize(d.getWidth(), d.getHeight());
		}
	}

	private void setDeckSizes(ArrayList<DeckView> decks, Dimension d) {
		for(DeckView deck: decks) {
			setCardSizes(deck.getAllCards(), d);
		}
	}

	private Dimension calculateTrickCardSize(ArrayList<CardView> hand) {
		Dimension d = getPanelSize();
		int numCards = hand.size();
		Dimension firstCard = hand.get(0).getSize();
		double width = firstCard.getWidth();
		if((int) d.getWidth() != (int) (width*numCards)) {
			Dimension newD = new Dimension(Math.min((int) d.getWidth()/numCards, CardGameConstants.MAX_CARD_WIDTH), (int) d.getHeight());
			//System.out.println("returning the new dimension" + d);
			return newD;
		}
		//System.out.println("returning the old preferred size" + firstCard + ", with d being " + d);
		return firstCard;
	}

	private Dimension getPanelSize() {
		Dimension d = getSize();
		d.setSize(d.getWidth()-CardGameConstants.HUGE_CARD_WIDTH, d.getHeight());
		return d;
	}

	private Dimension calculateDeckCardSize(ArrayList<DeckView> dvs) {
		Dimension d = getPanelSize();
		int numDecks = dvs.size();
		DeckView dv = mostPopulatedDeck(dvs);
		Dimension firstCard = dv.getTeammateCard().getSize();
		double cardHeight = firstCard.getHeight();
		if(deckCardsShouldBeResized(dv)) {
			int numCards = dv.getAllCards().size();
			double dHeight = deckHeight();
			double desiredDifference = numCards*(cardHeight/10);
			double desiredHeight = dHeight - desiredDifference;
			//System.out.println("cards being resized for height: " + desiredHeight + "current height is: " + cardHeight);
			d.setSize(d.getWidth(), desiredHeight);
			firstCard = d;
		}
		return firstCard;
	}

	private DeckView mostPopulatedDeck(ArrayList<DeckView> dvs) {
		int biggestNumCards = dvs.get(0).getAllCards().size();
		DeckView biggestDeck = dvs.get(0);
		for(DeckView dv: dvs) {
			if(dv.getAllCards().size() > biggestNumCards) {
				biggestDeck = dv;
				biggestNumCards = dv.getAllCards().size();
			}
		}
		return biggestDeck;
	}

	public boolean deckCardsShouldBeResized(DeckView dv) {
		Dimension firstCard = dv.getTeammateCard().getSize();
		double cardHeight = firstCard.getHeight();
		double dHeight = deckHeight();
		int numCards = dv.getAllCards().size();
		//I want to subtract the cardHeight from the deckHeight, since that will give me what is left over
		//That difference divided by the number of cards should give me the displacement each card will have
		double difference = dHeight - cardHeight;
		double displacement = difference / numCards;
		//I want to resize if the displacement is Less than one eighth of the size of the card
		return ((cardHeight > 100) && displacement < cardHeight / 10) || displacement > cardHeight / 8;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintDecks(g);
		paintTrickHand(g);
		paintOpponentTricks(g);
		paintBigCard(g);
	}

	private void paintBigCard(Graphics g) {
		if(bigCard != null) {
			bigCard.drawBigCard(g, getWidth(), getHeight());
		}
	}

	private void paintTrickHand(Graphics g) {
		int i = 0;
		for(CardView cv:currentTrickCardsLeft) {
			cv.setZOrder(i);
			cv.drawCard(g);
			i++;
		}
	}

	private void paintOpponentTricks(Graphics g) {
		for(CardView cv:opponentsTrickCardsLeft) {
			cv.drawCard(g);
		}
	}

	private void paintDecks(Graphics g) {
		for(DeckView dv:decksInPlay) {
			dv.drawDeck(g);
		}
		drawDividingLine(g);
	}
	
	/*
	 * To figure out where the line is, simply add the constant of the team size plus the height of the deck.
	 */
	private void drawDividingLine(Graphics g) {
		int y = 0;
		for(DeckView dv:decksInPlay) {
			if(dv.getPlayer().isHuman()) {
				y = dv.getY()-CardGameConstants.LINE_THICKNESS;
				break;
			}
		}
		drawDashedLine(g, 0, getWidth()-CardGameConstants.HUGE_CARD_WIDTH, y);
	}
	
	private void drawDashedLine(Graphics g, int x1, int x2, int y) {
		int dashLength = (x2-x1)/(CardGameConstants.NUM_DASHES*2);
		for(int i = 0; i < CardGameConstants.NUM_DASHES*2; i++) {
			if(i%2 == 0) {
				for(int j = -CardGameConstants.LINE_THICKNESS/2; j <= CardGameConstants.LINE_THICKNESS; j++) {
					g.drawLine(i*dashLength, y+j, (i+1)*dashLength, y+j);
				}
			}
		}
	}

	public void trickDeckChanged(Player p) {
		ArrayList<CardView> tHand = p.getTrickHand();
		recomputeTrickHandLocations(tHand, isOurUser(p));
	}

	public void trickCardAdded(Player p, CardView cv) {
		cardImages.add(cv);
		if(isOurUser(p)) {
			playersTrickCards.add(cv);
			currentTrickCardsLeft.add(cv);
		}else{
			opponentsTrickCards.add(cv);
			opponentsTrickCardsLeft.add(cv);
			cv.setFaceUp(CardGameConstants.SHOW_COMPUTER_CARDS);
		}
		trickDeckChanged(p);
	}

	public void trickCardRemoved(Player p, CardView cv) {
		if(isOurUser(p)) {
			currentTrickCardsLeft.remove(cv);
		}else{
			opponentsTrickCardsLeft.remove(cv);
			enableUser();
		}
		trickDeckChanged(p);
	}

	public void playDeckChanged(Player p) {
		//need to recompute the playDecks here.
		ArrayList<DeckView> decks = p.getAllDecks();
		recomputeDeckCards(decksInPlay);
	}

	//We might make this a hashmap of arraylists, where we have two arraylists
	public void playDeckAdded(Player p, DeckView pd) {
		for(CardView cv:pd.getAllCards()) {
			cardImages.add(cv);
		}
		decksInPlay.add(pd);
		handleShadowPlayer(p, pd);
		playDeckChanged(p);
	}

	public void cardAnimationLaunched(PossibleMove cMove, Player computer, Player opponent) {
		disableUser();
		status.setText("The computer is thinking about where to move");
		CardView cv = cMove.getTrickCard();
		cv.setFaceUp(true);
		DeckView dv = cMove.getDeck();
		CardMover cm = new CardMover(this, cMove, computer, opponent, cv, dv);
		Timer cardTimer = new Timer(20, cm);
		cardTimer.setInitialDelay(CardGameConstants.BETWEEN_GAME_PAUSE);
		cm.setTimer(cardTimer);
		cardTimer.start();
	}

	private void removeCardsFromImages(DeckView dv) {
		removeCardsFromImages(dv.getAllCards());
	}

	private void removeCardsFromImages(ArrayList<CardView> list) {
		for(CardView cv:list) {
			cardImages.remove(cv);
		}
	}

	public void computerMove() {
		Player p = currentGame.getHumanPlayer();
		ComputerPlayer oppo = (ComputerPlayer) currentGame.getOpposingPlayer();
		if(!oppo.chooseTrickCardToPlay(p)) {
			status.setText("The computer doesn't have any cards they want to play");
		}
	}

	public void playDeckRemoved(Player p, DeckView pd) {
		decksInPlay.remove(pd);
		removeCardsFromImages(pd);
		playDeckChanged(p);
	}

	/** Fixed a bug here where playDecksTossed was removing all of the decks
	 * That is certainly not what we want.  This removes only the particular players
	 * Decks.
	 */
	public void playDecksTossed(Player p) {
		boolean deckChanged = false;
		for(int i = 0; i < decksInPlay.size(); i++) {
			DeckView dv = decksInPlay.get(i);
			if(dv.getPlayer() == p) {
				removeCardsFromImages(dv);
				decksInPlay.remove(dv);
				i--;
				deckChanged = true;
			}
		}
		if(deckChanged) {
			playDeckChanged(p);
		}
	}

	public void trickCardsThrownAway(Player p) {
		if(isOurUser(p)) {
			clearCardList(playersTrickCards);
			currentTrickCardsLeft.clear();
		}else{
			clearCardList(opponentsTrickCards);
			opponentsTrickCardsLeft.clear();
		}

	}

	public void scoreUpdated(Player p) {
		if(panel != null) {
			panel.updateLabels(p);
		}

	}

	public void iceCreamPlayed(Player p, DeckView dv, Player cvOwner) {
		if(p.numRadiosInHand() > 0) {
			if(isOurUser(p)) {
				askForRadios(p, dv);
			}else{
				ComputerPlayer cp = (ComputerPlayer) p;
				cp.playARadio(dv);
			}
		}
		launchPebbleBagView(dv);
	}

	public void stinkBombPlayed(Player p, DeckView dv, Player cvOwner) {
		decideToLaunch(dv, cvOwner);
	}

	public void airFreshenerPlayed(Player p, DeckView dv, Player cvOwner) {
		decideToLaunch(dv, cvOwner);
	}

	private void handleShadowPlayer(Player p, DeckView dv) {
		TeammateCard tc = (TeammateCard) dv.getTeammateCard().getCard();
		if(tc.isShadowPlayer()) {
			//dv.showLabel(false);
			launchManipWindow(tc.getDescription(), dv, null);
		}
	}

	private void decideToLaunch(DeckView dv, Player cvOwner) {
		if(!notCoolMan) {
			launchManipWindow(dv, notCoolMan);
		}else if(!isOurUser(cvOwner)) {  //Launching Manip Window for Computer
			launchCompManipWindow(dv);
		}
	}

	public boolean isOurUser(Player p) {
		return p.isHuman();
	}

	private void askForRadios(Player p, DeckView dv) {
		String[] options = {"No Radios", "One Radio", "Two Radios"};
		String[] oneRadioOptions = new String[2];
		for(int i = 0; i < oneRadioOptions.length; i++) {
			oneRadioOptions[i] = options[i];
		}
		String message = "An Ice Cream Has Been Played, How many Radios would you like to play?";
		int n = 0;
		if(p.numRadiosInHand() > 1) {
			n = JOptionPane.showOptionDialog(this, message, "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		}else{
			n = JOptionPane.showOptionDialog(this, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, oneRadioOptions, oneRadioOptions[1]);
		}
		n = Math.min(n, p.numRadiosInHand());
		for(int i = 0; i < n; i++) {
			p.playARadio(dv);
		}
	}

	private void launchPebbleBagView(DeckView dv) {
		pebbleWindow = new PebbleFrame(dv); //Still needs more work
		pebbleWindow.addBagListener(this);
		disableUser();
	}
	
	private void launchCompManipWindow(DeckView dv) {
		launchManipWindow(dv, null, false);
	}

	private void launchManipWindow(DeckView dv, boolean shouldCalcCards) {
		//PlayDeck pd = dv.getPlayDeck();
		//TrickCard tc = pd.getLastTrickPlaced();
		ArrayList<CardView> allCards = dv.getAllCards();
		launchManipWindow(dv, allCards.get(allCards.size()-1), shouldCalcCards);
	}

	private void launchManipWindow(DeckView dv, CardView cv, boolean shouldCalcAllCards) {
		PlayDeck pd = dv.getPlayDeck();
		String question = "";
		if(cv != null) {
			question = pd.generateQuestion((TrickCard) cv.getCard(), shouldCalcAllCards);
		}else{
			ArrayList<CardView> allCards = dv.getAllCards();
			cv = allCards.get(allCards.size()-1);
			question = pd.generateQuestion((TrickCard) cv.getCard(), shouldCalcAllCards);
			cv = null;
		}
		//int answer = pd.generateAnswer(tc, shouldCalcAllCards);
		launchManipWindow(question, dv, cv);
	}

	private void launchManipWindow(String question, DeckView dv, CardView cv) {
		if(CardGameConstants.ASK_USERS_FRACTION_QS) {
			disableUser();
			//Need to setup a Timer here;
			ManFrameLauncher mfl = new ManFrameLauncher(this, question, manWindows, dv, cv);
			Timer t = new Timer(0, mfl);
			t.setInitialDelay(CardGameConstants.BETWEEN_GAME_PAUSE);
			mfl.setTimer(t);
			t.start();
		}else{
			makeSuccessfulDragChanges();
		}
	}

	private void launchManipWindow(DeckView dv, CardView cv) {
		launchManipWindow(dv, cv, notCoolMan);
	}

	private void addStatusBox() {
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		add(box, BorderLayout.SOUTH);
		status = new JLabel(" ");
		box.add(status);
	}

	private String generateErrorMessage(CardView cv, DeckView dv, Player p) {
		String temp = "";
		String suffix = ".  ";

		TrickCard tc = (TrickCard) cv.getCard();
		if(isOurUser(p)) {
			temp += "own deck";
			if(tc.isDefense()) {
				//More analysis could be done here.
				suffix += dv.getPlayDeck().getErrorMsg();
			}else{
				suffix += "You would be hurting yourself!";
			}
		}else{
			temp += "opponent's deck";
			if(tc.isDefense()) {
				suffix += "You would be protecting your opponent!";
			}else{
				suffix += dv.getPlayDeck().getErrorMsg();
			}
		}
		String prefix = "Sorry but you can't place " + cv + " on your " + temp;
		return prefix + suffix;
	}

	private String generateSuccessMessage(CardView cv, DeckView dv, Player p) {
		return "Nice Move!";
	}

	private void clearCardList(ArrayList<CardView> hand) {
		for(CardView cv:hand) {
			cardImages.remove(cv);
		}
		hand.clear();
	}

	public void enableUser() {
		for(DeckView dv:decksInPlay) {
			dv.showLabel(true);
		}
		if(cardHidden != null) {
			//cardHidden.setVisible(true);
			cardHidden = null;
		}
		userCanInteract = true;
		panel.enableControls();
	}

	public void disableUser() {
		for(DeckView dv:decksInPlay) {
			dv.showLabel(false);
		}
		cardHidden = currentCard;
		if(cardHidden != null) {
			//cardHidden.setVisible(false);
		}
		userCanInteract = false;
		panel.disableControls();
	}

	private boolean canInteract() {
		return userCanInteract;
	}

	private void updateViewWithMove(DeckView dv, CardView cv) {
		Player p = dv.getPlayer();
		String textToShow = "";
		if(p.addToPlayDeck(dv, cv, currentGame.getHumanPlayer())) {
			CardView oldCard = cv;
			makeSelectedCard(null);
			if(isInTrickHand(oldCard)) {
				p = currentGame.getHumanPlayer();
				p.removeTrickFromHand(oldCard);
			}
			textToShow = generateSuccessMessage(cv, dv, p);
		}else{
			textToShow = generateErrorMessage(cv, dv, p);
			sendCardBack(cv);
		}
		status.setText(textToShow);
	}

	private void sendCardBack(CardView cv) {
		repaintCard(cv);
		cv.setLocation(origX, origY);
		//setComponentZOrder(currentSelection, currentSelection.getZOrder());
		repaintCard(cv);
	}

	private boolean isTeammateCard(CardView cv) {
		for(DeckView dv:decksInPlay) {
			CardView tc = dv.getTeammateCard();
			if(tc == cv) {
				return true;
			}
		}
		return false;
	}

	private boolean isInTrickHand(CardView cv) {
		return isInHand(currentTrickCardsLeft, cv);
	}

	private boolean isInOpponentsTrickHand(CardView cv) {
		return isInHand(opponentsTrickCardsLeft, cv);
	}

	private boolean isInHand(ArrayList<CardView> hand, CardView card) {
		return hand.indexOf(card) != -1;
	}

	private boolean isSelectable(CardView cv) {
		if(cv == null) {
			return false;
		}
		boolean isInOpponentHand = isInOpponentsTrickHand(cv);
		return !(isInOpponentHand);
	}

	private int currentCardHeight(ArrayList<DeckView> decks) {
		return decks.get(0).getTeammateCard().getHeight();
	}

	private int teamCardHeightPlusMargin(ArrayList<DeckView> decks) {
		return currentCardHeight(decks)+CardGameConstants.BOARD_MARGIN;
	}

	//Might not be used since we have cardImages, though this should give us all of the players and such.
	private ArrayList<CardView> allCards() {
		ArrayList<CardView> cardsOnBoard = new ArrayList<CardView>();
		for(CardView cv:currentTrickCardsLeft) {
			cardsOnBoard.add(cv);
		}
		for(DeckView dv:decksInPlay) {
			for(CardView cv:dv.getAllCards()) {
				cardsOnBoard.add(cv);
			}
		}
		return cardsOnBoard;
	}


	public static void main(String[] args) {
		JFrame f = new JFrame("GamePanel Test");

		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new BorderLayout());

		GamePanel gp = new GamePanel(600, 950, null, false);
		cont.add(gp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		gp.startGame();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		if(decksInPlay.size() > 0) {
			makeSelectedCard(null);
			Dimension d = arg0.getComponent().getSize();
			d.setSize(d.getWidth()-CardGameConstants.HUGE_CARD_WIDTH, d.getHeight());
			recomputeAllToDimension(d);
		}
	}

	private void recomputeAll() {
		if(decksInPlay.size() > 0) {
			Dimension d = getPanelSize();
			recomputeAllToDimension(d);
		}
	}

	private void recomputeAllToDimension(Dimension d) {
		if(d.getWidth() > (100 + CardGameConstants.HUGE_CARD_WIDTH) && d.getHeight() > (100 + CardGameConstants.HUGE_CARD_HEIGHT)) {
			Dimension ourSize = getPreferredSize();
			if(Math.abs(d.getWidth()-ourSize.getWidth()) > 25 || Math.abs(d.getHeight() - ourSize.getHeight()) > 25) {
				recomputeAllByForce(d);
			}
		}
	}

	private void recomputeAllByForce(Dimension d) {
		setPreferredSize(d);
		recomputeDeckCards(decksInPlay);
		recomputeAllTrickCardLocations();
		repaint();
	}

	private void recomputeAllByForce() {
		recomputeAllByForce(getPanelSize());
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void iceCreamTruckDone(DeckView dv, boolean didKidsRunToTruck) {
		pebbleWindow.dispose();
		enableUser();
		if(didKidsRunToTruck) {
			removeTeam(dv);
		}else{
			removeIceRadiosFromDeck(dv);
		}
		repaint();
	}

	@Override
	public void manipWindowDone(DeckView dv, ManFrame mf) {
		mf.dispose();
		manWindows.remove(mf);
		makeSuccessfulDragChanges();
		if(manWindows.size() == 0) {
			enableUser();
		}
		repaint();
	}

	private void makeSuccessfulDragChanges() {
		if(notCoolMan && cardUsed != null) {
			makeSelectedCard(cardUsed);
			origX = cardUsedX;
			origY = cardUsedY;
			updateViewWithMove(deckUsed, currentCard);
			deckUsed = null;
			cardUsed = null;
		}
	}
}
