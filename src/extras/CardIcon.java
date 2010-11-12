package extras;
import javax.swing.*;

import transform.JXTransformer;

import cards.*;



import java.awt.*;

public class CardIcon extends ImageIcon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3784499824221725480L;
	protected Card card;
	protected boolean highlighted;
	protected int index;
	
	public static final int CARD_WIDTH = 181;
	public static final int CARD_HEIGHT = 270;
	public static final String IMG_PATH = "images/JPEG/";
	

	public CardIcon(Card c){ 
		this(c, CARD_WIDTH, CARD_HEIGHT);
	}
	
	public CardIcon(Card c, int width, int height) {
		super(IMG_PATH+c.getImageName());
		Image img = getImage();
		resetCard(c, img, width, height);
		
	}
	
	//Makes a new Trick Card
	public CardIcon(String imgName, int num, int den, String type) {
		this(new TrickCard(imgName, num, den, type), CARD_WIDTH, CARD_HEIGHT);
	}
	
	public CardIcon(Image img, int num, int den, String type) {
		this(img, num, den, type, CARD_WIDTH, CARD_HEIGHT);
	}
	
	public CardIcon(Image img, int num, int den, String type, int width, int height) {
		super(img);
		TrickCard c = new TrickCard(null, num, den, type);
		resetCard(c, img, width, height);
	}
	
	public CardIcon(Image img) {
		this(img, 0, 1, "stink", CARD_WIDTH, CARD_HEIGHT);
	}
	
	//Makes a new Teammate Card
	public CardIcon(String imgName, String name, String desc, int value) {
		this(new TeammateCard(imgName, name, desc, value), CARD_WIDTH, CARD_HEIGHT);
	}
	
	public Card getCard() {
		return card;
	}
	
	public boolean isTrickCard() {
		return (card instanceof TrickCard);
	}
	
	public boolean isTeammateCard() {
		return (card instanceof TeammateCard);
	}
	
	public void setHighlighted (boolean value) {
		highlighted = value;
//		repaint();
		//we want some type of fire event here to make all the other ones update as well
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x, y);
//		Image b = getImage();
		
		g.setColor(Color.red);
		g.drawString(""+index, getIconWidth()/2, getIconHeight()/2);
		if(highlighted) {
//			drawThickRectangle(Math.abs(getIconWidth()/2-b.getWidth(null)/2), Math.abs(getIconHeight()/2-b.getHeight(null)/2), Math.min(b.getWidth(null)-1, getIconWidth()-1), Math.min(b.getHeight(null)-1, getIconHeight()-1), 4, g);
			drawThickRectangle(0, 0, getIconWidth(), getIconHeight(), 4, g);
		}
	}
	
	private void resetCard(Card c, Image img, int width, int height) {
		index = -1;
		card = c;
		highlighted = false;
		setScaledImage(img, width, height);
	}
	
	private void setScaledImage(Image img, int width, int height) {
		if(getIconWidth() > width || getIconHeight() > height) {
			img = scaleImageKeepingRatio(img, width, height);
		}
		setImage(img);
	}
	private Image scaleImageKeepingRatio(Image img, int width, int height) {
		double ratio = ((double) getIconHeight())/getIconWidth();
		double windowRatio = ((double) height)/width;
		int xScale = width;
		int yScale = height;
		if(windowRatio < ratio) {
			yScale = height;
			xScale = (int) (yScale/ratio);
		}else{
			xScale = width;
			yScale = (int) (xScale*ratio);
		}
		return img.getScaledInstance(xScale, yScale, 0);
	}
	
	/* Draws lineSize pixel width rectangle, bounded with the box x y width height */
	private void drawThickRectangle(int x, int y, int width, int height, int lineSize, Graphics g) {
		for(int i = 0; i < lineSize; i++) {
			g.drawRect(x, y, width, height);
			x++;
			y++;
			width-=2;
			height-=2;
		}
	}
	
	public boolean withinBounds(int x, int y) {
		return (x >= 0 && x <= (getIconWidth()) && y >= 0 && y <= (getIconHeight()));
	}
	
	public void setZOrder(int zorder) {
		index = zorder;
	}
	
	public int getZOrder() {
		return index;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("PlayDeckView Test");
		
		JComponent cont = (JComponent) f.getContentPane();
		cont.setLayout(new FlowLayout());
		
		TeammateCard tm0 = new TeammateCard("cards_recruits.jpg", "Music Geeks", "joined team", 6);
		TeammateCard tm1 = new TeammateCard("cards_recruits12.jpg", "Basketball Team", "joined team", 12);
		
		TrickCard t0 = new TrickCard("cards_radio.jpg", 1, 1, "radio");
		TrickCard t1 = new TrickCard("cards_air.jpg", 1, 2, "air");
		TrickCard t2 = new TrickCard("cards_recruits12.jpg", 2, 2, "ice");
		TrickCard t3 = new TrickCard("cards_stink.jpg", 1, 2, "stink");
		
		JLabel pdm0 = new JLabel(new CardIcon(tm0, 100, 400));
		JLabel pd0 = new JLabel(new CardIcon(t0, 180, 180));
		CardIcon ci = new CardIcon(t1);
		JLabel pd1 = new JLabel(ci);
		ci.setHighlighted(true);
		CardIcon c2 = new CardIcon(t2, 90, 135);
		JLabel pd2 = new JLabel(c2);
		c2.setHighlighted(true);
		CardIcon c3 = new CardIcon(t3, 300, 300);
		JLabel pd3 = new JLabel(c3);
		c3.setHighlighted(true);
		CardIcon c4 = new CardIcon("cards_recruits5.jpg", 4, 4, "stink");
		JLabel pd4 = new JLabel(c4);
		
		JXTransformer t = new JXTransformer(pd3);
		t.rotate(Math.toRadians(180));
		
		cont.add(new CardViewOld(tm1));
		cont.add(pdm0);
		cont.add(pd2);
		cont.add(pd0);
		cont.add(pd1);
		cont.add(t);
		cont.add(pd4);
		
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
