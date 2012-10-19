package deck;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pebblebag.TugImages;
import basic.Constants;
import basic.Player;
import cards.Card;
import cards.CardView;
import cards.CardViewFactory;
import cards.ShadowCardView;
import cards.TeammateCard;
import cards.TrickCard;
import extras.Debug;
import extras.GraphicUtils;

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
		labelShown = true;
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

	public ArrayList<CardView> getAllCards() {
		ArrayList<CardView> wholeDeck = (ArrayList<CardView>) cardsPlayed.clone();
		wholeDeck.add(0, teammateCard);
		return wholeDeck;
	}

	public boolean addTrickCard(TrickCard c) {
		boolean couldAddCard = deck.addTrickCard(c);
		if(couldAddCard) {
			CardView cv = CardViewFactory.createCard(c);
			cardsPlayed.add(cv);
		}
		return couldAddCard;
	}

	public boolean addTrickCard(CardView cv) {
		return addTrickCard(cv, (TrickCard) cv.getCard());
	}
	
	public boolean addTrickCard(CardView cv, TrickCard tc) {
		boolean couldAddCard = deck.addTrickCard(tc);
		if(couldAddCard) {
			cardsPlayed.add(cv);
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
		if(isHighlighted()) {
			Color rectColor = Color.red;
			if(isLegalAdd()) {
				rectColor = Color.green;
			}
			drawDeckRectangle(g, rectColor);
		}
		if(labelShown && Constants.SHOW_DECK_MANIPS) {
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
		g.drawString(message, Constants.DECK_VIEW_MARGIN, (int) height-Constants.DECK_VIEW_MARGIN);
		g.setFont(origFont);
		ArrayList<CardView> allCards = getAllCards();
		if(cardPlayed != null) {
			allCards.add(cardPlayed);
		}
		double cardDelta = height/(allCards.size());
		cardDelta = Math.min(cardDelta, Constants.ORIG_CARD_HEIGHT);
		for(int i = 0; i < allCards.size(); i++) {
			CardView cv = allCards.get(i);
			cv.drawCard(g, 0, (int) (i*cardDelta), (int) width, (int) height);
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

	private void drawManipulatives(Graphics g) {
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
		if(numOnSide != 0) {
			int numTotal = deck.initialDeckValue();
			int numSoFar = 0;
			int numLeft = calculateDeck();
			int[] stinkAirs = deck.calculateStinksAndAirsSeparately();
			stinkAirs[1] = Math.min(numTotal, stinkAirs[1]);
			for(; numSoFar < numTotal-(stinkAirs[0]+stinkAirs[1]); numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), manip);
			}
			for(; numSoFar < numTotal-stinkAirs[0]; numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), freshManip);
			}
			for(; numSoFar < numTotal; numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, (int) (stinkManip.getWidth()*Constants.MANIP_SCALE), (int) (stinkManip.getHeight()*Constants.MANIP_SCALE), stinkManip);
			}
		}
	}
	
	private void drawOneMan(Graphics g, int x, int y, int numSoFar, int numOnSide, BufferedImage manImage) {
		drawOneMan(g, x, y, numSoFar, numOnSide, manImage.getWidth(), manImage.getHeight(), manImage);
	}
	
	private void drawOneMan(Graphics g, int x, int y, int numSoFar, int numOnSide, int width, int height, BufferedImage manImage) {
		int manX = x + (numSoFar%numOnSide)*width;
		int manY = y + (numSoFar/numOnSide)*height;
		//Debug.println("Drawing men at " + manX + ", " + manY + "with dimensions: " + manImage.getWidth() + ", " + manImage.getHeight());
		g.drawImage(manImage, manX, manY, width, height, null);
	}

	private int getCardWidth() {
		return (int) getTeammateCard().getSize().getWidth();
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

	public void showLabel(boolean lS) {
		labelShown = lS;
	}
}
