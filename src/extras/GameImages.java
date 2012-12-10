package extras;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import basic.Constants;



public final class GameImages {
	private static BufferedImage orangePebble;
	private static BufferedImage purplePebble;
	private static BufferedImage hiddenPebble;
	private static BufferedImage pebbleBag;
	private static BufferedImage man;
	private static BufferedImage stinkyMan;
	private static BufferedImage freshenedMan;
	private static BufferedImage lostMan;
	private static BufferedImage tugBackground;
	private static BufferedImage redFlag;
	private static BufferedImage tugRope;
	private static BufferedImage stinkBomb;
	private static BufferedImage airFresh;
	private static BufferedImage trashCan;
	private static BufferedImage trashCanOpen;
	private static BufferedImage discardPile;
	private static BufferedImage iceCreamTruck;
	
	public GameImages() {
		orangePebble = getOrangePebble();
		purplePebble = getPurplePebble();
		hiddenPebble = getHiddenPebble();
		pebbleBag = getPebbleBag();
		man = getMan();
		stinkyMan = getStinkyMan();
		freshenedMan = getFreshenedMan();
		tugBackground = getTugBackground();
		redFlag = getRedFlag();
		tugRope = getTugRope();
		lostMan = getLostMan();
		stinkBomb = getStinkBomb();
		airFresh = getAirFreshener();
		trashCan = getTrashCan();
		trashCanOpen = getTrashCanOpen();
		discardPile = getDiscardPile();
		iceCreamTruck = getIceCreamTruck();
	}
	
	public static BufferedImage rotatePerson(BufferedImage man, double radians, boolean myside) {
		AffineTransform tx = new AffineTransform();
		if(myside) {
			tx.rotate(-radians, man.getWidth()/2, man.getHeight()/2);
		}else{
			tx.rotate(radians, man.getWidth()/2, man.getHeight()/2);
		}
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		return (op.filter(man, null));
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
	
	public static BufferedImage getLostMan() {
		lostMan = retrieveImage(lostMan, Constants.MAN_LOST_FILENAME);
		return lostMan;
	}
	
	public static BufferedImage getTugBackground() {
		tugBackground = retrieveImage(tugBackground, Constants.TUG_BG_FILENAME);
		return tugBackground;
	}
	
	public static BufferedImage getRedFlag() {
		redFlag = retrieveImage(redFlag, Constants.TUG_FLAG_FILENAME);
		return redFlag;
	}
	
	public static BufferedImage getTugRope() {
		tugRope = retrieveImage(tugRope, Constants.TUG_ROPE_FILENAME);
		return tugRope;
	}
	
	public static BufferedImage getStinkBomb() {
		stinkBomb = retrieveImage(stinkBomb, Constants.TUG_STINK_FILENAME);
		return stinkBomb;
	}
	
	public static BufferedImage getAirFreshener() {
		airFresh = retrieveImage(airFresh, Constants.TUG_AIR_FILENAME);
		return airFresh;
	}
	
	public static BufferedImage getTrashCan() {
		trashCan = retrieveImage(trashCan, Constants.TRASH_CAN_FILENAME);
		return trashCan;
	}
	
	public static BufferedImage getTrashCanOpen() {
		trashCanOpen = retrieveImage(trashCanOpen, Constants.TRASH_CAN_OPEN_FILENAME);
		return trashCanOpen;
	}
	
	public static BufferedImage getDiscardPile() {
		discardPile = retrieveImage(discardPile, Constants.DISCARD_PILE_FILENAME);
		return discardPile;
	}
	
	public static BufferedImage getIceCreamTruck() {
		iceCreamTruck = retrieveImage(iceCreamTruck, Constants.ICE_CREAM_TRUCK_ICON_FILENAME);
		return iceCreamTruck;
	}
	
	public static BufferedImage getStinkOrAir(boolean isStinky) {
		if(isStinky) {
			return getStinkBomb();
		}
		return getAirFreshener();
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
