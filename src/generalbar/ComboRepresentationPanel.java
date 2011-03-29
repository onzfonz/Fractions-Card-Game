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

public class ComboRepresentationPanel extends RepPanel {
	private RepPanel firstRep;
	private TrickCard card;
	private RepPanel secondRep;
	
	public ComboRepresentationPanel(int num, int den) {
		this(new TrickCard("cards.jpg", num, den, "stink"));
	}
	public ComboRepresentationPanel(TrickCard tc) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		card = tc;
		TrickCard one = tc.getFirstCard();
		TrickCard two = tc.getSecondCard();
		RepPanel firstRep = buildRepresentation(one, true);
		RepPanel secondRep = buildRepresentation(two, false);
		//setLayout(new BorderLayout());
		add(firstRep);
		add(secondRep);
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		Debug.println("w:" + getWidth() + ", h:" + getHeight());
	}
	
	public boolean isGeneralType(TrickCard tc) {
		return tc.isAir() || tc.isStink();
	}
	
	public RepPanel buildRepresentation(TrickCard tc, boolean useFractionText) {
		if(useFractionText && isGeneralType(tc)) {
			return new FractionText(tc.toFraction());
		}
		if(tc.isAir() || tc.isStink()) {
			return new BarPanel(tc.getNumerator(), tc.getDenominator());
		}else if(tc.isIceCream()) {
			return new IcePanel(tc.getNumerator(), tc.getDenominator());
		}
		return new FractionText(tc.toFraction());
	}
	
	public void setNumerator(int n) {
		card = new TrickCard(card.getImageName(), n, card.getDenominator(), card.getType());
		firstRep.setNumerator(n);
		secondRep.setNumerator(n);
	}
	
	public void setDenominator(int d) {
		card = new TrickCard(card.getImageName(), card.getNumerator(), d, card.getType());
		firstRep.setDenominator(d);
		secondRep.setDenominator(d);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Combo Representation Test");
		TrickCard tc = new TrickCard("Point5cards.jpg", 1, 3, "combo[stink:1/2;ice:2/2]");
		ComboRepresentationPanel bp = new ComboRepresentationPanel(tc);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
