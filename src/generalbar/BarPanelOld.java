/* BarPanel.java
 * ----------------------
 * Simple starter file for making sure that the overlaying and boxes are being drawn correctly.
 * That is that the bar and the tick marks match correctly.
 * In this implementation one is overlaid on top of the other.
 */
package generalbar;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BarPanelOld extends JPanel {
	private int num;
	private int den;
	
	public static final int PEN_THICKNESS = 10;
	
	public BarPanelOld(int n, int d) {
		num = n;
		den = d;
	}
	
	public void paintComponent(Graphics g) {
		drawBar(g, 0, getHeight()/4, getWidth(), getHeight()/2);
	}
	
	private void drawBar(Graphics g, double x, double y, double width, double height) {
		double proposedSpacing = getWidth()/den;
		double thickness = proposedSpacing/PEN_THICKNESS;
		drawNumberLine(g, x, y, width, height, thickness);
		drawRedBar(g, x, y, width, height, thickness);
	}
	
	private void drawNumberLine(Graphics g, double x, double y, double width, double height, double thickness) {
		double realWidth = width-thickness;
		drawHorizontalLine(g, x, y, width, height, thickness);
		double spacing = realWidth/den;
		for(int i = 0; i < den+1; i++) {
			if(i == 0 || i == den || (i*2)%den == 0) {
				g.fillRect((int) (spacing*i), (int) y, (int) thickness, (int) height);
			}else{
				g.fillRect((int) (spacing*i), (int) (y+height/4), (int) thickness, (int) height/2);
			}
		}
	}
	
	private void drawHorizontalLine(Graphics g, double x, double y, double width, double height, double thickness) {
		g.fillRect((int) x, (int) ((y+height/2)-thickness/2), (int) width, (int) thickness);
	}
	
	private void drawRedBar(Graphics g, double x, double y, double width, double height, double thickness) {
		Color orig = g.getColor();
		g.setColor(Color.red);
		double realWidth = width-thickness;
		double spacing = realWidth/den;
		g.fillRoundRect((int) x, (int) (y+height/3), (int) (spacing*num+thickness/2), (int) height/3, 10, 10);
		g.setColor(orig);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("BarPanel Test");
		BarPanelOld bp = new BarPanelOld(4, 6);
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
