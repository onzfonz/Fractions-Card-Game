/* File: RepresentationPanel.java
 * ------------------------------
 * This is more manual and works but we lose being able to place the fraction correctly for the sake of making the
 * performance on the file slightly faster in the resizing department.  this will have to do for now.
 */
package generalbar;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import basic.Constants;

import cards.TrickCard;

public class CardRepresentation extends JPanel {
	private Representation rep;
	private TrickCard card;

	public CardRepresentation(int num, int den) {
		this(new TrickCard("cards.jpg", num, den, "stink"));
	}

	public CardRepresentation(TrickCard tc) {
		rep = new Representation(tc);
		card = tc;
	}

	public void paintComponent(Graphics g) {
		drawCardRepresentation(g);
	}

	public void drawCardRepresentation(Graphics g) {
		drawCardRepresentation(g, 0, 0, getWidth(), getHeight());
	}

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
		rep.drawRepresentation(g, x + calcX, y + calcY, calcWidth, calcHeight);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Testing Representation");
		TrickCard tc = new TrickCard("cards.jpg", 3, 5, "stink");
		CardRepresentation bp = new CardRepresentation(tc);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
