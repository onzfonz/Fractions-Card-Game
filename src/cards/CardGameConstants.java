package cards;

import java.awt.Font;

public interface CardGameConstants {
	public final int TRICK_HAND_SIZE = 4;
	public final int TEAM_HAND_SIZE = 2;
	public static final int BOARD_MARGIN = 50;
	public static final int DECK_VIEW_MARGIN = 25;
	public static final double SCALE = .75;
	public static final int ORIG_CARD_WIDTH = 181;
	public static final int ORIG_CARD_HEIGHT = 270;
	public static final int MAX_CARD_WIDTH = (int) (ORIG_CARD_WIDTH*SCALE);
	public static final int HUGE_CARD_WIDTH = MAX_CARD_WIDTH * 2;
	public static final int MAX_CARD_HEIGHT = (int) (ORIG_CARD_HEIGHT*SCALE);
	public static final int HUGE_CARD_HEIGHT = MAX_CARD_HEIGHT * 2;
	
	public static final String IMG_PATH = "images/JPEG/";
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final int DEFAULT_PEN_THICKNESS = 4;
	
	public static final String PURPLE_FILENAME = "chip_stay.png";
	public static final String ORANGE_FILENAME = "chip_ice.png";
	public static final String HIDDEN_FILENAME = "chip_unknown.png";
	public static final String PEBBLE_BAG_FILENAME = "pebble_bag.png";
	public static final String MAN_IMG_FILENAME = "man.png";
	public static final String MAN_STINKY_FILENAME = "stinkyman.png";
	public static final String MAN_FRESH_FILENAME = "freshman.png";
	public static final String MAN_IMG_PATH = "images/man.png";
	public static final int MAN_WIDTH = 50; // diameter of one dot
	public static final int MAN_HEIGHT = 100;
	
	public static final int PEBBLE_SIZE = 50;
	public static final int PEBBLE_BAG_SIZE = 250;
	public static final int PEBBLE_BAG_MARGIN = 10;
	public static final int PEBBLE_BAG_THRESHOLD = 60 + PEBBLE_BAG_MARGIN;
	public static final int MAX_PEBBLES_FOR_RESIZING = 6;
	public static final int BETWEEN_GAME_PAUSE = 2500;
	public static final int MINI_GAME_PAUSE = 300;
	public static final int SCORE_TO_WIN = 20;
	public static final Font DEFAULT_SMALL_FONT = new Font("sans-serif", Font.BOLD, 24);
	public static final Font DEFAULT_FONT = new Font("sans-serif", Font.BOLD, 32);
	public static final Font DEFAULT_BIG_FONT = new Font("sans-serif", Font.BOLD, 48);
	public static final String MAN_FRAME_NO_ANSWER_BTN_TEXT = "Not a whole Number!";
	public static final int LINE_THICKNESS = 2;
	public static final int NUM_DASHES = 20;
	
	//General Debugging and Other Future User Options
	public static final boolean SHOW_COMPUTER_CARDS = false;
	public static final boolean USER_GETS_GREEN_RECTANGLE = true;
	public static final boolean SHOW_DECK_LABEL_NUMBER = true;
	public static final boolean SHOW_DECK_MANIPS = true;
	//Option not implemented yet...Whether or not to construct the question to the user
	//When they have something like .5 of 12 to have them figure out all the values.
	public static final boolean GIVE_QUESTION_TO_USER = true;
	public static final boolean ASK_USERS_FRACTION_QS = true;
	public static final boolean MANIPS_OVERLAP = false;
	public static final boolean DEBUG_MODE = true;
	
	
	
}
