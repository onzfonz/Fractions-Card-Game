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

import javax.imageio.ImageIO;
import javax.swing.*;

import basic.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ShadowCardView extends CardView{
	private BufferedImage backGround;
	public static final int SHADOW_DEN_X = 5;
	public static final int SHADOW_DEN_Y = 3; //was 10
	public static final int SHADOW_NUM_Y = 2; //was 7

	public ShadowCardView(Card c){ 
		this(c, true);
	}

	public ShadowCardView(Card c, int width, int height, boolean isShowin) {
		super(c, width, height, isShowin);
		setSize(new Dimension(width, height));
	}

	public ShadowCardView(Card c, boolean visible) {
		this(c, Constants.MAX_CARD_WIDTH, Constants.MAX_CARD_HEIGHT, visible);
	}

	public ShadowCardView(Card c, int width, int height) {
		this(c, width, height, true);
	}

	public void drawCard(Graphics g) {
		drawCard(g, getX(), getY(), true);
	}

	public void drawCard(Graphics g, int x, int y, boolean useScaledImage) {
		if(visible) {
			if(useScaledImage) {
				drawCardComponents(g, xScale, yScale, x, y, true);
				drawCardText(g, Constants.FONT_SMALL, x, y, SHADOW_DEN_X, SHADOW_DEN_Y, SHADOW_NUM_Y, getWidth(), getHeight());
				if(highlighted) {
					//drawThickRectangle(Math.abs(getWidth()/2-scaledImg.getWidth(null)/2), Math.abs(getHeight()/2-.getHeight(null)/2), Math.min(b.getWidth(null)-1, getWidth()-1), Math.min(b.getHeight(null)-1, getHeight()-1), 4, g);
					drawThickRectangle(x, y, (int) getSize().getWidth(), (int) getSize().getHeight(), 4, Color.red, g);
				}
			}else{
				drawCardComponents(g, 0, 0, x, y, false);
				drawCardText(g, Constants.FONT_REG, x, y, SHADOW_DEN_X+1, SHADOW_DEN_Y, SHADOW_NUM_Y, Constants.ORIG_CARD_WIDTH, Constants.ORIG_CARD_HEIGHT);
			}
			if(Constants.DEBUG_MODE) {
				g.drawString(""+index, getX() + (int) getSize().getWidth()/2, getY() + (int) getSize().getHeight()/2);
			}
		}
	}
	
	public void drawBigCard(Graphics g, int panelWidth, int panelHeight) {
		int xCoord = panelWidth-Constants.HUGE_CARD_WIDTH;
		int yCoord = panelHeight-Constants.HUGE_CARD_HEIGHT;
		g.drawImage(backGround, xCoord, yCoord, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT, null);
		drawCardComponents(g, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT, xCoord, yCoord, true);
		drawCardText(g, Constants.FONT_LARGE, xCoord, yCoord, SHADOW_DEN_X+1, SHADOW_DEN_Y, SHADOW_NUM_Y, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT);
	}
	
	private void drawCardComponents(Graphics g, int width, int height, int xCoord, int yCoord, boolean scaleImg) {
		if(!scaleImg) {
			width = getWidth();
			height = getHeight();
		}
		drawImages(g, xCoord, yCoord, width, height);
	}
	
	private void drawCardText(Graphics g, Font f, int xCoord, int yCoord, int denX, int denY, int numY, int width, int height) {
		String s = extractShadowText();
		Font origFont = g.getFont();
		FontMetrics fm;
		double sWidth;
		while(true) {
			g.setFont(f);
			fm = g.getFontMetrics();
			sWidth = fm.stringWidth(s);
			if(sWidth < (width * .75)) {
				break;
			}
			f = f.deriveFont(f.getSize2D()-1);
		}
		//g.drawString(s, (int) (xCoord + (double) (width/denX)), (int) (yCoord + ((double) height/denY)*numY));
		g.drawString(s, (int) (xCoord + (width/2-sWidth/2)), (int) (yCoord + ((double) height/denY)*numY));
		g.setFont(origFont);
	}

	private String extractShadowText() {
		String question = card.getDescription();
		int qIndex = question.indexOf("?");
		int sIndex = indexOfFirstNumber(question);
		return question.substring(sIndex, qIndex);
	}

	private int indexOfFirstNumber(String s) {
		for(int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if(Character.isDigit(ch)) {
				return i;
			}
		}
		return -1;
	}
}
