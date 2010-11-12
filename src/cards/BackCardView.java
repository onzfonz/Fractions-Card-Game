package cards;
/*
 * File:BackCardView.java
 * ------------------
 * This is a simple static class that will have one copy of the cards that it will ask
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

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
		BufferedImage img = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream imageURL = cl.getResourceAsStream(Constants.IMG_PATH+filename);
		try{
			img = ImageIO.read(imageURL);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
