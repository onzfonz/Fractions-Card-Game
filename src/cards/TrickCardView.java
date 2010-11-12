package cards;

import java.awt.Dimension;
import java.awt.Graphics;

import basic.Constants;
import extras.Debug;
import generalbar.CardRepresentation;

public class TrickCardView extends CardView {
	protected CardRepresentation rPanel;
	
	public TrickCardView(Card c){ 
		this(c, true);
	}

	public TrickCardView(Card c, int width, int height, boolean isShowin) {
		super(c, width, height, isShowin);
		setSize(new Dimension(width, height));
		rPanel = new CardRepresentation((TrickCard) c);
	}

	public TrickCardView(Card c, boolean visible) {
		this(c, Constants.MAX_CARD_WIDTH, Constants.MAX_CARD_HEIGHT, visible);
	}

	public TrickCardView(Card c, int width, int height) {
		this(c, width, height, true);
	}
	
	public void drawCard(Graphics g) {
		drawCard(g, getX(), getY(), true);
	}

	public void drawCard(Graphics g, int x, int y, boolean useScaledImage) {
		super.drawCard(g, x, y, useScaledImage);
		Debug.println("x: " + x + ", y: " + y + ", xS: " + xScale + ", yS: " + yScale);
		rPanel.drawCardRepresentation(g, x, y, xScale, yScale);
	}
	
	public void drawBigCard(Graphics g, int panelWidth, int panelHeight) {
		super.drawBigCard(g, panelWidth, panelHeight);
		rPanel.drawCardRepresentation(g, panelWidth-Constants.HUGE_CARD_WIDTH, panelHeight-Constants.HUGE_CARD_HEIGHT, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT);
	}
}
