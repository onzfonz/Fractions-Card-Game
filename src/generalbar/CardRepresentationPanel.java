/* File: RepresentationPanel.java
 * ------------------------------
 * This is more manual and works but we lose being able to place the fraction correctly for the sake of making the
 * performance on the file slightly faster in the resizing department.  this will have to do for now.
 */
package generalbar;

import extras.Debug;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import basic.Constants;
import cards.TrickCard;

public class CardRepresentationPanel extends RepPanel {
	private RepPanel rep;
	private TrickCard card;
	private ArrayList<BufferedImage> images;
	
	

	public CardRepresentationPanel(int num, int den) {
		this(new TrickCard("cards.jpg", num, den, "stink"));
	}
	
	public CardRepresentationPanel(TrickCard tc) {
		rep = RepresentationPanelFactory.createRep(tc);
		card = tc;
		if(rep != null) {
			add(rep);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
//	
//	public void drawCardRepresentation(Graphics g) {
//		drawCardRepresentation(g, 0, 0, getWidth(), getHeight());
//	}
	
	public void drawCardRepresentation(Graphics g, double x, double y, double width, double height) {
		double calcWidth = width * Constants.REP_WIDTH_MOD;
		double calcHeight = height * Constants.REP_HEIGHT_MOD;
		double calcX = width* Constants.REP_X_MOD;
		double calcY = height* Constants.REP_Y_MOD;
		if(card.isIceCream()) {
			calcHeight = height * Constants.REP_ICE_H_MOD;
			calcY = height * Constants.REP_ICE_H_MOD;
		}else if(card.isCombo()) {
			calcWidth = width * Constants.REP_COMBO_W_MOD;
			calcHeight = height * Constants.REP_COMBO_H_MOD;
			calcX = width/2 - calcWidth/2 - width * Constants.REP_COMBO_X_MOD;
			calcY = height * Constants.REP_COMBO_Y_MOD;
		}
		Debug.println("dcR: x: " + (x+calcX) + ", y: " + (y+calcY) + ", w: " + calcWidth + ", h: " + calcHeight);
		if(rep!=null) {
			//rep.setBounds((int) (x + calcX), (int) (y + calcY), (int) calcWidth, (int) calcHeight);
			Rectangle r = getBounds();
			Rectangle newR = new Rectangle((int) (x + calcX), (int) (y + calcY), (int) calcWidth, (int) calcHeight);
			//The bug here is that there are too many containers inside of each other.  Figure out which one is needed.
			//Maybe the CardRepPanel is not needed, and you just plop this thing into the CardViewPanel
			if(!r.equals(newR)) {
				setBounds((int) (x + calcX), (int) (y + calcY), (int) calcWidth, (int) calcHeight);
			}
			//rep.revalidate();
			//revalidate();
		}
	}
	
	public void setNumerator(int n) {
		rep.setNumerator(n);
	}
	
	public void setDenominator(int d) {
		rep.setDenominator(d);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Testing Card Representation");
		TrickCard tc = new TrickCard("cards.jpg", 1, 2, "combo[air:1/2;ice:2/2]");
		CardRepresentationPanel bp = new CardRepresentationPanel(tc);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
