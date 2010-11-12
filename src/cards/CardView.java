package cards;
/*
 * File:CardView.java
 * ------------------
 * At this point this file is going to be like an extra view for the panel, not only does it have the model for 
 * a single card, but it also has all of the view stuff associated with that card including it's width, height, etc.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import basic.Constants;
import extras.Debug;
import generalbar.RepresentationPanel;

public class CardView {
	protected Card card;
	ArrayList<BufferedImage> images;
	protected boolean highlighted;
	protected boolean faceUp;
	protected boolean visible;
	protected boolean isCombo;
	protected int comboSelected;
	protected int index;
	protected int width;
	protected int height;
	protected int x;
	protected int y;
	protected int xScale;
	protected int yScale;

	public CardView(Card c){ 
		this(c, true);
	}

	public CardView(Card c, int width, int height, boolean isShowin) {
		super();
		index = -1;
		comboSelected = -1;
		card = c;
		highlighted = false;
		faceUp = isShowin;
		setVisible(true);
		images = new ArrayList<BufferedImage>();
		ArrayList <String> fnames = buildImageFilenames(card);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		for(String name:fnames) {
			Debug.println(Constants.IMG_PATH+name);
			InputStream imageURL = cl.getResourceAsStream(Constants.IMG_PATH+name);
			BufferedImage imgComp = null;
			try{
				Debug.println(imageURL);
				imgComp = ImageIO.read(imageURL);
			}catch(IOException e) {
				e.printStackTrace();
			}
			images.add(imgComp);
		}
		isCombo = isComboCard();
		setSize(new Dimension(width, height));
	}

	public CardView(Card c, boolean visible) {
		this(c, Constants.MAX_CARD_WIDTH, Constants.MAX_CARD_HEIGHT, visible);
	}

	public CardView(Card c, int width, int height) {
		this(c, width, height, true);
	}


	public void setSize(Dimension d) {
		scaleImageToDimension(d);
		width = xScale;
		height = yScale;
	}

	public void setSize(double w, double h) {
		setSize(new Dimension((int) w, (int) h));
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	private void scaleImageToDimension(Dimension d) {
		BufferedImage image = images.get(0);
		if(image.getWidth() >= d.getWidth() || image.getHeight() >= d.getHeight()) { //need to fix this here
			double ratio = ((double) image.getHeight())/image.getWidth();
			double windowRatio = ((double) d.getHeight())/d.getWidth();
			xScale = (int) d.getWidth();
			yScale = (int) d.getHeight();
			if(windowRatio < ratio) {
				yScale = (int) d.getHeight();
				xScale = (int) (yScale/ratio);
			}else{
				xScale = (int) d.getWidth();
				yScale = (int) (xScale*ratio);
			}
		}
	}

	private BufferedImage getBackOfCard() {
		if(card.isTeammateCard()) {
			return BackCardView.getTeammateBackImage();
		}else{
			return BackCardView.getTrickBackImage();
		}
	}

	public boolean isRadio() {
		if(card instanceof TrickCard) {
			return ((TrickCard) card).isRadio();
		}
		return false;
	}

	public boolean isAir() {
		if(card instanceof TrickCard) {
			return ((TrickCard) card).isAir();
		}
		return false;
	}

	public boolean isStink() {
		if(card instanceof TrickCard) {
			return ((TrickCard) card).isStink();
		}
		return false;
	}

	public boolean isIceCream() {
		if(card instanceof TrickCard) {
			return ((TrickCard) card).isIceCream();
		}
		return false;
	}

	public boolean isShadowPlayer() {
		if(card instanceof TeammateCard) {
			return ((TeammateCard) card).isShadowPlayer();
		}
		return false;
	}

	public boolean isCombo() {
		return isCombo;
	}

	private boolean isComboCard() {
		if(card instanceof TrickCard) {
			return ((TrickCard) card).isCombo();
		}
		return false;
	}

	public Card getCard() {
		return card;
	}

	public ArrayList<TrickCard> getCards() {
		ArrayList<TrickCard> cs = new ArrayList<TrickCard>();
		if(!isCombo()) {
			cs.add((TrickCard) card);
		}else{
			TrickCard tCard = (TrickCard) card;
			cs.add(tCard.getFirstCard());
			cs.add(tCard.getSecondCard());
		}
		return cs;
	}

	public void setCard(Card c) {
		card = c;
	}

	public String getName() {
		return card.getName();
	}

	public String getDescription() {
		return card.getDescription();
	}

	public void setCardViewDimension(Dimension d) {
		setSize(d);
	}

	public void setHighlighted (boolean value) {
		highlighted = value;
		//		repaint();
		//we want some type of fire event here to make all the other ones update as well
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setFaceUp (boolean value) {
		faceUp = value;
	}

	public boolean isFaceUp() {
		return faceUp;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public boolean isVisible() {
		return visible;
	}

	//Old version of paintComponent that doesn't work anymore.  Used when CardView was a JComponent
	//	public void paintComponent(Graphics g) {
	//		//super.paintComponent(g);
	//		if(visible) {
	//			g.drawImage(scaledImg, 0, 0, null);
	//			if(highlighted) {
	//				//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
	//				drawThickRectangle(0, 0, getWidth(), getHeight(), 4, Color.red, g);
	//			}
	//		}
	//	}

	public void drawCard(Graphics g) {
		drawCard(g, getX(), getY(), true);
	}

	public void drawCard(Graphics g, int x, int y, boolean useScaledImage) {
		if(visible) {
			if(useScaledImage) {
				drawImages(g, x, y, xScale, yScale);
				if(highlighted) {
					//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
					drawThickRectangle(x, y, (int) getSize().getWidth(), (int) getSize().getHeight(), 4, Color.red, g);
				}
			}else{
				drawImages(g, x, y);
			}
			if(Constants.DEBUG_MODE) {
				g.drawString(""+index, getX() + (int) getSize().getWidth()/2, getY() + (int) getSize().getHeight()/2);
			}
		}
	}
	
	private void drawImages(Graphics g, int x, int y) {
		for(BufferedImage image:images) {
			g.drawImage(image, x, y, null);
		}
	}
	
	protected void drawImages(Graphics g, int x, int y, int width, int height) {
		for(BufferedImage image:images) {
			g.drawImage(image, x, y, width, height, null);
		}
	}

	public void drawBigCard(Graphics g, int width, int height) {
		drawImages(g, width-Constants.HUGE_CARD_WIDTH, height-Constants.HUGE_CARD_HEIGHT, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT);
	}

	/* Draws lineSize pixel width rectangle, bounded with the box x y width height */
	public static void drawThickRectangle(int x, int y, int width, int height, int lineSize, Color c, Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(c);
		for(int i = 0; i < lineSize; i++) {
			g.drawRect(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
		g.setColor(oldColor);
	}

	public static void drawThickOval(int x, int y, int width, int height, int lineSize, Color c, Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(c);
		for(int i = 0; i < lineSize; i++) {
			g.drawOval(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
		g.setColor(oldColor);
	}

	private ArrayList<String> buildImageFilenames(Card c) {
		if(c == null) {
			return null;
		}
		ArrayList<String> names = new ArrayList<String>();
		if(c.isTrickCard()) {
			buildTrickFilenames((TrickCard) c, names);
		}else{
			buildTeammateFilenames((TeammateCard) c, names);
		}
		return names;
	}
	
	private void buildTeammateFilenames(TeammateCard tc, ArrayList<String> names) {
		names.add(Constants.TEAMMATE_BGROUND);
		if(tc.isShadowPlayer()) {
			names.add(Constants.SHADOW_MGROUND);
		}else{
			String s = tc.getImageName();
			names.add(Constants.FNAME_TEAM + s);
		}
	}
	
	private void buildTrickFilenames(TrickCard tc, ArrayList<String> names) {
		names.add(Constants.TRICK_BGROUND);
		if(tc.isCombo()) {
			addComboFilenames(tc, names);
		}else if(tc.isRadio()) {
			names.add(Constants.RADIO_MID);
		}else{
			addBasicTrickFilenames(tc, names);
		}
	}
	
	private void addComboFilenames(TrickCard tc, ArrayList<String> names) {
		names.add(Constants.COMBO_MID);
		String s = parseComboType(tc);
		names.add(Constants.FNAME_MIDFORE + Constants.FNAME_SPACE_SEP + s + Constants.IMG_EXT);
		if(Constants.TEXT_AS_IMAGES) {
			s = parseComboValue(tc);
			names.add(Constants.FNAME_COMBO_REP + s);
		}
		
	}
	
	private void addBasicTrickFilenames(TrickCard tc, ArrayList<String> names) {
		String fname = "";
		String reptype = "";
		if(tc.isAir()) {
			fname = Constants.AIR_MID;
			reptype = Constants.FNAME_GENBAR;
		}else if(tc.isStink()) {
			fname = Constants.STINK_MID;
			reptype = Constants.FNAME_GENBAR;
		}else if(tc.isIceCream()) {
			fname = Constants.ICE_MID;
			reptype = Constants.FNAME_ICE;
		}
		String name = tc.getImageName();
		names.add(fname);
		if(Constants.TEXT_AS_IMAGES) {
			names.add(Constants.FNAME_REP + Constants.FNAME_SPACE_SEP + reptype + Constants.FNAME_SPACE_SEP + name);
		}
	}

	private String parseComboType(TrickCard tc) {
		String name = tc.getImageName();
		int pos = name.indexOf(Constants.FNAME_SPACE_SEP);
		return name.substring(0, pos);
	}

	private String parseComboValue(TrickCard tc) {
		String name = tc.getImageName();
		int pos = name.indexOf(Constants.FNAME_SPACE_SEP);
		return name.substring(pos + Constants.FNAME_SPACE_SEP.length());
	}
	// Convenience setters for clients

	public static void drawThickRectangle(int x, int y, int width, int height, Color c, Graphics g) {
		drawThickRectangle(x, y, width, height, Constants.DEFAULT_PEN_THICKNESS, c, g);
	}

	public boolean withinBounds(int x, int y) {
		return (x >= getX() && x <= (getX()+getSize().getWidth()) && y >= getY() && y <= (getY()+getSize().getHeight()));
	}

	public TrickCard setOptionChosen(int option) {
		comboSelected = option;
		ArrayList<TrickCard> cards = getCards();
		return cards.get(comboSelected);
	}

	public int getOptionChosen() {
		return comboSelected;
	}

	public void setZOrder(int zorder) {
		index = zorder;
	}

	public int getZOrder() {
		return index;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Moves x,y both the given dx,dy
	public void moveBy(int dx, int dy) {
		setLocation(getX()+dx, getY()+dy);
	}

	public String toString() {
		return card.toString();
	}

	/* containers are commented out since we cannot simple make a JComponent that has a cardView
	 * CardViews are no longer JComponents.
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckView Test");

		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());

		TeammateCard tm0 = new TeammateCard(Constants.MUSIC_GEEKS_FILENAME, "Music Geeks", "joined team", 6);
		TeammateCard tm1 = new TeammateCard(Constants.BBALL_TEAM_FILENAME, "Basketball Team", "joined team", 12);

		TrickCard t0 = new TrickCard(Constants.RADIO_FILENAME, 1, 1, "radio");
		TrickCard t1 = new TrickCard(Constants.HALF_AIR_FILENAME, 1, 2, "air");
		TrickCard t2 = new TrickCard(Constants.BBALL_TEAM_FILENAME, 2, 2, "ice");
		TrickCard t3 = new TrickCard(Constants.HALF_STINK_FILENAME, 1, 2, "stink");

		CardView pdm0 = CardViewFactory.createCard(tm0, 100, 400);
		CardView pd0 = CardViewFactory.createCard(t0, 180, 180);
		CardView pd1 = CardViewFactory.createCard(t1);
		pd1.setHighlighted(true);
		CardView pd2 = CardViewFactory.createCard(t2, 90, 135);
		pd2.setHighlighted(true);
		CardView pd3 = CardViewFactory.createCard(t3, 300, 300);
		pd3.setHighlighted(true);

		//		JXTransformer t = new JXTransformer(pd3);
		//		t.rotate(Math.toRadians(180));
		//		
		//		cont.add(pdm0);
		//		cont.add(CardViewFactory.createCard(tm1));
		//		cont.add(pd2);
		//		cont.add(pd0);
		//		cont.add(pd1);
		//		cont.add(t);

		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}


}
