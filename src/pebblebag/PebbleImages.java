package pebblebag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import cards.CardGameConstants;


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
		orangePebble = retrieveImage(orangePebble, CardGameConstants.ORANGE_FILENAME);
		return orangePebble;
	}

	public static BufferedImage getPurplePebble() {
		purplePebble = retrieveImage(purplePebble, CardGameConstants.PURPLE_FILENAME);
		return purplePebble;
	}

	public static BufferedImage getHiddenPebble() {
		hiddenPebble = retrieveImage(hiddenPebble, CardGameConstants.HIDDEN_FILENAME);
		return hiddenPebble;
	}
	
	public static BufferedImage getPebbleBag() {
		pebbleBag = retrieveImage(pebbleBag, CardGameConstants.PEBBLE_BAG_FILENAME);
		return pebbleBag;
	}
	
	public static BufferedImage getMan() {
		man = retrieveImage(man, CardGameConstants.MAN_IMG_FILENAME);
		return man;
	}
	
	public static BufferedImage getStinkyMan() {
		stinkyMan = retrieveImage(stinkyMan, CardGameConstants.MAN_STINKY_FILENAME);
		return stinkyMan;
	}
	
	public static BufferedImage getFreshenedMan() {
		freshenedMan = retrieveImage(freshenedMan, CardGameConstants.MAN_FRESH_FILENAME);
		return freshenedMan;
	}

	private static BufferedImage retrieveImage(BufferedImage img, String filename) {
		if(img == null) {
			try {
				img = ImageIO.read(new File(CardGameConstants.IMG_PATH+filename));
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}
}
