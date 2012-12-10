package basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
	private static final Properties props = new Properties();
	public static final int TRICK_HAND_SIZE;
	public static final int TEAM_HAND_SIZE;
	public static final String IMG_PATH;
	public static final String BASE_IMG_PATH;
	public static final String FNAME_TEAM_DECK;
	public static final String FNAME_TEAM_DECK_ALT;
	public static final String FNAME_TRICK_DECK;
	public static final String FNAME_SEQ_PREFIX;
	public static final int SCORE_TO_WIN;
	public static final int NUM_ROUNDS_B4_SHADOW;
	
	//IP Addresses
	public static final String SERVER_IP;
	public static final String SERVER_IP_STANFORD;
	//older one was 127.0.0.1
//	public static final String SERVER_IP = "10.32.25.19";
	public static final String LOCAL_SERVER_IP;
	public static final String LOCAL_SERVER_IP2;
	public static final String SERVER_ADDR;
	public static final String LOCALHOST;

	public static final int PANEL_WIDTH;
	public static final int PANEL_HEIGHT;
	
	//Tooltip Message
	public static final String TIP_START;
	public static final String TIP_DONE_TURN;
	public static final String TIP_MANIP;
	public static final String TIP_USER_ANSWER;
	public static final String TIP_ANSWER;
	public static final String TIP_NOT_WHOLE;
	public static final String TIP_CLEAR;
	public static final String TIP_SHOW;
	public static final String TIP_LINE;
	public static final String TIP_PENCIL;
	public static final String TIP_PPL;
	public static final String TIP_DONE_SHAKING;
	public static final String TIP_MANIP_AREA;
	
	//String messages
	public static final String ICE_CREAM_NAME;
	public static final String ICE_DESC;
	public static final String STINK_DESC;
	public static final String AIR_DESC;
	public static final String RADIO_DESC;
	public static final String MONEY_DESC;
	public static final String YOU_WON;
	public static final String YOU_LOST;
	public static final String MAN_FRAME_YOUR_DECK_TEXT;
	public static final String MAN_FRAME_THEIR_DECK_TEXT;
	public static final String MAN_FRAME_DEFAULT_PLAY;
	public static final String CORRECT;
	public static final String CONNECT_SERVER;
	public static final String DECIDED_MOVE;
	public static final String THOUGHT_MOVE;
	public static final String UP_SOON;
	public static final String ICE_CREAM_ORANGE_DESC;
	public static final String ICE_CREAM_PURPLE_DESC;

	//Error Messages
	public static final String ERROR_PLACING_RADIO;
	public static final String ERROR_INPUT_NO_INT;
	public static final String ERROR_WRONG_ANSWER;
	public static final String ERROR_NO_GAME_YET;
	public static final String ERROR_HURTING_YOURSELF;
	public static final String ERROR_PROTECT_OTHER;
	public static final String ERROR_TRY_AGAIN;
	public static final String ERROR_TRY_ONCE_MORE;
	public static final String ERROR_NOT_QUITE;
	public static final String ERROR_NOT_AN_INTEGER;
	public static final String ERROR_SORRY;
	public static final String ERROR_CANT_PLACE_PREFIX;
	public static final String ERROR_CANT_PLACE_ON;
	public static final String ERROR_NOT_WHOLE_NUM;
	public static final String ERROR_NO_STINKERS_LEFT;
	public static final String ERROR_NO_STINK_CARDS;
	public static final String ERROR_STINKS_LEFT_DIV;
	
	public static final String STATUS_NEW_ROUND;
	public static final String MAN_FRAME_NO_ANSWER_BTN_TEXT;
	
	//Info messages
	public static final String INFO_SHUFFLING;
	public static final String INFO_PLAY_AGAIN;
	public static final String INFO_NO_MOVES;
	public static final String INFO_START_GAME_HELP;
	public static final String INFO_STARTING;
	public static final String INFO_NET_ASK_NAME;
	public static final String INFO_NET_ASK_PARTNER;
	public static final String INFO_ERR_REAL_NAME;
	public static final String INFO_PICK_SERVER;
	public static final String INFO_ERR_SERVER_404;
	public static final String INFO_ERR_NAMES_TAKEN;
	public static final String INFO_NET_ERR_LOST_CONN;
	public static final String INFO_ASK_B4_CLOSING;
	public static final String INFO_SHADOW_PLAYER;
		
	//Status messages
	//public static final String STATUS_NEW_ROUND = "A new round has started; choose which tricks you want to play.";
	public static final String STATUS_OPPO_NO_MOVES;
	public static final String STATUS_CALC_FRACTION;
	public static final String STATUS_TURN;
	public static final String STATUS_YOUR_TURN;
	public static final String STATUS_OPPO_TURN;
	public static final String STATUS_NICE_MOVE;
	public static final String STATUS_NO_SUCH_CMD;
	public static final String STATUS_DECIDING_WHOS_FIRST;
	
	public static final String STATUS_THEY;
	public static final String STATUS_FALL;
	public static final String STATUS_NO_FALL;
	public static final String STATUS_FOR_THE;
	public static final String STATUS_FIB;
	public static final String STATUS_OWN_DECK;
	public static final String STATUS_OPPO_DECK;
	public static final String STATUS_FIB_FIGURE_PREFIX;
	public static final String STATUS_FIB_FIGURE_MID;
	public static final String STATUS_FIB_FIGURE_SUFFIX;
	
	public static final String PARTS_PREFIX_YOU;
	public static final String PARTS_SUFFIX_YOU;
	public static final String PARTS_PREFIX_THEM;
	public static final String PARTS_SUFFIX_THEM;
	public static final String PARTS_POST_THEY;
	public static final String PARTS_TIE;
	public static final String PARTS_HAD;
	public static final String PARTS_MORE_TEAMMATE;
	public static final String PARTS_PLURAL_SUFFIX;
	public static final String PARTS_THAN;
	
	//Man messages
	public static final String MAN_MSG_HOW_MANY;
	public static final String MAN_MSG_GROUPS_NOT_EQUAL;
	public static final String MAN_MSG_NOT_COOL_MAN;
	public static final String MAN_HELP_PLACE_PREFIX;
	public static final String MAN_HELP_PLACE_SUFFIX;
	public static final String MAN_HELP_CIRCLE_PREFIX;
	public static final String MAN_HELP_CIRCLE_SUFFIX;
	
	public static final String COMBO_MSG_SIDE;
	
	//Button names
	public static final String BTN_NEW_GAME;
	public static final String BTN_DONE_TURN;
	public static final String BTN_PASS;
	public static final String BTN_LAUNCH_MANIP;
	public static final String BTN_MAN_ANSWER;
	public static final String BTN_MAN_CLEAR;
	public static final String BTN_MAN_HELP;
	public static final String BTN_PEBBLE_DONE_SHAKING;
	public static final String BTN_BACK_TO_GAME;
	
	public static final String[] YES_NO;
	public static final String[] RADIO_OPTIONS;
	
	
	public static final String WINDOW_TITLE;
	public static final String TITLE_COMBO;
	public static final String TITLE_ICE;
	public static final String YOU_ARE;
	public static final String YOUR_OPPO_HAS;
	
	public static final String YOUR_INIT_SCORE;
	public static final String THEIR_INIT_SCORE;
	public static final String YOUR_LABEL;
	public static final String THEIR_LABEL;
	
	//Question prompts
	public static final String ASK_FOR_RADIOS;
	
	//User Preferences
	public static boolean SHOW_COMPUTER_CARDS;
	public static boolean REGULAR_MODE;  //false gives green rectangle, doesn't have them calculate their mistakes, can be used for easy level
	public static boolean SHOW_DECK_LABEL_NUMBER;
	public static boolean SHOW_DECK_MANIPS;
	public static boolean HAVE_MANIP_BUTTON;
	//Option not implemented yet...Whether or not to construct the question to the user
	//When they have something like .5 of 12 to have them figure out all the values.
	public static boolean GIVE_QUESTION_TO_USER;
	public static boolean ASK_USERS_FRACTION_QS;
	public static boolean MANIPS_OVERLAP;
	public static boolean DEBUG_MODE;
	public static boolean VERBOSE_MODE;
	public static boolean SHOW_COLOR_SLIDERS;
	public static boolean NETWORK_MODE;
	public static boolean SHOW_ME_HOW_ENABLED;
	public static boolean SHOW_WORK_ON_COMPUTER;
	public static boolean USE_RIGGED_DECK;
	
	public static int ANIMATION_DELAY;
	public static final int ANIMATION_MS_PAUSE;
	
	static {
		InputStream input = Constants.class.getResourceAsStream("/game.properties");
		if(input != null) {
			try {
				props.load(input);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		TRICK_HAND_SIZE = propIntValue("TRICK_HAND_SIZE");
		TEAM_HAND_SIZE = propIntValue("TEAM_HAND_SIZE");
		IMG_PATH = propValue("IMG_PATH");
		BASE_IMG_PATH = propValue("BASE_IMG_PATH");
		FNAME_TEAM_DECK = propValue("FNAME_TEAM_DECK");
		FNAME_TEAM_DECK_ALT = propValue("FNAME_TEAM_DECK_ALT");
		FNAME_TRICK_DECK = propValue("FNAME_TRICK_DECK");
		FNAME_SEQ_PREFIX = propValue("FNAME_SEQ_PREFIX");
		SCORE_TO_WIN = propIntValue("SCORE_TO_WIN");
		NUM_ROUNDS_B4_SHADOW = propIntValue("NUM_ROUNDS_B4_SHADOW");
		
		SERVER_IP = propValue("SERVER_IP");
		SERVER_IP_STANFORD = propValue("SERVER_IP");
		LOCAL_SERVER_IP = propValue("LOCAL_SERVER_IP");
		LOCAL_SERVER_IP2 = propValue("LOCAL_SERVER_IP2");
		SERVER_ADDR = propValue("SERVER_ADDR");
		LOCALHOST = propValue("LOCALHOST");
		
		PANEL_WIDTH = propIntValue("PANEL_WIDTH");
		PANEL_HEIGHT = propIntValue("PANEL_HEIGHT");
		
		TIP_START = propValue("TIP_START");
		TIP_DONE_TURN = propValue("TIP_DONE_TURN");
		TIP_MANIP = propValue("TIP_MANIP");
		TIP_USER_ANSWER = propValue("TIP_USER_ANSWER");
		TIP_ANSWER = propValue("TIP_ANSWER");
		TIP_NOT_WHOLE = propValue("TIP_NOT_WHOLE");
		TIP_CLEAR = propValue("TIP_CLEAR");
		TIP_SHOW = propValue("TIP_SHOW");
		TIP_LINE = propValue("TIP_LINE");
		TIP_PENCIL = propValue("TIP_PENCIL");
		TIP_PPL = propValue("TIP_PPL");
		TIP_DONE_SHAKING = propValue("TIP_DONE_SHAKING");
		TIP_MANIP_AREA = propValue("TIP_MANIP_AREA");
		
		ICE_CREAM_NAME = propValue("ICE_CREAM_NAME");
		ICE_DESC = propValue("ICE_DESC");
		STINK_DESC = propValue("STINK_DESC");
		AIR_DESC = propValue("AIR_DESC");
		RADIO_DESC = propValue("RADIO_DESC");
		MONEY_DESC = propValue("MONEY_DESC");
		YOU_WON = propValue("YOU_WON");
		YOU_LOST = propValue("YOU_LOST");
		MAN_FRAME_YOUR_DECK_TEXT = propValue("MAN_FRAME_YOUR_DECK_TEXT");
		MAN_FRAME_THEIR_DECK_TEXT = propValue("MAN_FRAME_THEIR_DECK_TEXT");
		MAN_FRAME_DEFAULT_PLAY = propValue("MAN_FRAME_DEFAULT_PLAY");
		CORRECT = propValue("CORRECT");
		CONNECT_SERVER = propValue("CONNECT_SERVER");
		DECIDED_MOVE = propValue("DECIDED_MOVE");
		THOUGHT_MOVE = propValue("THOUGHT_MOVE");
		UP_SOON = propValue("UP_SOON");
		ICE_CREAM_ORANGE_DESC = propValue("ICE_CREAM_ORANGE_DESC");
		ICE_CREAM_PURPLE_DESC = propValue("ICE_CREAM_PURPLE_DESC");
		
		ERROR_PLACING_RADIO = propValue("ERROR_PLACING_RADIO");
		ERROR_INPUT_NO_INT = propValue("ERROR_INPUT_NO_INT");
		ERROR_WRONG_ANSWER = propValue("ERROR_WRONG_ANSWER");
		ERROR_NO_GAME_YET = propValue("ERROR_NO_GAME_YET");
		ERROR_HURTING_YOURSELF = propValue("ERROR_HURTING_YOURSELF");
		ERROR_PROTECT_OTHER = propValue("ERROR_PROTECT_OTHER");
		ERROR_TRY_AGAIN = propValue("ERROR_TRY_AGAIN");
		ERROR_TRY_ONCE_MORE = propValue("ERROR_TRY_ONCE_MORE");
		ERROR_NOT_QUITE = propValue("ERROR_NOT_QUITE");
		ERROR_NOT_AN_INTEGER = propValue("ERROR_NOT_AN_INTEGER");
		ERROR_SORRY = propValue("ERROR_SORRY");
		ERROR_CANT_PLACE_PREFIX = propValue("ERROR_CANT_PLACE_PREFIX"); 
		ERROR_CANT_PLACE_ON = propValue("ERROR_CANT_PLACE_ON"); 
		ERROR_NOT_WHOLE_NUM = propValue("ERROR_NOT_WHOLE_NUM");
		ERROR_NO_STINKERS_LEFT = propValue("ERROR_NO_STINKERS_LEFT");
		ERROR_NO_STINK_CARDS = propValue("ERROR_NO_STINK_CARDS");
		ERROR_STINKS_LEFT_DIV = propValue("ERROR_STINKS_LEFT_DIV");
		
		INFO_SHUFFLING = propValue("INFO_SHUFFLING");
		INFO_PLAY_AGAIN = propValue("INFO_PLAY_AGAIN");
		INFO_NO_MOVES = propValue("INFO_NO_MOVES");
		INFO_START_GAME_HELP = propValue("INFO_START_GAME_HELP");
		INFO_STARTING = propValue("INFO_STARTING");
		INFO_NET_ASK_NAME = propValue("INFO_NET_ASK_NAME");
		INFO_NET_ASK_PARTNER = propValue("INFO_NET_ASK_PARTNER");
		INFO_ERR_REAL_NAME = propValue("INFO_ERR_REAL_NAME");
		INFO_PICK_SERVER = propValue("INFO_PICK_SERVER");
		INFO_ERR_SERVER_404 = propValue("INFO_ERR_SERVER_404");
		INFO_ERR_NAMES_TAKEN = propValue("INFO_ERR_NAMES_TAKEN");
		INFO_NET_ERR_LOST_CONN = propValue("INFO_NET_ERR_LOST_CONN"); 
		INFO_ASK_B4_CLOSING = propValue("INFO_ASK_B4_CLOSING");
		INFO_SHADOW_PLAYER = propValue("INFO_SHADOW_PLAYER");

		STATUS_OPPO_NO_MOVES = propValue("STATUS_OPPO_NO_MOVES");
		STATUS_CALC_FRACTION = propValue("STATUS_CALC_FRACTION");
		STATUS_TURN = propValue("STATUS_TURN");
		STATUS_YOUR_TURN = propValue("STATUS_YOUR_TURN");
		STATUS_OPPO_TURN = propValue("STATUS_OPPO_TURN");
		STATUS_NICE_MOVE = propValue("STATUS_NICE_MOVE");
		STATUS_NO_SUCH_CMD = propValue("STATUS_NO_SUCH_CMD");
		STATUS_DECIDING_WHOS_FIRST = propValue("STATUS_DECIDING_WHOS_FIRST");
		STATUS_THEY = propValue("STATUS_THEY"); 
		STATUS_FALL = propValue("STATUS_FALL");
		STATUS_NO_FALL = propValue("STATUS_NO_FALL");
		STATUS_FOR_THE = propValue("STATUS_FOR_THE"); 
		STATUS_FIB = propValue("STATUS_FIB");
		STATUS_OWN_DECK = propValue("STATUS_OWN_DECK");
		STATUS_OPPO_DECK = propValue("STATUS_OPPO_DECK");
		STATUS_FIB_FIGURE_PREFIX = propValue("STATUS_FIB_FIGURE_PREFIX"); 
		STATUS_FIB_FIGURE_MID = propValue("STATUS_FIB_FIGURE_MID"); 
		STATUS_FIB_FIGURE_SUFFIX = propValue("STATUS_FIB_FIGURE_SUFFIX");
		STATUS_NEW_ROUND = propValue("STATUS_NEW_ROUND");

		PARTS_PREFIX_YOU = propValue("PARTS_PREFIX_YOU");
		PARTS_SUFFIX_YOU = propValue("PARTS_SUFFIX_YOU");
		PARTS_PREFIX_THEM = propValue("PARTS_PREFIX_THEM");
		PARTS_SUFFIX_THEM = propValue("PARTS_SUFFIX_THEM");
		PARTS_POST_THEY = propValue("PARTS_POST_THEY");
		PARTS_TIE = propValue("PARTS_TIE");
		PARTS_HAD = propValue("PARTS_HAD"); 
		PARTS_MORE_TEAMMATE = propValue("PARTS_MORE_TEAMMATE");
		PARTS_PLURAL_SUFFIX = propValue("PARTS_PLURAL_SUFFIX");
		PARTS_THAN = propValue("PARTS_THAN"); 

		MAN_MSG_HOW_MANY = propValue("MAN_MSG_HOW_MANY");
		MAN_MSG_GROUPS_NOT_EQUAL = propValue("MAN_MSG_GROUPS_NOT_EQUAL");
		MAN_MSG_NOT_COOL_MAN = propValue("MAN_MSG_NOT_COOL_MAN");
		MAN_HELP_PLACE_PREFIX = propValue("MAN_HELP_PLACE_PREFIX"); 
		MAN_HELP_PLACE_SUFFIX = propValue("MAN_HELP_PLACE_SUFFIX");
		MAN_HELP_CIRCLE_PREFIX = propValue("MAN_HELP_CIRCLE_PREFIX"); 
		MAN_HELP_CIRCLE_SUFFIX = propValue("MAN_HELP_CIRCLE_SUFFIX");
		COMBO_MSG_SIDE = propValue("COMBO_MSG_SIDE");

		BTN_NEW_GAME = propValue("BTN_NEW_GAME");
		BTN_DONE_TURN = propValue("BTN_DONE_TURN");
		BTN_PASS = propValue("BTN_PASS");
		BTN_LAUNCH_MANIP = propValue("BTN_LAUNCH_MANIP");
		BTN_MAN_ANSWER = propValue("BTN_MAN_ANSWER");
		BTN_MAN_CLEAR = propValue("BTN_MAN_CLEAR");
		BTN_MAN_HELP = propValue("BTN_MAN_HELP");
		BTN_PEBBLE_DONE_SHAKING = propValue("BTN_PEBBLE_DONE_SHAKING");
		BTN_BACK_TO_GAME = propValue("BTN_BACK_TO_GAME");
		MAN_FRAME_NO_ANSWER_BTN_TEXT = propValue("MAN_FRAME_NO_ANSWER_BTN_TEXT");
		YES_NO = new String[2];
		YES_NO[0] = propValue("YES_NO_YES");
		YES_NO[1] = propValue("YES_NO_NO");
		RADIO_OPTIONS = new String[3];
		RADIO_OPTIONS[0] = propValue("RADIO_OPTIONS_0");
		RADIO_OPTIONS[1] = propValue("RADIO_OPTIONS_1");
		RADIO_OPTIONS[2] = propValue("RADIO_OPTIONS_2");

		WINDOW_TITLE = propValue("WINDOW_TITLE");
		TITLE_COMBO = propValue("TITLE_COMBO");
		TITLE_ICE = propValue("TITLE_ICE");
		YOU_ARE = propValue("YOU_ARE"); 
		YOUR_OPPO_HAS = propValue("YOUR_OPPO_HAS"); 
		YOUR_INIT_SCORE = propValue("YOUR_INIT_SCORE"); 
		THEIR_INIT_SCORE = propValue("THEIR_INIT_SCORE"); 
		YOUR_LABEL = propValue("YOUR_LABEL");
		THEIR_LABEL = propValue("THEIR_LABEL");
		ASK_FOR_RADIOS = propValue("ASK_FOR_RADIOS");
		
		REGULAR_MODE = propTFValue("REGULAR_MODE");
		SHOW_DECK_MANIPS = propTFValue("SHOW_DECK_MANIPS");
		HAVE_MANIP_BUTTON = propTFValue("HAVE_MANIP_BUTTON");
		GIVE_QUESTION_TO_USER = propTFValue("GIVE_QUESTION_TO_USER");
		ASK_USERS_FRACTION_QS = propTFValue("ASK_USERS_FRACTION_QS");
		MANIPS_OVERLAP = propTFValue("MANIPS_OVERLAP");
		DEBUG_MODE = propTFValue("DEBUG_MODE");
		VERBOSE_MODE = propTFValue("VERBOSE_MODE");
		NETWORK_MODE = propTFValue("NETWORK_MODE");
		SHOW_ME_HOW_ENABLED = propTFValue("SHOW_ME_HOW_ENABLED");
		SHOW_WORK_ON_COMPUTER = propTFValue("SHOW_WORK_ON_COMPUTER");
		SHOW_COLOR_SLIDERS = propTFValue("DEBUG_MODE") && propTFValue("SHOW_COLOR_SLIDERS");
		SHOW_DECK_LABEL_NUMBER = propTFValue("DEBUG_MODE") && propTFValue("SHOW_DECK_LABEL_NUMBER");
		SHOW_COMPUTER_CARDS = propTFValue("DEBUG_MODE") && propTFValue("SHOW_COMPUTER_CARDS");
		USE_RIGGED_DECK = propTFValue("USE_RIGGED_DECK");
		
		ANIMATION_DELAY = propIntValue("ANIMATION_DELAY");
		ANIMATION_MS_PAUSE = propIntValue("ANIMATION_MS_PAUSE");
	}
	
	public static String propValue(String propName) {
		return props.getProperty(propName);
	}
	
	public static int propIntValue(String propName) {
		return Integer.parseInt(propValue(propName));
	}
	
	public static boolean propTFValue(String propName) {
		return Boolean.parseBoolean(propValue(propName));
	}
	
	public static final int BOARD_MARGIN = 50;
	public static final int DECK_VIEW_MARGIN = 25;
	public static final double SCALE = .75;
	public static final double MANIP_SCALE = .9;
	public static final int ORIG_CARD_WIDTH = 181;
	public static final int ORIG_CARD_HEIGHT = 270;
	public static final int MAX_CARD_WIDTH = (int) (ORIG_CARD_WIDTH*SCALE);
	public static final int HUGE_CARD_WIDTH = MAX_CARD_WIDTH * 2;
	public static final int MAX_CARD_HEIGHT = (int) (ORIG_CARD_HEIGHT*SCALE);
	public static final int HUGE_CARD_HEIGHT = MAX_CARD_HEIGHT * 2;

	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final int DEFAULT_PEN_THICKNESS = 4;
	
	//For the added story animations in PeopleSprayer and PeopleDisperser
	public static final int MAX_SPEED = 5;
	public static final int JUMP_VELOCITY = 10;
	public static final int DISPERSER_TIMES = 160;
	public static final int TRANSFORMER_TIMES = 19;
	public static final double PROPENSITY_TO_FLIP = .03;
	public static final int REVOLUTIONS = 5;
	public static final int PARTS_PER_REVOLUTION = 20;
	public static final int MANIP_STATE_REG = -1;
	public static final int MANIP_STATE_STINKY = 0;
	public static final int MANIP_STATE_FRESH = 1;
	
	//Card keywords
	public static final String FNAME_REP = "Representation";
	public static final String FNAME_MIDFORE = "MidForeground";
	public static final String FNAME_GENBAR = "General-Bar";
	public static final String FNAME_ICE = "Ice-Cream";
	public static final String FNAME_TEAM = "Midground---Teammates---";
	public static final String FNAME_SPACE_SEP = "---";
	public static final String FILE_CONTENT_SPACING = "---";
	public static final String FNAME_COMBO_REP = FNAME_REP + FNAME_SPACE_SEP + "Combo" + FNAME_SPACE_SEP;
	public static final String FNAME_SEP = "-";
	public static final String IMG_EXT = ".png";
	
	//Trick Card types
	public static final String STINK = "stink";
	public static final String AIR = "air";
	public static final String ICE = "ice";
	public static final String RADIO = "radio";
	public static final String COMBO = "combo";

	//Additional Image filenames
	public static final String PURPLE_FILENAME = "chip_stay.png";
	public static final String ORANGE_FILENAME = "chip_ice.png";
	public static final String HIDDEN_FILENAME = "chip_unknown.png";
	public static final String PEBBLE_BAG_FILENAME = "pebble_bag.png";
	public static final String MAN_IMG_FILENAME = "man.png";
	public static final String MAN_STINKY_FILENAME = "stinkyman.png";
	public static final String MAN_FRESH_FILENAME = "freshman.png";
	public static final String MAN_LOST_FILENAME = "lostman.png";
	public static final String TUG_BG_FILENAME = "tugbackground3.png";
	public static final String TUG_FLAG_FILENAME = "flag1.png";
	public static final String TUG_ROPE_FILENAME = "ropethin.png";
	public static final String TUG_STINK_FILENAME = "stinkbomb.png";
	public static final String TUG_AIR_FILENAME = "airfreshener.png";
	public static final String TRASH_CAN_FILENAME = "trashcan.png";
	public static final String TRASH_CAN_OPEN_FILENAME = "trashcanopen.png";
	public static final String DISCARD_PILE_FILENAME = "CardBack---Discard.png";
	public static final String ICE_CREAM_TRUCK_ICON_FILENAME = "icecreamtruck.png";
	public static final String MAN_IMG_PATH = BASE_IMG_PATH + "man.png";
	public static final String PEN_ICON_IMG_PATH = IMG_PATH + "lineicon.png";
	public static final String LINE_ICON_IMG_PATH = IMG_PATH + "pencilicon.png";
	public static final String PPL_ICON_IMG_PATH = IMG_PATH + "pplicon.png";
	
	//Debug Filenames for starting up Files
	public static final String CHEAT_SHADOW_CARD = "Shadow Players,What Is 1/2 of 6?,Shadow-Players.png,3,1";
	
	//Card Filenames
	public static final String TEAMMATE_BACK = "CardBack---Teammates.png";
	public static final String TRICK_BACK = "CardBack---Tricks.png";
	public static final String TEAMMATE_BGROUND = "Background---Teammates.png";
	public static final String TRICK_BGROUND = "Background---Tricks.png";
	public static final String RADIO_MID = "Midground---Radio.png";
	public static final String STINK_MID = "Midground---Stink.png";
	public static final String AIR_MID = "Midground---Air.png";
	public static final String ICE_MID = "Midground---Ice-Cream.png";
	public static final String COMBO_MID = "Midground---Combo.png";
	public static final String SHADOW_MGROUND = "Midground---Teammates---Shadow-Players.png";
	public static final String COMBO_FILE_DELIMITER = ";";

	//Older Auxiliary Filenames
	public static final String HALF_STINK_FILENAME = "One-Half.png";
	public static final String POINT_5_FILENAME = "Point-5.png";
	public static final String ONE_QTR_AIR_FILENAME = "One-Quarter.png";
	public static final String THREE_QTRS_AIR_FILENAME = "Three-Quarters.png";
	public static final String HALF_AIR_FILENAME = "One-Half.png";
	public static final String RADIO_FILENAME = "Radio---General.png";
	public static final String MUSIC_GEEKS_FILENAME = "Music-Geeks.png";
	public static final String BBALL_TEAM_FILENAME = "Basketball-Team.png";
	public static final String TWO_TWO_ICE_FILENAME = "One-Half.png";
	public static final String COMBO1_FILENAME = "Combo-1---Combo---Point-5-Ice.png";
	
	
	//General Constants
	public static final int MAN_WIDTH = 50; // diameter of one dot
	public static final int MAN_HEIGHT = 100;

	public static final int PEBBLE_SIZE = 50;
	public static final int PEBBLE_BAG_SIZE = 250;
	public static final int PEBBLE_BAG_MARGIN = 10;
	public static final int PEBBLE_BAG_THRESHOLD = 60 + PEBBLE_BAG_MARGIN;
	public static final int MAX_PEBBLES_FOR_RESIZING = 6;
	public static final int BETWEEN_GAME_PAUSE = 2500;
	public static final int MINI_GAME_PAUSE = 300;
	public static final int RESULT_ANIMATION_FIRE_WINDOW_PAUSE = 300;
	public static final Font FONT_TINY = new Font("sans-serif", Font.BOLD, 16);
	public static final Font FONT_SMALL = new Font("sans-serif", Font.BOLD, 24);
	public static final Font FONT_REG = new Font("sans-serif", Font.BOLD, 32);
	public static final Font FONT_LARGE = new Font("sans-serif", Font.BOLD, 48);
	public static final Color GAME_BACKGROUND = new Color(80, 100, 60);
	public static final Color STATUS_BACKGROUND = new Color(200, 0, 0);
	public static final Color STATUS_FOREGROUND = new Color(255, 255, 255);
	public static final Color TOOLBOX_FOREGROUND_LOUD = new Color(0, 192, 0);
	public static final Color TOOLBOX_FOREGROUND = new Color(0, 0, 0);
	public static final Color TOOLBOX_BACKGROUND = new Color(255, 255, 255);
	public static final Color COLOR_LIGHT_WOOD = new Color(221, 202, 147);
	public static final Color MANIP_CENTER_BACKGROUND = new Color(197, 241, 186);
	public static final Color MANIP_SHADOW_LEFT_BACKGROUND = new Color(140, 152, 163);
	public static final Color MANIP_SHADOW_CENTER_BACKGROUND = new Color(50, 50, 50);
	public static final Color MANIP_SHADOW_TEXT_FOREGROUND = new Color(255, 255, 255);
	public static final Color TINY_SHADOW_COLOR = Color.black;
	public static final Color COLOR_SKIN_TONE = new Color(244, 213, 183);
	public static final Color COLOR_ORANGE = new Color(245, 140, 105);
	public static final Color COLOR_PURPLE = new Color(178, 80, 158);
	public static final Color BAD_MOVE_COLOR = Color.red;
	public static final Color POSSIBLE_MOVE_COLOR = Color.blue;
	public static final Color DEFAULT_BUTTON_TEXT_COLOR = Color.black;
	public static final Color LOUD_BUTTON_TEXT_COLOR = Color.red;
	public static final Color GOOD_MOVE_COLOR = Color.green;
	public static final Color HIGHLIGHTED_MOVE_COLOR = Color.blue;
	public static final Color SUGGEST_MOVE_COLOR = Color.yellow;
	public static final int LINE_THICKNESS = 2;
	public static final int NUM_PLAYERS = 2;
	public static final int NUM_DASHES = 20;
	public static final int DRAG_THRESHOLD = 30;
	public static final int PENCIL_MODE = 1;
	public static final int LINE_MODE = 0;
	public static final int PPL_MODE = -1;
	public static final int LEFT_MOUSE_BTN = MouseEvent.BUTTON1;
	public static final int SOCKET_PORT = 1604;
	public static final int NUM_MANIPS_PER_ROW = 4;
	public static final String SENTENCE_SEP = "  ";

	
	public static final String OPTION_WEST = "West";
	public static final String OPTION_EAST = "East";
	public static final String OPTION_NORTH = "North";
	public static final String OPTION_SOUTH = "South";
	public static final String OPTION_CENTER = "Center";
	public static final String OPTION_OUTER = "Outer";
	
	//Card type messages
	public static final String STINK_TYPE = "Stink Bomb";
	public static final String AIR_TYPE = "Air Freshener";
	public static final String ICE_TYPE = "Ice Cream Truck";
	public static final String RADIO_TYPE = "Radio";
	public static final String MONEY_TYPE = "Ice Cream Truck";
	
	//Network constants
	public static final String NETWORK_SEND_DEBUG = "Send via Network: ";
	
	//Network in game commands
	public static final String CMD_MOVE = "move";
	public static final String CMD_ADD_TRICK = "Hand";
	public static final String CMD_ADD_TEAM = "Team";
	public static final String CMD_PART_CLEAR = "clear";
	public static final String CMD_CALC = "calc";
	public static final String CMD_NO_MOVES = "nomoves";
	public static final String CMD_PART_ME = "my";
	public static final String CMD_TURN = "Turn";
	public static final String CMD_START = "start";
	public static final String CMD_NEW_ROUND = "newround";
	public static final String CMD_RADIOS = "radios";
	public static final String CMD_PART_OPPO = "o";
	public static final String CMD_SHAKED = "shaked";
	public static final String CMD_SHAKING = "shaking";
	public static final String CMD_CHIP = "chip";
	public static final String CMD_MYHAND = CMD_PART_ME + CMD_ADD_TRICK;
	public static final String CMD_OHAND = CMD_PART_OPPO + CMD_ADD_TRICK;
	public static final String CMD_MYTEAM = CMD_PART_ME + CMD_ADD_TEAM;
	public static final String CMD_OTEAM = CMD_PART_OPPO + CMD_ADD_TEAM;
	public static final String CMD_ARG_START = "{";
	public static final String CMD_ARG_END = "}";
	public static final String CMD_SEP = ":";
	public static final String CMD_MOVE_SEP = ";";
	public static final String CMD_SEP_ARG = CMD_SEP + CMD_ARG_START;
	public static final String CMD_CARD_DELIMITER = "_";
	public static final String CMD_LOG = "log";
	public static final String CMD_LOG_HYPHEN = "-";
	public static final String CMD_LOG_SPACE = " ";
	
	//More network commands
	public static final String NET_CMD_PRE = ".";
	public static final String NET_CMD_ADD = NET_CMD_PRE + "add";
	public static final String NET_CMD_REMOVE = NET_CMD_PRE + "remove";
	public static final String NET_CMD_START = NET_CMD_PRE + "start";
	public static final String NET_CMD_QUIT = NET_CMD_PRE + "oppoleft";
	public static final String NET_CMD_PLAY = NET_CMD_PRE + "play";
	public static final String NET_CMD_GAME = NET_CMD_PRE + "cmd";
	public static final String NET_CMD_ERR = NET_CMD_PRE + "error";
	public static final String NET_CMD_READY_TO_START = NET_CMD_PRE + "readytostart";
	//public static final String NET_CMD_ = NET_CMD_PRE + "";
		
	//Ugly constants that shouldn't really be public but are shared by multiple files
	public static final double REP_WIDTH_MOD = .40;
	public static final double REP_HEIGHT_MOD = .28;
	public static final double REP_X_MOD = .535;
	public static final double REP_Y_MOD = REP_HEIGHT_MOD;
	public static final double REP_ICE_H_MOD = .25;
	public static final double REP_COMBO_W_MOD = .9;
	public static final double REP_COMBO_H_MOD = .13;
	public static final double REP_COMBO_X_MOD = .02;
	public static final double REP_COMBO_Y_MOD = .57;	

	public static boolean TEXT_AS_IMAGES = true;  //Need to change ComboCardView superclass if you change this
}
