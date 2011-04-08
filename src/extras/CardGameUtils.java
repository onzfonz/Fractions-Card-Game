package extras;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import basic.Constants;

public class CardGameUtils {
	public static void pause(int millis) {
		try{
			Thread.sleep(millis);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage getCardImageViaFilename(String fname) {
		return getCardImage(Constants.IMG_PATH + fname);
	}
	
	public static BufferedImage getCardImage(String path) {
		BufferedImage img = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Debug.println("image path: " + path);
		InputStream imageURL = cl.getResourceAsStream(path);
		try{
			img = ImageIO.read(imageURL);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static int keepInBoundary(int coord, int size, int min, int max) {
		if(coord < (min + size/2)) {
			return min + size/2;
		}else if(coord > (max - size/2)) {
			return max - size/2;
		}
		return coord;
	}
}
