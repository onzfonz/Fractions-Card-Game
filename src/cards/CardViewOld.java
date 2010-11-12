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

public class CardViewOld extends JComponent {
	protected Card card;
	protected BufferedImage img;
	protected Image scaledImg;
	protected boolean highlighted;
	protected int index;
	
	public static final int CARD_WIDTH = 181;
	public static final int CARD_HEIGHT = 270;
	public static final String IMG_PATH = "images/JPEG/";
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final int DEFAULT_PEN_THICKNESS = 4;
	

	public CardViewOld(Card c){ 
		this(c, CARD_WIDTH, CARD_HEIGHT);
	}
	
	public CardViewOld(Card c, int width, int height) {
		super();
		index = -1;
		card = c;
		String name = card.getImageName();
		highlighted = false;
		try {
		    img = ImageIO.read(new File(IMG_PATH+name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaledImg = img;
		setOpaque(true);
		setBackground(Color.black);
		setPreferredSize(new Dimension(width, height));
		revalidate();
	}
	
	@Override
	public void setPreferredSize(Dimension d) {
		scaleImageToDimension(d);
		super.setPreferredSize(new Dimension(scaledImg.getWidth(null), scaledImg.getHeight(null)));
	}
	
	public void setPreferredSize(double w, double h) {
		setPreferredSize(new Dimension((int) w, (int) h));
	}
	
	private void scaleImageToDimension(Dimension d) {
		if(img.getWidth() > d.getWidth() || img.getHeight() > d.getHeight()) {
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
			scaledImg = img.getScaledInstance(xScale, yScale, 0);
		}
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
		setPreferredSize(d);
		revalidate();
	}
	
	public void setHighlighted (boolean value) {
		highlighted = value;
//		repaint();
		//we want some type of fire event here to make all the other ones update as well
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		g.drawImage(scaledImg, 0, 0, null);
		if(highlighted) {
			//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
			drawThickRectangle(0, 0, getWidth(), getHeight(), 4, Color.red, g);
		}
	}
	
	public void drawCard(Graphics g) {
		g.drawImage(scaledImg, getX(), getY(), null);
		if(highlighted) {
			g.setColor(Color.red);
			//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
			drawThickRectangle(getX(), getY(), (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight(), 4, Color.red, g);
		}
		g.drawString(""+index, getX() + (int) getPreferredSize().getWidth()/2, getY() + (int) getPreferredSize().getHeight()/2);
	}
	
	/*public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image b = img;
		if(img.getWidth() > getWidth() || img.getHeight() > getHeight()) {
			double ratio = ((double) img.getHeight())/img.getWidth();
			double windowRatio = ((double) getHeight())/getWidth();
			int xScale = getWidth();
			int yScale = getHeight();
			if(windowRatio < ratio) {
				yScale = getHeight();
				xScale = (int) (yScale/ratio);
			}else{
				xScale = getWidth();
				yScale = (int) (xScale*ratio);
			}
			b = img.getScaledInstance(xScale, yScale, 0);
//			setPreferredSize(new Dimension(xScale, yScale));
		}
		g.drawImage(b, getWidth()/2-b.getWidth(null)/2, getHeight()/2-b.getHeight(null)/2, null);
		g.setColor(Color.red);
		g.drawString(""+index, getWidth()/2, getHeight()/2);
		if(highlighted) {
			g.setColor(Color.red);
			drawThickRectangle(Math.abs(getWidth()/2-b.getWidth(null)/2), Math.abs(getHeight()/2-b.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
		}
	}*/
	
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
	
	public static void drawThickRectangle(int x, int y, int width, int height, Color c, Graphics g) {
		drawThickRectangle(x, y, width, height, DEFAULT_PEN_THICKNESS, c, g);
	}
	
	public boolean withinBounds(int x, int y) {
		return (x >= getX() && x <= (getX()+getPreferredSize().getWidth()) && y >= getY() && y <= (getY()+getPreferredSize().getHeight()));
	}
	
	public void setZOrder(int zorder) {
		index = zorder;
	}
	
	public int getZOrder() {
		return index;
	}
	
// Convenience setters for clients
	
	// Moves x,y both the given dx,dy
	public void moveBy(int dx, int dy) {
		setLocation(getX()+dx, getY()+dy);
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
		
		CardViewOld pdm0 = new CardViewOld(tm0, 100, 400);
		CardViewOld pd0 = new CardViewOld(t0, 180, 180);
		CardViewOld pd1 = new CardViewOld(t1);
		pd1.setHighlighted(true);
		CardViewOld pd2 = new CardViewOld(t2, 90, 135);
		pd2.setHighlighted(true);
		CardViewOld pd3 = new CardViewOld(t3, 300, 300);
		pd3.setHighlighted(true);
		
		JXTransformer t = new JXTransformer(pd3);
		t.rotate(Math.toRadians(180));
		
		cont.add(pdm0);
		cont.add(new CardViewOld(tm1));
		cont.add(pd2);
		cont.add(pd0);
		cont.add(pd1);
		cont.add(t);
		
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
