package cards;
/*
 * File:BackCardView.java
 * ------------------
 * This is a simple static class that will have one copy of the cards that it will ask
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class BackCardView {
	public static final String TEAMMATE_BACK = "cards_back_teammates.jpg";
	public static final String TRICK_BACK = "cards_back_tricks.jpg";
	private static BufferedImage teamImg;
	private static BufferedImage trickImg;

	static {
		teamImg = getCardImage(TEAMMATE_BACK);
		trickImg = getCardImage(TRICK_BACK);
	}

	public static BufferedImage getTeammateBackImage() {
		return teamImg;
	}

	public static BufferedImage getTrickBackImage() {
		return trickImg;
	}

	private static BufferedImage getCardImage(String filename) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(CardGameConstants.IMG_PATH+filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
