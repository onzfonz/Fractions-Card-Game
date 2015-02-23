package cards;
/*
 * File:BackCardView.java
 * ------------------
 * This is a simple static class that will have one copy of the cards that it will ask
 */

import java.awt.image.BufferedImage;
import extras.CardGameUtils;

import basic.Constants;


public class BackCardView {	
	private BackCardView() {
		
	}
	
	private static class BackCardViewHolder {
		public static final BufferedImage teamImg = getCardImage(Constants.TEAMMATE_BACK);
		public static final BufferedImage trickImg = getCardImage(Constants.TRICK_BACK);
	}

	public static BufferedImage getTeammateBackImage() {
		return BackCardViewHolder.teamImg;
	}

	public static BufferedImage getTrickBackImage() {
		return BackCardViewHolder.trickImg;
	}

	private static BufferedImage getCardImage(String filename) {
		return CardGameUtils.getCardImage(Constants.IMG_PATH + filename);
	}
}
