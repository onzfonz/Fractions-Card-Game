package extras;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import basic.Constants;
import cards.TeammateCard;



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
	private static BufferedImage stinkyLayer;
	private static BufferedImage freshenedLayer;
	private static BufferedImage mouseButton;
	private static BufferedImage mouseButtonDown;
	private static BufferedImage highlightLayer;
	private static BufferedImage selectLayer;
	private static BufferedImage numLine;
	private static BufferedImage numLineHalf;
	private static BufferedImage numLineQtr;
	private static BufferedImage numLine3Qtrs;
	
	private static BufferedImage[] musicGeeks;
	private static BufferedImage[] pirates;
	private static BufferedImage[] twins;
	private static BufferedImage[] firstGraders;
	private static BufferedImage[] johnsonFamily;
	private static BufferedImage[] basketballTeam;
	private static BufferedImage[] shadowPlayers;
	
	public GameImages() {
		orangePebble = getOrangePebble();
		purplePebble = getPurplePebble();
		hiddenPebble = getHiddenPebble();
		pebbleBag = getPebbleBag();
		man = getMan();
		stinkyMan = getStinkyMan();
		stinkyLayer = getStinkyLayer();
		freshenedMan = getFreshenedMan();
		freshenedLayer = getFreshenedLayer();
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
		musicGeeks = getMusicGeeks();
		mouseButton = getMouseCursor();
		mouseButtonDown = getMouseButtonDown();
		highlightLayer = getHighlightLayer();
		selectLayer = getSelectedLayer();
	}
	
	public static BufferedImage rotatePerson(BufferedImage man, double radians, boolean myside) {
		AffineTransform tx = new AffineTransform();
		int w = man.getWidth();
		int h = man.getHeight();
		tx.translate(w/2.0, h/2.0);
		if(myside) {
//			tx.rotate(-radians, w/2.0, h/2.0);
			tx.rotate(-radians);
		}else{
//			tx.rotate(radians, w/2.0, h/2.0);
			tx.rotate(radians);
		}
		tx.translate(w/-2.0, h/-2.0);
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
	
	public static BufferedImage getStinkyLayer() {
		String layerName = Constants.LAYER_STINKY_FILENAME;
		if(Constants.USE_CHARACTER_MANIPS_IN_CALC) {
			layerName = Constants.LAYER_STINKY_CHAR_FILENAME;
		}
		stinkyLayer = retrieveImage(stinkyLayer, layerName);
		return stinkyLayer;
	}
	
	public static BufferedImage getFreshenedMan() {
		freshenedMan = retrieveImage(freshenedMan, Constants.MAN_FRESH_FILENAME);
		return freshenedMan;
	}
	
	public static BufferedImage getFreshenedLayer() {
		String layerName = Constants.LAYER_FRESH_FILENAME;
		if(Constants.USE_CHARACTER_MANIPS_IN_CALC) {
			layerName = Constants.LAYER_FRESH_CHAR_FILENAME;
		}
		freshenedLayer = retrieveImage(freshenedLayer, layerName);
		return freshenedLayer;
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
	
	public static BufferedImage getMouseCursor() {
		mouseButton = retrieveImage(mouseButton, Constants.MOUSE_ICON);
		return mouseButton;
	}
	
	public static BufferedImage getMouseButtonDown() {
		mouseButtonDown = retrieveImage(mouseButtonDown, Constants.MOUSE_DOWN_ICON);
		return mouseButtonDown;
	}
	
	public static BufferedImage getHighlightLayer() {
		highlightLayer = retrieveImage(highlightLayer, Constants.LAYER_HIGHLIGHT_FILENAME);
		return highlightLayer;
	}
	
	public static BufferedImage getSelectedLayer() {
		selectLayer = retrieveImage(selectLayer, Constants.LAYER_SELECT_FILENAME);
		return selectLayer;
	}
	
	public static BufferedImage getNormalNumberLine() {
		numLine = retrieveImage(numLine, Constants.NUM_LINE_FILENAME);
		return numLine;
	}
	
	public static BufferedImage getHighlightedNumberLine(int num, int den) {
		if(den == 2 && num == 1) {
			return getHalfNumberLine();
		}else if(den == 4) {
			if(num == 1) {
				return getQtrNumberLine();
			}else if(num == 3) {
				return get3QtrsNumberLine();
			}else if(num == 2) {
				return getHalfNumberLine();
			}
		}
		return getNormalNumberLine();
	}
	
	private static BufferedImage getHalfNumberLine() {
		numLineHalf = retrieveImage(numLineHalf, Constants.NUM_LINE_HALF_FILENAME);
		return numLineHalf;
	}
	
	private static BufferedImage getQtrNumberLine() {
		numLineQtr = retrieveImage(numLineQtr, Constants.NUM_LINE_QTR_FILENAME);
		return numLineQtr;
	}
	
	private static BufferedImage get3QtrsNumberLine() {
		numLine3Qtrs = retrieveImage(numLine3Qtrs, Constants.NUM_LINE_3_QTRS_FILENAME);
		return numLine3Qtrs;
	}
	
	public static BufferedImage getCharacterImage(int index, TeammateCard tc) {
		BufferedImage[] charSet = getCorrectCharacterSet(tc);
		return getSingleCharacterImage(index, charSet);
	}
	
	public static BufferedImage getRandomCharacter() {
		RandomGenerator rgen = RandomGenerator.getInstance();
		ArrayList<BufferedImage> chars = new ArrayList<BufferedImage>();
		chars.addAll(Arrays.asList(get1stGraders()));
		chars.addAll(Arrays.asList(getBasketballTeam()));
		chars.addAll(Arrays.asList(getJohnsonFamily()));
		chars.addAll(Arrays.asList(getMusicGeeks()));
		chars.addAll(Arrays.asList(getPirates()));
		chars.addAll(Arrays.asList(getTwins()));
		return chars.get(rgen.nextInt(0, chars.size()-1));
	}
	
	private static BufferedImage[] getCorrectCharacterSet(TeammateCard tc) {
		String name = tc.getName();
		if(name.equals(Constants.NAME_1ST_GRADERS)) {
			return get1stGraders();
		}else if(name.equals(Constants.NAME_BBALL_TEAM)) {
			return getBasketballTeam();
		}else if(name.equals(Constants.NAME_JOHNSON)) {
			return getJohnsonFamily();
		}else if(name.equals(Constants.NAME_MUSIC_GEEKS)) {
			return getMusicGeeks();
		}else if(name.equals(Constants.NAME_PIRATES)) {
			return getPirates();
		}else if(name.equals(Constants.NAME_TWINS)) {
			return getTwins();
		}
		return getShadowPlayers();
	}
	
	private static BufferedImage getSingleCharacterImage(int index, BufferedImage[] arr) {
		return arr[index%arr.length];
	}
	
	private static BufferedImage[] getMusicGeeks() {
		musicGeeks = fillAllCharacters(musicGeeks, Constants.CHAR_GEEKS_FILENAME, Constants.IMG_EXT, Constants.NUM_GEEKS);
		return musicGeeks;
	}
	
	private static BufferedImage[] get1stGraders() {
		firstGraders = fillAllCharacters(firstGraders, Constants.CHAR_1ST_GRADERS_FILENAME, Constants.IMG_EXT, Constants.NUM_1ST_GRADERS);
		return firstGraders;
	}
	
	private static BufferedImage[] getJohnsonFamily() {
		johnsonFamily = fillAllCharacters(johnsonFamily, Constants.CHAR_JOHNSON_FILENAME, Constants.IMG_EXT, Constants.NUM_JOHNSON);
		return johnsonFamily;
	}
	
	private static BufferedImage[] getBasketballTeam() {
		basketballTeam = fillAllCharacters(basketballTeam, Constants.CHAR_BBALL_FILENAME, Constants.IMG_EXT, Constants.NUM_BBALL_TEAM);
		return basketballTeam;
	}
	
	private static BufferedImage[] getPirates() {
		pirates = fillAllCharacters(pirates, Constants.CHAR_PIRATES_FILENAME, Constants.IMG_EXT, Constants.NUM_PIRATES);
		return pirates;
	}
	
	private static BufferedImage[] getTwins() {
		twins = fillAllCharacters(twins, Constants.CHAR_TWINS_FILENAME, Constants.IMG_EXT, Constants.NUM_TWINS);
		return twins;
	}
	
	private static BufferedImage[] getShadowPlayers() {
		shadowPlayers = fillAllCharacters(shadowPlayers, Constants.CHAR_SHADOW_FILENAME, Constants.IMG_EXT, Constants.NUM_SHADOW_PLAYERS);
		return shadowPlayers;
	}
	
	private static BufferedImage[] fillAllCharacters(BufferedImage[] arr, String prefix, String suffix, int size) {
		if(arr != null) {
			return arr;
		}
		BufferedImage[] characters = new BufferedImage[size];
		for(int i = 0; i < characters.length; i++) {
			characters[i] = retrieveImage(characters[i], prefix+(i+1)+suffix);
		}
		return characters;
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
