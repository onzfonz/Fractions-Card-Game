package pebblebag;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import basic.Constants;

import cards.CardFrame;


public final class PebbleImages {
	private static BufferedImage orangePebble;
	private static BufferedImage purplePebble;
	private static BufferedImage hiddenPebble;
	private static BufferedImage pebbleBag;
	private static BufferedImage man;
	private static BufferedImage stinkyMan;
	private static BufferedImage freshenedMan;

	public PebbleImages() {
		orangePebble = getOrangePebble();
		purplePebble = getPurplePebble();
		hiddenPebble = getHiddenPebble();
		pebbleBag = getPebbleBag();
		man = getMan();
		stinkyMan = getStinkyMan();
		freshenedMan = getFreshenedMan();
	}

	public static BufferedImage getOrangePebble() {
		orangePebble = retrieveImage(orangePebble, Constants.ORANGE_FILENAME);
		return orangePebble;
	}

	public static BufferedImage getPurplePebble() {
		purplePebble = retrieveImage(purplePebble, Constants.PURPLE_FILENAME);
		return purplePebble;
	}

	public static BufferedImage getHiddenPebble() {
		hiddenPebble = retrieveImage(hiddenPebble, Constants.HIDDEN_FILENAME);
		return hiddenPebble;
	}
	
	public static BufferedImage getPebbleBag() {
		pebbleBag = retrieveImage(pebbleBag, Constants.PEBBLE_BAG_FILENAME);
		return pebbleBag;
	}
	
	public static BufferedImage getMan() {
		man = retrieveImage(man, Constants.MAN_IMG_FILENAME);
		return man;
	}
	
	public static BufferedImage getStinkyMan() {
		stinkyMan = retrieveImage(stinkyMan, Constants.MAN_STINKY_FILENAME);
		return stinkyMan;
	}
	
	public static BufferedImage getFreshenedMan() {
		freshenedMan = retrieveImage(freshenedMan, Constants.MAN_FRESH_FILENAME);
		return freshenedMan;
	}

	private static BufferedImage retrieveImage(BufferedImage img, String filename) {
		if(img == null) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream imageURL = cl.getResourceAsStream(Constants.IMG_PATH+filename);
			try{
				img = ImageIO.read(imageURL);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}
}
