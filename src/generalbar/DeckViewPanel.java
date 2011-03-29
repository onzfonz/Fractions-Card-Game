package generalbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import pebblebag.PebbleImages;
import basic.Constants;
import basic.Player;
import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;
import deck.PlayDeck;

/* 
 * This is using a bit more of the CardViewPanel but will be using a JLayeredPane
 */

public class DeckViewPanel extends JPanel {
	protected ArrayList<CardViewPanel> cardsPlayed;
	protected CardViewPanel teammateCard;
	protected PlayDeck deck;
	protected Player player;
	protected boolean legalAdd;
	protected boolean labelShown;
	
	private boolean highlighted;

	public DeckViewPanel(TeammateCard c, Player p) {
		super();
		player = p;
		teammateCard = CardViewPanelFactory.createCard(c);
		if(c.isShadowPlayer()) {
			//TODO: need to introduce ShadowCardViewPanel
			//teammateCard = new ShadowCardView(c);
		}
		cardsPlayed = new ArrayList<CardViewPanel>();
		deck = new PlayDeck(c);
		highlighted = false;
		labelShown = true;
		addCard(teammateCard);
		
		setLayout(null);
		setOpaque(true);
		setBackground(Color.black);
		revalidate();
	}

	public DeckViewPanel(PlayDeck pd, Player p) {
		this(pd.getTeammateCard(), p);
		ArrayList<Card> cards = pd.getAllCards();
		for(int i = 1; i < cards.size(); i++) {
			Card c = cards.get(i);
			CardViewPanel cv = CardViewPanelFactory.createCard(c);
			addCard(cv);
		}
	}

	public ArrayList<CardViewPanel> getAllCards() {
		ArrayList<CardViewPanel> wholeDeck = (ArrayList<CardViewPanel>) cardsPlayed.clone();
		wholeDeck.add(0, teammateCard);
		return wholeDeck;
	}

	public boolean addTrickCard(TrickCard c) {
		boolean couldAddCard = deck.addTrickCard(c);
		if(couldAddCard) {
			CardViewPanel cv = CardViewPanelFactory.createCard(c);
			addCard(cv);
		}
		return couldAddCard;
	}

	public boolean addTrickCard(CardViewPanel cv) {
		return addTrickCard(cv, (TrickCard) cv.getCard());
	}
	
	public boolean addTrickCard(CardViewPanel cv, TrickCard tc) {
		boolean couldAddCard = deck.addTrickCard(tc);
		if(couldAddCard) {
			addCard(cv);
		}
		return couldAddCard;
	}
	
	private void addCard(CardViewPanel cv) {
		cardsPlayed.add(cv);
		add(cv);
		cv.setBounds(cv.getX(), cv.getY()+cardsPlayed.size()*20, Constants.ORIG_CARD_WIDTH, Constants.ORIG_CARD_HEIGHT);
		cv.setZOrder(10-cardsPlayed.size());
		setPreferredSize(new Dimension(cv.getWidth(), calculateDeckHeight()));
	}
	
	private void removeCard(CardViewPanel cv) {
		cardsPlayed.remove(cv);
		remove(cv);
	}

	public boolean removeTrickCard(CardViewPanel cv) {
		boolean couldRemoveCard = deck.removeTrickCard((TrickCard) cv.getCard());
		if(couldRemoveCard) {
			removeCard(cv);
		}
		return couldRemoveCard;
	}

	public boolean couldAddTrickCard(CardViewPanel cv) {
		return couldAddTrickCard((TrickCard) cv.getCard());
	}
	
	public boolean couldAddTrickCard(TrickCard tc) {
		return deck.couldAddTrickCard(tc);
	}
	
	private void recomputeLocations() {
		Dimension size = getSize();
		int i = 0;
		for(CardViewPanel cv:cardsPlayed) {
			cv.setLocation(size.width/2 - cv.getWidth()/2, i*Math.max(60, Math.min(cv.getHeight()*(cardsPlayed.size()-1), size.height-cv.getHeight()))/(cardsPlayed.size()-1));
			i++;
		}
	}
	
	private void setZComponents() {
		for(CardViewPanel cv:cardsPlayed) {
			setComponentZOrder(cv, cv.getZOrder());
		}
	}

	/* Right now it will not draw all cards itself
	 * since there needs to be more housekeeping for it to work this way in gamepanel.
	 * We need to change how trickCards are added and where they are kept, and how
	 * we make the little change of placing it above everything else in the z order for it to work.
	 */
	public void drawDeck(Graphics g) {
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
	
	public void paintComponent(Graphics g) {
		drawDeck(g);
	}
	
	public void drawAbsoluteDeck(Graphics g, CardViewPanel cardPlayed, double height) {
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
		ArrayList<CardViewPanel> allCards = getAllCards();
		if(cardPlayed != null) {
			allCards.add(cardPlayed);
		}
		double cardDelta = height/(allCards.size());
		cardDelta = Math.min(cardDelta, Constants.ORIG_CARD_HEIGHT);
		for(int i = 0; i < allCards.size(); i++) {
			CardViewPanel cv = allCards.get(i);
			cv.drawCard(g, 0, (int) (i*cardDelta), false);
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
	
	public int getCenterY(){
		return getY() + (int) calculateDeckHeight()/2;
	}
	
	public int getCenterX() {
		return getX() + (int) (getCardWidth()/2);
	}

	private void drawManipulatives(Graphics g) {
		int x = getCardWidth() + getX();
		int y = getY();
		BufferedImage manip = PebbleImages.getMan();
		BufferedImage stinkManip = PebbleImages.getStinkyMan();
		BufferedImage freshManip = PebbleImages.getFreshenedMan();
		if(Constants.DEBUG_MODE) {
			System.out.println("getCardWidth is: " + getCardWidth() + ", while manip width is " + manip.getWidth());
		}
		int numOnSide = getCardWidth()/manip.getWidth();
		if(numOnSide != 0) {
			int numTotal = deck.initialDeckValue();
			int numSoFar = 0;
			int numLeft = calculateDeck();
			int[] stinkAirs = deck.calculateStinksAndAirsSeparately();
			stinkAirs[1] = Math.min(numTotal, stinkAirs[1]);
			for(; numSoFar < stinkAirs[0]; numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, stinkManip);
			}
			for(; numSoFar < stinkAirs[1]+stinkAirs[0]; numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, freshManip);
			}
			for(; numSoFar < numTotal; numSoFar++) {
				drawOneMan(g, x, y, numSoFar, numOnSide, manip);
			}
		}
	}
	
	private void drawOneMan(Graphics g, int x, int y, int numSoFar, int numOnSide, BufferedImage manImage) {
		int manX = x + (numSoFar%numOnSide)*manImage.getWidth();
		int manY = y + (numSoFar/numOnSide)*manImage.getHeight();
		//Debug.println("Drawing men at " + manX + ", " + manY + "with dimensions: " + manImage.getWidth() + ", " + manImage.getHeight());
		g.drawImage(manImage, manX, manY, null);
	}

	private int getCardWidth() {
		return (int) getTeammateCard().getSize().getWidth();
	}

	private void drawDeckRectangle(Graphics g, Color c) {
		int x = getTeammateCard().getX();
		int y = getTeammateCard().getY();
		int width = getCardWidth();
		int height = (int) calculateDeckHeight();
		CardViewPanel.drawThickRectangle(x, y, width, height, c, g);
	}

	public int calculateDeckHeight() {
		int topY = getTeammateCard().getY();
		CardViewPanel topCard = getTopMostCard();
		int y2 = topCard.getY();
		int bottomY = y2 + (int) topCard.getSize().getHeight();
		return bottomY - topY;	
	}

	/* 
	 * Returns the topmost card which also happens to be the lowest card 
	 * on the y axis in the pile.
	 */
	private CardViewPanel getTopMostCard() {
		ArrayList<CardViewPanel> cards = getAllCards();
		return cards.get(cards.size()-1);
	}

	public CardViewPanel getTeammateCard() {
		return teammateCard;
	}

	public int calculateDeck() {
		return deck.calculateDeck();
	}

	public int getPotentialScoreChange(CardViewPanel cv) {
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
	
	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckView Test");

		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());

		TeammateCard tm0 = new TeammateCard(Constants.MUSIC_GEEKS_FILENAME, "Music Geeks", "joined team", 6);
		TeammateCard tm1 = new TeammateCard(Constants.BBALL_TEAM_FILENAME, "Basketball Team", "joined team", 12);
		TrickCard t3 = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 2, "stink");
		TrickCard t1 = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 2, "air");	

		CardViewPanel pdm0 = CardViewPanelFactory.createCard(tm0, 100, 400);
		CardViewPanel pd1 = CardViewPanelFactory.createCard(t1);
		pd1.setHighlighted(true);
		CardViewPanel pd3 = CardViewPanelFactory.createCard(t3, 300, 300);
		pd3.setHighlighted(true);
		
		PlayDeck pd = new PlayDeck(tm0);
		pd.addTrickCard(t3);
		pd.addTrickCard(t1);
		PlayDeck pd0 = new PlayDeck(tm1);
		pd0.addTrickCard(t3);
		DeckViewPanel dvp = new DeckViewPanel(pd, null);
		DeckViewPanel dvp1 = new DeckViewPanel(pd0, null);
		DeckViewPanel dvp2 = new DeckViewPanel(tm0, null);
		cont.add(dvp);
		cont.add(dvp1);
		cont.add(dvp2);
		//cont.add(pd3);

		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
