/* BarPanel.java
 * ----------------------
 * Simple starter file for making sure that the overlaying and boxes are being drawn correctly.
 * That is that the bar and the tick marks match correctly.
 * In this implementation one is overlaid on top of the other.
 */
package generalbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BarPanel extends RepPanel {
	private int num;
	private int den;
	
	public static final int PEN_THICKNESS = 10;
	public static final String ZERO_LABEL = "0";
	public static final String ONE_LABEL = "1";
	public static final int BAR_REDNESS = 175;
	
	public BarPanel(int n, int d) {
		num = n;
		den = d;
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		drawBar(g, 0, getHeight()/8.0, getWidth(), getHeight());
	}
	
	public void drawBar(Graphics g, double x, double y, double width, double height) {
		double proposedSpacing = width/den;
		double thickness = proposedSpacing/PEN_THICKNESS;
		drawRedBar(g, x, y+height/2, width, height/3, thickness);
		drawNumberLine(g, x, y, width, height/3, thickness);
	}
	
	private void drawNumberLabels(Graphics g, double x, double y, double width, double height, double halfFontWidth) {
		Font oldFont = g.getFont();
		Font newFont = calculateFontSize(g, halfFontWidth);
		g.setFont(newFont);
		double h = calculateFontHeight(g);
		double w = calculateFontWidth(g);
		g.drawString(ZERO_LABEL, (int) x, (int) (y+h));
		g.drawString(ONE_LABEL, (int) (x + width-w), (int) (y+h));
		g.setFont(oldFont);
	}
	
	private Font calculateFontSize(Graphics g, double halfFontWidth) {
		Font someFont = g.getFont();
		someFont = someFont.deriveFont(someFont.getStyle() ^ Font.BOLD);
		FontMetrics fm = g.getFontMetrics(someFont);
		double w = fm.stringWidth(ZERO_LABEL);
		int increment = 1;
		if(w > halfFontWidth * 2) {
			increment = -1;
		}
		while(Math.abs(w - (halfFontWidth * 2)) > 2) {
			someFont = someFont.deriveFont(someFont.getSize2D()+increment);
			fm = g.getFontMetrics(someFont);
			w = fm.stringWidth(ZERO_LABEL);
		}
		return someFont;
	}
	
	private double calculateFontWidth(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		return fm.stringWidth(ZERO_LABEL);
	}
	
	private double calculateFontHeight(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		return fm.getAscent()-(fm.getLeading()+fm.getDescent());
	}
	
	private void drawNumberLine(Graphics g, double x, double y, double width, double height, double thickness) {
		double realWidth = width-thickness;
		drawHorizontalLine(g, x, y, width, height, thickness);
		double spacing = realWidth/den;
		for(int i = 0; i < den+1; i++) {
			if(i == 0 || i == den || (i*2)%den == 0) {
				g.fillRect((int) (x+spacing*i), (int) y, (int) thickness, (int) height);
			}else{
				g.fillRect((int) (x+spacing*i), (int) (y+height/4), (int) thickness, (int) height/2);
			}
		}
		drawNumberLabels(g, x, y+height, width, height, thickness);
	}
	
	private void drawHorizontalLine(Graphics g, double x, double y, double width, double height, double thickness) {
		g.fillRect((int) x, (int) ((y+height/2)-thickness/2), (int) width-1, (int) thickness);
	}
	
	private void drawRedBar(Graphics g, double x, double y, double width, double height, double thickness) {
		Color orig = g.getColor();
		double realWidth = width-thickness;
		double spacing = realWidth/den;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect((int) x, (int) (y), (int) width, (int) height, 2, 2);
		g.setColor(new Color(BAR_REDNESS, 0, 0));
		g.fillRoundRect((int) x, (int) (y+height/3), (int) (spacing*num+thickness/2), (int) height/3, 2, 2);
		g.setColor(orig);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("BarPanel Test");
		BarPanel bp = new BarPanel(1, 3);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public int getNumerator() {
		return num;
	}

	public void setNumerator(int num) {
		this.num = num;
	}

	public int getDenominator() {
		return den;
	}

	public void setDenominator(int den) {
		this.den = den;
	}
}
