package cards;
/*
 * File:CardView.java
 * ------------------
 * At this point this file is going to be like an extra view for the panel, not only does it have the model for 
 * a single card, but it also has all of the view stuff associated with that card including it's width, height, etc.
 */

import javax.imageio.ImageIO;
import javax.swing.*;

import transform.JXTransformer;



import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CardView {
	protected Card card;
	protected BufferedImage img;
	protected Image scaledImg;
	protected boolean highlighted;
	protected boolean faceUp;
	protected boolean visible;
	protected int index;
	protected int width;
	protected int height;
	protected int x;
	protected int y;

	public CardView(Card c){ 
		this(c, true);
	}

	public CardView(Card c, int width, int height, boolean isShowin) {
		super();
		index = -1;
		card = c;
		String name = card.getImageName();
		highlighted = false;
		faceUp = isShowin;
		setVisible(true);
		try {
			img = ImageIO.read(new File(CardGameConstants.IMG_PATH+name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaledImg = getCurrentCardImage();
		setSize(new Dimension(width, height));
	}

	public CardView(Card c, boolean visible) {
		this(c, CardGameConstants.MAX_CARD_WIDTH, CardGameConstants.MAX_CARD_HEIGHT, visible);
	}

	public CardView(Card c, int width, int height) {
		this(c, width, height, true);
	}


	public void setSize(Dimension d) {
		scaleImageToDimension(d);
		width = scaledImg.getWidth(null);
		height = scaledImg.getHeight(null);
	}

	public void setSize(double w, double h) {
		setSize(new Dimension((int) w, (int) h));
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	private void scaleImageToDimension(Dimension d) {
		if(img.getWidth() >= d.getWidth() || img.getHeight() >= d.getHeight()) { //need to fix this here
			double ratio = ((double) img.getHeight())/img.getWidth();
			double windowRatio = ((double) d.getHeight())/d.getWidth();
			int xScale = (int) d.getWidth();
			int yScale = (int) d.getHeight();
			if(windowRatio < ratio) {
				yScale = (int) d.getHeight();
				xScale = (int) (yScale/ratio);
			}else{
				xScale = (int) d.getWidth();
				yScale = (int) (xScale*ratio);
			}
			scaledImg = getCurrentCardImage().getScaledInstance(xScale, yScale, 0);
		}
	}

	private BufferedImage getCurrentCardImage() {
		if(isFaceUp()) {
			return img;
		}else{
			return getBackOfCard();
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

	public Card getCard() {
		return card;
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
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		if(visible) {
			g.drawImage(scaledImg, 0, 0, null);
			if(highlighted) {
				//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
				drawThickRectangle(0, 0, getWidth(), getHeight(), 4, Color.red, g);
			}
		}
	}

	public void drawCard(Graphics g) {
		drawCard(g, getX(), getY(), true);
	}
	
	public void drawCard(Graphics g, int x, int y, boolean useScaledImage) {
		if(visible) {
			if(useScaledImage) {
				g.drawImage(scaledImg, x, y, null);
				if(highlighted) {
					g.setColor(Color.red);
					//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
					drawThickRectangle(x, y, (int) getSize().getWidth(), (int) getSize().getHeight(), 4, Color.red, g);
				}
			}else{
				g.drawImage(img, x, y, null);
			}
			if(CardGameConstants.DEBUG_MODE) {
				g.drawString(""+index, getX() + (int) getSize().getWidth()/2, getY() + (int) getSize().getHeight()/2);
			}
		}
	}
	
	public void drawBigCard(Graphics g, int width, int height) {
		Image bimg = getCurrentCardImage().getScaledInstance(CardGameConstants.HUGE_CARD_WIDTH, CardGameConstants.HUGE_CARD_HEIGHT, 0);
		g.drawImage(bimg, width-bimg.getWidth(null), height-bimg.getHeight(null), null);
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

	// Convenience setters for clients

	public static void drawThickRectangle(int x, int y, int width, int height, Color c, Graphics g) {
		drawThickRectangle(x, y, width, height, CardGameConstants.DEFAULT_PEN_THICKNESS, c, g);
	}

	public boolean withinBounds(int x, int y) {
		return (x >= getX() && x <= (getX()+getSize().getWidth()) && y >= getY() && y <= (getY()+getSize().getHeight()));
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

		TeammateCard tm0 = new TeammateCard("cards_recruits.jpg", "Music Geeks", "joined team", 6);
		TeammateCard tm1 = new TeammateCard("cards_recruits12.jpg", "Basketball Team", "joined team", 12);

		TrickCard t0 = new TrickCard("cards_radio.jpg", 1, 1, "radio");
		TrickCard t1 = new TrickCard("cards_air.jpg", 1, 2, "air");
		TrickCard t2 = new TrickCard("cards_recruits12.jpg", 2, 2, "ice");
		TrickCard t3 = new TrickCard("cards_stink.jpg", 1, 2, "stink");

		CardView pdm0 = new CardView(tm0, 100, 400);
		CardView pd0 = new CardView(t0, 180, 180);
		CardView pd1 = new CardView(t1);
		pd1.setHighlighted(true);
		CardView pd2 = new CardView(t2, 90, 135);
		pd2.setHighlighted(true);
		CardView pd3 = new CardView(t3, 300, 300);
		pd3.setHighlighted(true);

		//		JXTransformer t = new JXTransformer(pd3);
		//		t.rotate(Math.toRadians(180));
		//		
		//		cont.add(pdm0);
		//		cont.add(new CardView(tm1));
		//		cont.add(pd2);
		//		cont.add(pd0);
		//		cont.add(pd1);
		//		cont.add(t);

		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}


}
