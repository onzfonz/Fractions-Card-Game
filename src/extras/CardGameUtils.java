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
	
	public static BufferedImage getCardImage(String path) {
		BufferedImage img = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		System.out.println(path);
		InputStream imageURL = cl.getResourceAsStream(path);
		try{
			img = ImageIO.read(imageURL);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
