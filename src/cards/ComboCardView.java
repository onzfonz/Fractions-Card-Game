package cards;
/*
 * File:LayeredCardView.java
 * ------------------
 * This is my first attempt to try to decompose or break down the images so that we can display images one
 * on top of the other.
 * Thus each card we hope will have three sections, a background,
 * a midground which is broken up into two parts
 * and then a foreground.
 * These will follow our early attempts at creating something where the components are separated.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import basic.Constants;
import extras.Debug;
import extras.GraphicUtils;

public class ComboCardView extends CardView{
	private BufferedImage backGround;
	public static final int SHADOW_DEN_X = 5;
	public static final int SHADOW_DEN_Y = 3; //was 10
	public static final int SHADOW_NUM_Y = 2; //was 7

	public ComboCardView(Card c){ 
		this(c, true);
	}

	public ComboCardView(Card c, int width, int height, boolean isShowin) {
		super(c, width, height, isShowin);
	}

	public ComboCardView(Card c, boolean visible) {
		this(c, Constants.MAX_CARD_WIDTH, Constants.MAX_CARD_HEIGHT, visible);
	}

	public ComboCardView(Card c, int width, int height) {
		this(c, width, height, true);
	}

	public void drawCard(Graphics g) {
		drawCard(g, getX(), getY(), true);
	}

	public void drawCard(Graphics g, int x, int y, boolean useScaledImage) {
		super.drawCard(g, x, y, useScaledImage);
		drawOptionSelection(g);
	}
	
	public void drawBigCard(Graphics g, int panelWidth, int panelHeight) {
//		Image bimg = getCurrentCardImage().getScaledInstance(Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT, 0);
//		int xCoord = panelWidth-bimg.getWidth(null);
//		int yCoord = panelHeight-bimg.getHeight(null);
//		g.drawImage(bimg, xCoord, yCoord, null);
//		TrickCard c = (TrickCard) getCard();
//		if(comboSelected != -1 && !c.isCombo()) {
//			GraphicUtils.drawThickOval(xCoord, yCoord, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT, 4, Color.RED, g);
//		}
		super.drawBigCard(g, panelWidth, panelHeight);
		drawOptionSelection(g, panelWidth-Constants.HUGE_CARD_WIDTH, panelHeight-Constants.HUGE_CARD_HEIGHT, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT);
	}
	
	public void drawOptionSelection(Graphics g) {
		drawOptionSelection(g, getX(), getY(), getWidth(), getHeight());
	}
	
	public void drawOptionSelection(Graphics g, int x, int y, int width, int height) {
		TrickCard c = (TrickCard) getCard();
		String type = c.getType();
		if(comboSelected != -1 && !c.isCombo()) {
			Debug.printlnVerbose("drawing option with option:" + comboSelected + ", combo type: " + type + ", height: " + height);
			drawACircle(g, x, y, width, height, comboSelected);
		}
	}
	
	public static void drawACircle(Graphics g, int x, int y, int width, int height, int option) {
		GraphicUtils.drawThickRectangle(x + (width/2)*option, y+height/4, width/2, height/2, 4, Constants.POSSIBLE_MOVE_COLOR, g);
//		GraphicUtils.drawThickOval(x + (width/2)*option, y+height/4, width/2, height/2, 4, Color.RED, g);
	}
}
