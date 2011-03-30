package basic;
/*
 * This is going to be the main file that will be dealing with all of the views for the cards.  It will be a heavy file
 * that will contain all of the game states including the decks, the players hand, and so on.
 * 
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import manipulatives.ManCardPanel;
import manipulatives.ManFrame;
import manipulatives.ManListener;
import network.NetDelegate;
import network.NetHelper;
import pebblebag.IceWindowListener;
import pebblebag.PebbleFrame;
import cards.CardView;
import cards.TeammateCard;
import cards.TeammateCardFactory;
import cards.TrickCard;
import cards.TrickCardFactory;

import combo.ComboFrame;
import combo.ComboListener;

import deck.DeckView;
import deck.PlayDeck;
import extras.Debug;
import extras.PanelListener;
import extras.RandomGenerator;

public class GamePanel extends JPanel implements PlayerListener, ComponentListener, IceWindowListener, ManListener, ComboListener {

	/* Panel will have
	 * opponent's decks in play up top
	 * person's decks in play down below
	 * peson's hand of trick cards below that
	 */
	/* Instance Variables */
	private List<CardView> playersTrickCards; //Represents the initial hand of trick cards
	private List<CardView> currentTrickCardsLeft; //Represents the current cards that haven't been played yet
	private List<CardView> opponentsTrickCardsLeft; //Represents trick cards for computer.
	private List<CardView> opponentsTrickCards; //Represents the initial hand of trick cards for the opponent
	private Game currentGame;
	private ArrayList<CardView> cardImages;
	private List<DeckView> decksInPlay;
	private ArrayList<ManCardPanel> manWindows;

	private CardView currentCard;
	private CardView bigCard;
	private CardView cardHidden;
	private CardView cardUsed;
	private TrickCard origComboTrick;
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
	private int numRadiosInPlay;
	private JLabel status;
	private JPanel statusBox;
	private PebbleFrame pebbleWindow;
	private PanelListener panel;
	private boolean myTurn;
	private NetDelegate netRep;
	private Semaphore timeToDrawPebs;
	private RandomGenerator rgen;
	private boolean firstPlayerTurn;
	private String title;

	private static final int PLAYDECK_OFFSET = 60;
	//private static final int MINIMUM_CARD_DISPLACEMENT = 15;


	//Different booleans for maybe future settable options
	public GamePanel(int w, int h, PanelListener ls, NetDelegate nRep) {
		setPreferredSize(new Dimension(w, h));
		setOpaque(true);
		setBackground(Constants.GAME_BACKGROUND);
		setLayout(new BorderLayout(10, 10));
		panel = ls;
		netRep = nRep;
		timeToDrawPebs = new Semaphore(0, true);
		rgen = RandomGenerator.getInstance();
		
		firstPlayerTurn = rgen.nextBoolean();
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
			}

			public void mouseReleased(MouseEvent e) {
				//				repaintCard(findCardView(e.getX(), e.getY()));
				if(currentCard != null) {
					//have a function that gives the user a chance to select which side of the card...
					
					if(currentDeck != null && isInTrickHand(currentCard) && canInteract()) {
						if(currentCard.isCombo()) {
							launchComboFrame(currentCard);
							return;
						}
						launchWindowOrTryToPlaceCard();
					}else{
						sendCardBack(currentCard);
						if(!isInTrickHand(currentCard)) {
							makeSelectedCard(null);
						}
					}
				}
				resetToZero();
			}
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
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
					if(dv != null && !Constants.REGULAR_MODE) {  //This is for calculating the green squares
						if(currentCard.isCombo()) { //Since it's a green...Just have it try to be added on both choices
							ArrayList<TrickCard> options = currentCard.getCards();
							Player curHuman = currentGame.getHumanPlayer();
							for(TrickCard option:options) {
								add = dv.getPlayer().couldAddToPlayDeck(dv, option, curHuman);
								if(add) {
									break;
								}
							}
						} else {
							add = dv.getPlayer().couldAddToPlayDeck(dv, currentCard, currentGame.getHumanPlayer());
						}
					}
					makeSelectedDeck(dv, add);
				}
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
	}
	
	private void resetToZero() {
		makeSelectedDeck(null, false);
		origX = origY = lastX = lastY = 0;
		dragging = false;
		repaint(0, 0, getWidth(), getHeight());
	}
	
	private void launchWindowOrTryToPlaceCard() {
		if(Constants.ASK_USERS_FRACTION_QS && (currentCard.isAir() || currentCard.isStink())) {
			launchManipWindow(currentDeck, currentCard);
			cardUsed = currentCard;
			deckUsed = currentDeck;
			cardUsedX = origX;
			cardUsedY = origY;
			repaint();
		}else{
			tryAndPlaceCardOnDeck(currentDeck, currentCard);
		}
	}
	
	private void tryAndPlaceCardOnDeck(DeckView dv, CardView cv) {
		Player p = dv.getPlayer();
		String textToShow = "";
		String msgText = status.getText();
		int deckIndex = p.indexOfDeck(dv);
		int cardIndex = getTrickIndex(cv);
		Debug.println("deck: " + deckIndex + "; card: " + cardIndex);
		boolean addedToDeck = p.addToPlayDeck(dv, cv, currentGame.getHumanPlayer());
		if(addedToDeck) {
			CardView oldCard = cv;
			makeSelectedCard(null);
			if(isInTrickHand(oldCard)) {
				p = currentGame.getHumanPlayer();
				p.removeTrickFromHand(oldCard);
				if(Constants.NETWORK_MODE) {
					if(cv.isCombo()) {
						NetHelper.sendNetComboMove(netRep, cardIndex, deckIndex, cv.getOptionChosen());
					}else{
						NetHelper.sendNetMove(netRep, cardIndex, deckIndex);	
					}
				}
			}
			textToShow = status.getText();
			if(msgText.equals(textToShow)) {
				textToShow = generateSuccessMessage(cv, dv, p);
			}
			firstPlayerTurn = !firstPlayerTurn;
		}else{
			textToShow = generateErrorMessage(cv, dv, p);
			sendCardBack(cv);
			if(cv.isCombo()) {
				cv.resetOption();
			}
		}
		setTurn(!addedToDeck);
		status.setText(textToShow);
	}
	
	public int getTrickIndex(CardView cv) {
		int ind = 0;
		int cardX = origX;
		for(CardView otherc:currentTrickCardsLeft) {
			if(!cv.equals(otherc)) {
				int otherX = otherc.getX();
				if(cardX > otherX) {
					ind++;
				}
			}
		}
		return ind;
	}

	public void startGame(boolean againstComputer) {
		resetPanel();
		status.setText(Constants.STATUS_DECIDING_WHOS_FIRST);
		currentGame = new Game(this, this, againstComputer);
		recomputeAllByForce(getPanelSize());
		setTurn(againstComputer);
	}

	public boolean gameStarted() {
		return currentGame != null;
	}

	private void resetPanel() {
		cardImages = new ArrayList<CardView>();
		playersTrickCards = Collections.synchronizedList(new ArrayList<CardView>());
		currentTrickCardsLeft = Collections.synchronizedList(new ArrayList<CardView>());
		opponentsTrickCardsLeft = Collections.synchronizedList(new ArrayList<CardView>());
		opponentsTrickCards = Collections.synchronizedList(new ArrayList<CardView>());
		manWindows = new ArrayList<ManCardPanel>();
		decksInPlay = Collections.synchronizedList(new ArrayList<DeckView>());
		currentCard = null;
		currentDeck = null;
		origX = lastX = 0;
		origY = lastY = 0;
		dragging = false;
		curSelOrigIndex = -1;
		addComponentListener(this);
		myTurn = false;
		
		//enableUser();
		revalidate();
	}

	public void newRound() {
		status.setText(Constants.STATUS_NEW_ROUND);
		if(Constants.NETWORK_MODE) {
			currentGame.clearTheRound();
			setTurn(false);
		}else{
			currentGame.playOneRound();
		}
	}

	public void addTeam(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.addExtraTeammate();
	}

	public void addTrick(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.addExtraTrickToHand();
	}
	
	public void addTricksToHand(ArrayList<String> ts, boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		for(String s:ts) {
			addTrickToHand(s, isForPlayer);
		}
	}
	
	public void addTeamsToHand(ArrayList<String> ts, boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearDecks();
		for(String s:ts) {
			addTeamToHand(s, isForPlayer);
		}
	}
	
	public void addTrickToHand(String s, boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		TrickCard tc = TrickCardFactory.createCard(s);
		p.addTrickCardToHand(tc);
	}
	
	public void addTeamToHand(String s, boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		TeammateCard tc = TeammateCardFactory.createCard(s);
		p.addTeammateCard(tc);
	}
	
	public void clearHand(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		repaint();
	}
	
	public void clearTeam(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearDecks();
		repaint();
	}

	public void setTrickHandDebug(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		TrickCard t1 = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 2, "Stink");
		for(int i = 0; i < 4; i++) {
			p.addTrickCardToHand(t1);
		}
	}

	public void setTrickHandToAirs(boolean isForPlayer) {
		Player p = getCorrectPlayer(isForPlayer);
		p.clearTrickHand();
		TrickCard t1 = new TrickCard(Constants.THREE_QTRS_AIR_FILENAME, 3, 4, "Air");
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

	private boolean inRange(List list, int num) {
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
		Debug.println("CardView " + cv + " selectable? " + isSelectable(cv));
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
				List<CardView> hand = findStackForCard(cv);
				placeCardOnTopOfStack(cv, hand);
			}else{
				playersTrickCards.add(cv);
			}
			//setComponentZOrder(cv, 0);
			repaintCard(cv);
		}
	}

	private List<CardView> findStackForCard(CardView cv) {
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

	private void placeCardOnTopOfStack(CardView cv, List<CardView> hand) {
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
	private void recomputeTrickHandLocations(List<CardView> tHand, boolean isPlayer) {
		recomputeTrickHandSizes(tHand, isPlayer);
		recomputeTrickCardCoords(tHand, isPlayer);
		repaint();
	}

	private void recomputeTrickCardCoords(List<CardView> tHand, boolean isPlayer) {
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
			//Debug.println(cv.getX() + ", " + cv.getY());
		}
	}

	private void recomputeAllTrickCardLocations() {
		recomputeAllTrickHandSizes();
		recomputeTrickCardCoords(currentTrickCardsLeft, true);
		recomputeTrickCardCoords(opponentsTrickCardsLeft, false);
	}

	private void recomputeDeckCards(List<DeckView> decks) {
		if(decks.size() > 0) {
			recomputeDeckCardSizes(decks);
			Dimension d = getPanelSize();
			//double maxDeckHeight = d.getHeight()-(2*teamCardHeightPlusMargin(decks))-oppoTrickHeight(); 
			double maxDeckHeight = deckHeight();
			Debug.println("maxDeckHeight: " + maxDeckHeight + ", teamCardNMargin: " + teamCardHeightPlusMargin(decks) + ", totalHeight: " + getPanelSize().getHeight() + ", oppoTrickCard: " + oppoTrickHeight() + ", trickCard: " + playerTrickHeight());
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

	private double getCardHeight(List<CardView> cards) {
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
		return (pos * margin) + (margin/2-cv.getWidth()/2);
	}

	private int numPlayersDecks(List<DeckView> decks, Player p) {
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

	private void recomputeTrickHandSizes(List<CardView> hand, boolean isPlayer) {
		if(hand.size() > 0) {
			Dimension d = calculateTrickCardSize(hand);
			if(!isPlayer) {
				d.setSize(d.getWidth()*.5, d.getHeight()*.5);
			}
			setCardSizes(hand, d);
		}
	}

	private void recomputeDeckCardSizes(List<DeckView> decks) {
		Dimension d = calculateDeckCardSize(decks);
		setDeckSizes(decks, d);
	}

	private void setCardSizes(List<CardView> hand, Dimension d) {
		for(CardView cv: hand) {
			cv.setSize(d.getWidth(), d.getHeight());
		}
	}

	private void setDeckSizes(List<DeckView> decks, Dimension d) {
		for(DeckView deck: decks) {
			setCardSizes(deck.getAllCards(), d);
		}
	}

	private Dimension calculateTrickCardSize(List<CardView> hand) {
		Dimension d = getPanelSize();
		int numCards = hand.size();
		Dimension firstCard = hand.get(0).getSize();
		double width = firstCard.getWidth();
		if((int) d.getWidth() != (int) (width*numCards)) {
			Dimension newD = new Dimension(Math.min((int) d.getWidth()/numCards, Constants.MAX_CARD_WIDTH), (int) d.getHeight());
			//Debug.println("returning the new dimension" + d);
			return newD;
		}
		//Debug.println("returning the old preferred size" + firstCard + ", with d being " + d);
		return firstCard;
	}

	private Dimension getPanelSize() {
		Dimension d = getSize();
		d.setSize(d.getWidth()-Constants.HUGE_CARD_WIDTH, d.getHeight());
		return d;
	}

	private Dimension calculateDeckCardSize(List<DeckView> dvs) {
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
			//Debug.println("cards being resized for height: " + desiredHeight + "current height is: " + cardHeight);
			d.setSize(d.getWidth(), desiredHeight);
			firstCard = d;
		}
		return firstCard;
	}

	private DeckView mostPopulatedDeck(List<DeckView> dvs) {
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
		Helpers.acquireSem(Helpers.cardDist);
		paintDecks(g);
		paintTrickHand(g);
		paintOpponentTricks(g);
		paintBigCard(g);
		Helpers.releaseSem(Helpers.cardDist);
	}

	private void paintBigCard(Graphics g) {
		if(bigCard != null) {
			bigCard.drawBigCard(g, getWidth(), getHeight());
		}
	}

	private void paintTrickHand(Graphics g) {
		int i = 0;
		for(Iterator<CardView> iter = currentTrickCardsLeft.iterator(); iter.hasNext();) {
			CardView cv = iter.next();
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
				y = dv.getY()-Constants.LINE_THICKNESS;
				break;
			}
		}
		drawDashedLine(g, 0, getWidth()-Constants.HUGE_CARD_WIDTH, y);
	}
	
	private void drawDashedLine(Graphics g, int x1, int x2, int y) {
		int dashLength = (x2-x1)/(Constants.NUM_DASHES*2);
		for(int i = 0; i < Constants.NUM_DASHES*2; i++) {
			if(i%2 == 0) {
				for(int j = -Constants.LINE_THICKNESS/2; j <= Constants.LINE_THICKNESS; j++) {
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
			cv.setFaceUp(Constants.SHOW_COMPUTER_CARDS);
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

	public void cardAnimationLaunched(PossibleMove cMove, Player computer, Player opponent, String message) {
		String prefix = "";
		if(isOurUser(computer)) {
			prefix = Constants.YOU_ARE;
		}else{
			prefix = Constants.YOUR_OPPO_IS;
			panel.opponentTurn();
		}
		status.setText(prefix + message);
		repaint();
		disableUser();
		CardView cv = cMove.getCardView();
		//cv.setFaceUp(true);
		DeckView dv = cMove.getDeck();
		CardMover cm = new CardMover(this, cMove, computer, opponent, cv, dv);
		Timer cardTimer = new Timer(20, cm);
		cardTimer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
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

	public boolean computerMove() {
		boolean computerHadMove = true;
		Player p = currentGame.getHumanPlayer();
		if(!Constants.NETWORK_MODE) {
			ComputerPlayer oppo = (ComputerPlayer) currentGame.getOpposingPlayer();
			if(!oppo.chooseTrickCardToPlay(p)) {
				status.setText(Constants.STATUS_OPPO_NO_MOVES);
				computerHadMove = false;
			}
		}else{
			setTurn(false);
		}
		return computerHadMove;
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
	
	public void oppoUsedRadios(int numRadios) {
		Player p = currentGame.getOpposingPlayer();
		numRadiosInPlay = numRadios;
		if(numRadiosInPlay > 0) {
			p.playARadio(deckUsed);
		}
		deckUsed = null;
	}

	public void iceCreamPlayed(Player p, DeckView dv, Player cvOwner) {
		int numRadios = 0;
		if(p.numRadiosInHand() > 0) {
			if(isOurUser(p)) {
				numRadios = askForRadios(p, dv);
			}else{
				if(Constants.NETWORK_MODE) {
					/* This is the condition where an ice cream has been played but we have to wait on the other */
					deckUsed = dv;
					updateStatus("Waiting to see if your opponent will play a radio");
				} else {
					ComputerPlayer cp = (ComputerPlayer) p;
					numRadiosInPlay = 1;
					cp.playARadio(dv);
				}
				
			}
		}else{
			launchPebbleBagView(dv);
		}
		if(isOurUser(p)) {
			NetHelper.sendNetRadios(netRep, numRadios);
		}
	}
	
	public void radioPlayed(Player p, DeckView dv, Player cvOwner) {
		numRadiosInPlay--;
		if(numRadiosInPlay <= 0) {
			launchPebbleBagView(dv);
		}else{
			cvOwner.playARadio(dv);
		}
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
		if(!isOurUser(cvOwner)) {  //Launching Manip Window for Computer
			launchCompManipWindow(dv);
		}
	}
	
	private void decideToLaunchPebbleBag(DeckView dv) {
		
	}

	public boolean isOurUser(Player p) {
		return p.isHuman();
	}

	private int askForRadios(Player p, DeckView dv) {
		String[] oneRadioOptions = new String[2];
		for(int i = 0; i < oneRadioOptions.length; i++) {
			oneRadioOptions[i] = Constants.RADIO_OPTIONS[i];
		}
		String message = Constants.ASK_FOR_RADIOS;
		int n = 0;
		if(p.numRadiosInHand() > 1) {
			n = JOptionPane.showOptionDialog(this, message, "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, Constants.RADIO_OPTIONS, Constants.RADIO_OPTIONS[2]);
		}else{
			n = JOptionPane.showOptionDialog(this, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, oneRadioOptions, oneRadioOptions[1]);
		}
		numRadiosInPlay = Math.min(n, p.numRadiosInHand());
		if(n > 0) {
			p.playARadio(dv);
		}else{
			launchPebbleBagView(dv);
		}
		return n;
	}

	private void launchPebbleBagView(DeckView dv) {
		disableUser();
		String subject = "their";
		if(dv.getPlayer().isHuman()) {
			subject = "your";
		}
		status.setText(Constants.STATUS_FIB_FIGURE_PREFIX + subject + Constants.STATUS_FIB_FIGURE_MID +Constants.ICE_CREAM_NAME+ Constants.STATUS_FIB_FIGURE_SUFFIX);
		PebbleFrameLauncher pfl = new PebbleFrameLauncher(this, dv, netRep);
		Timer t = new Timer(0, pfl);
		t.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		pfl.setTimer(t);
		t.start();
	}
	
	private void launchComboFrame(CardView cv) {
		disableUser();
		ComboFrame combo = new ComboFrame(cv);
		combo.addListener(this);
	}
	
	public void setPebbleWindow(PebbleFrame pebWin) {
		pebbleWindow = pebWin;
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
		if(Constants.ASK_USERS_FRACTION_QS) {
			disableUser();
			//Need to setup a Timer here;
			status.setText(Constants.STATUS_CALC_FRACTION);
			ManFrameLauncher mfl = new ManFrameLauncher(this, question, manWindows, dv, cv);
			Timer t = new Timer(0, mfl);
			t.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
			mfl.setTimer(t);
			t.start();
		}else{
			makeSuccessfulDragChanges();
		}
	}

	private void launchManipWindow(DeckView dv, CardView cv) {
		launchManipWindow(dv, cv, true);
	}

	private void addStatusBox() {
		statusBox = new JPanel();
		statusBox.setBackground(Constants.STATUS_BACKGROUND);
		statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.X_AXIS));
		statusBox.add(Box.createHorizontalGlue());
		add(statusBox, BorderLayout.NORTH);
		status = new JLabel(" ");
		status.setForeground(Constants.STATUS_FOREGROUND);
		status.setFont(Constants.FONT_SMALL);
		statusBox.add(status);
		statusBox.add(Box.createHorizontalGlue());
		
	}

	private String generateErrorMessage(CardView cv, DeckView dv, Player p) {
		String temp = "";
		String suffix = ".  ";

		TrickCard tc = (TrickCard) cv.getCard();
		if(isOurUser(p)) {
			temp += Constants.STATUS_OWN_DECK;
			if(tc.isDefense()) {
				//More analysis could be done here.
				suffix += dv.getPlayDeck().getErrorMsg();
			}else{
				suffix += Constants.ERROR_HURTING_YOURSELF;
			}
		}else{
			temp += Constants.STATUS_OPPO_DECK;
			if(tc.isDefense()) {
				suffix += Constants.ERROR_PROTECT_OTHER;
			}else{
				suffix += dv.getPlayDeck().getErrorMsg();
			}
		}
		String prefix = Constants.ERROR_CANT_PLACE_PREFIX + cv + Constants.ERROR_CANT_PLACE_ON + temp;
		return prefix + suffix;
	}

	private String generateSuccessMessage(CardView cv, DeckView dv, Player p) {
		return Constants.STATUS_NICE_MOVE;
	}

	private void clearCardList(List<CardView> playersTrickCards2) {
		for(CardView cv:playersTrickCards2) {
			cardImages.remove(cv);
		}
		playersTrickCards2.clear();
	}

	public void enableUser() {
		if(!myTurn || manWindows.size() > 0) {
			Debug.println("Returning from enableUser prematurely: myTurn: " + myTurn + ", windowSize " + manWindows.size());
			return;
		}
		showDeckExtras(true);
		if(cardHidden != null) {
			//cardHidden.setVisible(true);
			cardHidden = null;
		}
		status.setText(decideTurn(true));
		userCanInteract = true;
		panel.enableControls();
	}
	
	private String decideTurn(boolean ourTurn) {
		if(!ourTurn) {
			return Constants.STATUS_OPPO_TURN;
		}
		return parsePlayerName() + "'s " + Constants.STATUS_TURN;
	}
	
	private String parsePlayerName() {
		if(title == null || title.equals("")) {
			return "Your ";
		}
		int andIndex = title.indexOf("&");
		if(andIndex == -1) {
			return title;
		}
		if(firstPlayerTurn) {
			return title.substring(0, andIndex - 1);
		}
		return title.substring(andIndex + 2);
	}
	
	public void updateStatus(String s) {
		status.setText(s);
	}
	
	public JLabel getStatusBox() {
		return status;
	}
	
	public JPanel getStatusArea() {
		return statusBox;
	}
	
	public void setTurn(boolean t) {
		myTurn = t;
		if(t) {
			enableUser();
		}else{
			disableUser();
			showDeckExtras(true);
			status.setText(Constants.STATUS_OPPO_TURN);
			panel.opponentTurn();
		}
		repaint();
	}

	public void disableUser() {
		showDeckExtras(false);
		cardHidden = currentCard;
		if(cardHidden != null) {
			//cardHidden.setVisible(false);
		}
		userCanInteract = false;
		panel.disableControls();
	}
	
	private void showDeckExtras(boolean visible) {
		for(DeckView dv:decksInPlay) {
			dv.showLabel(visible);
		}
	}

	private boolean canInteract() {
		return userCanInteract;
	}

	private void sendCardBack(CardView cv) {
		repaintCard(cv);
		if(origComboTrick != null && cv.isCombo() && isInTrickHand(cv)) {
			cv.setCard(origComboTrick);
			origComboTrick = null;
		}
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

	private boolean isInHand(List<CardView> hand, CardView card) {
		return hand.indexOf(card) != -1;
	}

	private boolean isSelectable(CardView cv) {
		if(cv == null) {
			return false;
		}
		boolean isInOpponentHand = isInOpponentsTrickHand(cv);
		return !(isInOpponentHand);
	}

	private int currentCardHeight(List<DeckView> decks) {
		return decks.get(0).getTeammateCard().getHeight();
	}

	private int teamCardHeightPlusMargin(List<DeckView> decks) {
		return currentCardHeight(decks)+Constants.BOARD_MARGIN;
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

		GamePanel gp = new GamePanel(600, 950, null, null);
		cont.add(gp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		gp.startGame(true);
	}

	//@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		if(decksInPlay.size() > 0) {
			makeSelectedCard(null);
			Dimension d = arg0.getComponent().getSize();
			d.setSize(d.getWidth()-Constants.HUGE_CARD_WIDTH, d.getHeight());
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
		if(d.getWidth() > (100 + Constants.HUGE_CARD_WIDTH) && d.getHeight() > (100 + Constants.HUGE_CARD_HEIGHT)) {
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

	//@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void iceCreamTruckDone(DeckView dv, boolean didKidsRunToTruck) {
		pebbleWindow.dispose();
		boolean ourTurn = false;
		if(Constants.NETWORK_MODE) {
			if(isOurUser(dv.getPlayer())){
				enableUser();
				ourTurn = true;
			}
		}else{
			enableUser();
		}
		String msg = "";
		if(didKidsRunToTruck) {
			removeTeam(dv);
			msg = Constants.STATUS_FALL;
		}else{
			removeIceRadiosFromDeck(dv);
			msg = Constants.STATUS_NO_FALL;
		}
		showDeckExtras(true);
		status.setText(Constants.STATUS_THEY + msg + Constants.STATUS_FOR_THE + Constants.ICE_CREAM_NAME + Constants.STATUS_FIB + Constants.SENTENCE_SEP + decideTurn(ourTurn));
		repaint();
	}

	//@Override
	public void manipWindowDone(DeckView dv, ManFrame mf) {
		mf.dispose();
		manWindows.remove(mf);
		makeSuccessfulDragChanges();
		if(manWindows.size() == 0) {
			//TODO: This one is tricky because shadow players trick cards and stink bombs all go through here.
			tidyUpCalculationClosing();
		}
		repaint();
	}
	
	public void manipPanelClosed(ManCardPanel mcp) {
		manWindows.remove(mcp);
		panel.manViewDone(mcp);
		makeSuccessfulDragChanges();
		if(manWindows.size() == 0) {
			tidyUpCalculationClosing();
		}
		repaint();
	}
	
	public void toggleManipView() {
		panel.toggleManipView();
	}
	
	public void tidyUpCalculationClosing() {
		boolean shouldChangeText = status != null && (status.getText().length() < 5 || !status.getText().substring(0, Constants.ERROR_SORRY.length()).equals(Constants.ERROR_SORRY));
		String formerText = status.getText();
		System.out.println(shouldChangeText + ", " + status.getText());
		if(isMyTurn()) {
			enableUser();
			Debug.println("enabling user");
		}
		if(shouldChangeText) {
			if(isMyTurn()) {
				status.setText(Constants.CORRECT + decideTurn(true));
			}else{
				status.setText(Constants.STATUS_RIGHT_OPPO_TURN);
			}
		}else{
			status.setText(formerText);
		}
		showDeckExtras(true);
	}
	
	public boolean isMyTurn() {
		return myTurn;
	}
	
	public void comboCardDone(CardView cv, int option) {
		ArrayList<TrickCard> optList = cv.getCards();
//		int option = JOptionPane.showOptionDialog(getTopLevelAncestor(), "Which Part of the Combo Card would you like?", "", 0, 0, null, options, options[0]);
		cv.setOptionChosen(option);
		TrickCard cardSelected = optList.get(option);
		origComboTrick = (TrickCard) cv.getCard();
		currentCard.setCard(cardSelected);
		launchWindowOrTryToPlaceCard();
		resetToZero();
		//Only happens with user, so this will go away
		//enableUser();
	}

	private void makeSuccessfulDragChanges() {
		if(/*notCoolMan && */cardUsed != null) {
			makeSelectedCard(cardUsed);
			origX = cardUsedX;
			origY = cardUsedY;
			tryAndPlaceCardOnDeck(deckUsed, currentCard);
			deckUsed = null;
			cardUsed = null;
		}
	}

	public void handleNetworkMove(String cardIndex, int deckIndex) {
		Player p = getCorrectPlayer(false);
		p.assembleMove(cardIndex, deckIndex, getCorrectPlayer(true));
	}
	
	public void timeToDrawPebbles() {
		boolean didIt = false;
		try{
			timeToDrawPebs.acquire();
			pebbleWindow.pebbleDrawingTime();
			didIt = true;
		}catch(InterruptedException e) {
			if(didIt) timeToDrawPebs.release();
		}
	}

	public void timeWhenBagShaking() {
		timeToDrawPebs.release();
	}
	
	public void titleUpdated(String s) {
		title = s;
	}
	
	public PanelListener getPanelListener() {
		return panel;
	}
}
