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
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import extras.GameImages;


public class IcePanel extends RepPanel {
	private int num;
	private int den;
	
	public IcePanel(int n, int d) {
		num = n;
		den = d;
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		drawIce(g, 0, 0, getWidth(), getHeight());
	}
	
	public void drawIce(Graphics g, double x, double y, double width, double height) {
		double proposedSpacing = width/((((num+den)*3)+(num+den)+1));
		drawPebbles(g, GameImages.getOrangePebble(), proposedSpacing, 0, 0, getWidth(), getHeight(), 0, num);
		drawPebbles(g, GameImages.getPurplePebble(), proposedSpacing, 0, 0, getWidth(), getHeight(), num, num+den);
	}
	
	private void drawPebbles(Graphics g, BufferedImage img, double spacing, double x, double y, double width, double height, int start, int end) {
		for(int i = start; i < end; i++) {
			g.drawImage(img, (int) (x + spacing + spacing*(i*4)), (int) (y+ height/2-spacing*1.5), (int) spacing*3, (int) spacing*3, null);
		}
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("IcePanel Test");
		IcePanel bp = new IcePanel(2, 2);
		f.add(bp);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public int getNumerator() {
		return num;
	}

	@Override
	public void setNumerator(int num) {
		this.num = num;
	}

	public int getDenominator() {
		return den;
	}

	@Override
	public void setDenominator(int den) {
		this.den = den;
	}
}
