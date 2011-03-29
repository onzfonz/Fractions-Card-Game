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

import cards.TrickCard;

public class Representation extends JPanel {
	private BarPanel nbp;
	private TrickCard card;
	private String fract;
	
	public Representation(int num, int den) {
		this(new TrickCard("cards.jpg", num, den, "stink"));
	}
	
	public Representation(TrickCard tc) {
		nbp = new BarPanel(tc.getNumerator(), tc.getDenominator());
		fract = tc.toFraction();
		card = tc;
	}
	
	public void paintComponent(Graphics g) {
		drawRepresentation(g);
	}
	
	public void drawRepresentation(Graphics g, double x, double y, double width, double height) {
		height = height/2;
		drawText(g, x, y, width, height);
		nbp.drawBar(g, x, y + height, width, height);
	}
	
	public void drawRepresentation(Graphics g) {
		double width = getWidth();
		double height = getHeight();
		drawRepresentation(g, 0, 0, width, height);
	}
	
	public void setNumerator(int n) {
		card = new TrickCard(card.getImageName(), n, card.getDenominator(), card.getType());
		fract = card.toFraction();
		nbp.setNumerator(n);
	}
	
	public void setDenominator(int d) {
		card = new TrickCard(card.getImageName(), card.getNumerator(), d, card.getType());
		fract = card.toFraction();
		nbp.setDenominator(d);
	}
	
	public void drawText(Graphics g, double x, double y, double width, double height) {
		Font origFont = g.getFont();
		Font newFont = calculateFontSize(g, width, height);
		g.setFont(newFont);
		FontMetrics fm = g.getFontMetrics();
		double w = fm.stringWidth(fract);
		double h = fm.getAscent()-(fm.getLeading()+fm.getDescent());
		g.drawString(fract, (int) (x+(width/2 - w/2)), (int) (y+(3*(height-(height/2 - h/2))/4)));
		g.setFont(origFont);
	}
	
	private Font calculateFontSize(Graphics g, double width, double height) {
		Font someFont = g.getFont();
		someFont = someFont.deriveFont(someFont.getStyle() ^ Font.BOLD);
		FontMetrics fm = g.getFontMetrics(someFont);
		double h = fm.getAscent();
		int increment = 1;
		if(h > getHeight()) {
			increment = -1;
		}
		while(Math.abs(h - (height)) > 2) {
			someFont = someFont.deriveFont(someFont.getSize2D()+increment);
			fm = g.getFontMetrics(someFont);
			h = fm.getAscent();
		}
		return someFont;
	}
	
	public String getText() {
		return fract;
	}

	public void setText(String text) {
		fract = text;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Testing Representation");
		TrickCard tc = new TrickCard("cards.jpg", 3, 5, "stink");
		Representation bp = new Representation(tc);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
