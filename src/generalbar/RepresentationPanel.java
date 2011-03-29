/* File: RepresentationPanel.java
 * ------------------------------
 * This looks to work fine and is much nicer when we have to resize then doing it the other way with the fractions.
 */
package generalbar;

import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cards.TrickCard;
import extras.Debug;

public class RepresentationPanel extends RepPanel {
	private RepPanel rPanel;
	private TrickCard card;
	private FractionText fract;
	
	public RepresentationPanel(int num, int den) {
		this(new TrickCard("cards.jpg", num, den, "stink"));
	}
	public RepresentationPanel(TrickCard tc) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setLayout(new BorderLayout());
		card = tc;
		decideRepresentations(card);
	}
	
	private void decideRepresentations(TrickCard tc) {
		if(tc.isRadio()) {
			return;
		}
		if(tc.isIceCream()) {
			rPanel = new IcePanel(tc.getNumerator(), tc.getDenominator());
		}else{
			rPanel = new BarPanel(tc.getNumerator(), tc.getDenominator());
			fract = new FractionText(tc.toFraction());
			fract.setSize(fract.getPreferredSize());
			add(fract);
		}
		add(rPanel);
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		//Debug.println("w:" + getWidth() + ", h:" + getHeight());
	}
	
	public void setNumerator(int n) {
		card = new TrickCard(card.getImageName(), n, card.getDenominator(), card.getType());
		fract.setText(card.toFraction());
		rPanel.setNumerator(n);
	}
	
	public void setDenominator(int d) {
		card = new TrickCard(card.getImageName(), card.getNumerator(), d, card.getType());
		fract.setText(card.toFraction());
		rPanel.setDenominator(d);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Representation Panel Test");
		TrickCard tc = new TrickCard("cards.jpg", 1, 3, "ice");
		RepresentationPanel bp = new RepresentationPanel(tc);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
