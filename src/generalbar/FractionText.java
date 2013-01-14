package generalbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import extras.Debug;

public class FractionText extends RepPanel {
	private String text;

	public FractionText(String text) {
		int parenPos = text.indexOf("(");
		if(parenPos != -1) {
			int rightPos = text.indexOf(")");
			text = text.substring(parenPos+1, rightPos);
		}
		this.text = text;
	}
	
	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		g.setColor(Color.DARK_GRAY);
//		g.fillRect(0, 0, getWidth(), getHeight());
//		g.setColor(Color.BLACK);
		drawText(g);
	}
	
	public void drawText(Graphics g) {
		Font origFont = g.getFont();
		Font newFont = calculateFontSize(g);
		g.setFont(newFont);
		FontMetrics fm = g.getFontMetrics();
		double w = fm.stringWidth(text);
		double h = fm.getAscent()-(fm.getLeading()+fm.getDescent());
		//Debug.println(getHeight() + "," + h);
		g.drawString(text, (int) (getWidth()/2.0 - w/2.0), (int) (getHeight()-(getHeight()/2.0 - h/2.0)));
		g.setFont(origFont);
	}
	
	private Font calculateFontSize(Graphics g) {
		Font someFont = g.getFont();
		someFont = someFont.deriveFont(someFont.getStyle() ^ Font.BOLD);
		FontMetrics fm = g.getFontMetrics(someFont);
		double h = fm.getAscent();
		int increment = 1;
		if(h > getHeight()) {
			increment = -1;
		}
		while(Math.abs(h - (getHeight())) > 2) {
			someFont = someFont.deriveFont(someFont.getSize2D()+increment);
			fm = g.getFontMetrics(someFont);
			h = fm.getAscent();
		}
		return someFont;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void setNumerator(int n) {
		int pos = text.indexOf("/");
		if(pos == -1) {
			return;
		}
		text = n + text.substring(pos);
		
	}
	
	public void setDenominator(int d) {
		int pos = text.indexOf("/");
		if(pos == -1) {
			return;
		}
		text = text.substring(0, pos+1) + d;
	}
}
