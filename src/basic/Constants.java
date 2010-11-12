package basic;

import java.awt.Font;

public class Constants {
	public static final int TRICK_HAND_SIZE = 4;
	public static final int TEAM_HAND_SIZE = 2;
	public static final int BOARD_MARGIN = 50;
	public static final int DECK_VIEW_MARGIN = 25;
	public static final double SCALE = .75;
	public static final int ORIG_CARD_WIDTH = 181;
	public static final int ORIG_CARD_HEIGHT = 270;
	public static final int MAX_CARD_WIDTH = (int) (ORIG_CARD_WIDTH*SCALE);
	public static final int HUGE_CARD_WIDTH = MAX_CARD_WIDTH * 2;
	public static final int MAX_CARD_HEIGHT = (int) (ORIG_CARD_HEIGHT*SCALE);
	public static final int HUGE_CARD_HEIGHT = MAX_CARD_HEIGHT * 2;

	public static final String IMG_PATH = "images/PNG/";
	public static final String BASE_IMG_PATH = "images/";
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final int DEFAULT_PEN_THICKNESS = 4;
	
	//Card keywords
	public static final String FNAME_REP = "Representation";
	public static final String FNAME_MIDFORE = "MidForeground";
	public static final String FNAME_GENBAR = "General-Bar";
	public static final String FNAME_ICE = "Ice-Cream";
	public static final String FNAME_TEAM = "Midground---Teammates---";
	public static final String FNAME_SPACE_SEP = "---";
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
	public static final String MAN_IMG_PATH = "images/man.png";
	public static final String PEN_ICON_IMG_PATH = IMG_PATH + "lineicon.png";
	public static final String LINE_ICON_IMG_PATH = IMG_PATH + "pencilicon.png";
	public static final String PPL_ICON_IMG_PATH = IMG_PATH + "pplicon.png";
	
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
	public static final String THREE_QTRS_AIR_FILENAME = "Three-Quarters.png";
	public static final String HALF_AIR_FILENAME = "One-Half.png";
	public static final String RADIO_FILENAME = "Radio---General.png";
	public static final String MUSIC_GEEKS_FILENAME = "Music-Geeks.png";
	public static final String BBALL_TEAM_FILENAME = "Basketball-Team.png";
	public static final String TWO_TWO_ICE_FILENAME = "One-Half.png";
	public static final String COMBO1_FILENAME = "Combo-1---Combo---Point-5-Ice.png";
	
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
	public static final Font FONT_TINY = new Font("sans-serif", Font.BOLD, 16);
	public static final Font FONT_SMALL = new Font("sans-serif", Font.BOLD, 24);
	public static final Font FONT_REG = new Font("sans-serif", Font.BOLD, 32);
	public static final Font FONT_LARGE = new Font("sans-serif", Font.BOLD, 48);
	public static final String MAN_FRAME_NO_ANSWER_BTN_TEXT = "Not a whole Number!";
	public static final int LINE_THICKNESS = 2;
	public static final int NUM_DASHES = 20;
	public static final int DRAG_THRESHOLD = 30;
	public static final int PENCIL_MODE = 1;
	public static final int LINE_MODE = 0;
	public static final int PPL_MODE = -1;
	
	//String messages
	public static final String ICE_CREAM_NAME = "Ice Cream Truck";
	public static final String ICE_DESC = "Choose one of the other player's groups.  Put the pebbles in the bag.  If the other player chooses one of the bad pebbles, then the kids will run to the " + Constants.ICE_CREAM_NAME + ".";
	public static final String STINK_DESC = "Choose one of the other player's groups.  Remove a fraction of teammates from that group";
	public static final String AIR_DESC = "Choose one of the other player's groups.  Protect a fraction of the teammates from that group";
	public static final String RADIO_DESC = "Choose one your teammate cards that has an " + ICE_CREAM_NAME + " on it.  That card now gets to remove two pebbles from the bag!";
	public static final String MONEY_DESC = "Use this card and combine it with other cards to buy more trick cards or teammates!";
	public static final String YOU_WON = "YOU WON!!";
	public static final String YOU_LOST = "You lost.";
	public static final String MAN_FRAME_YOUR_DECK_TEXT = "Your Deck";
	public static final String MAN_FRAME_THEIR_DECK_TEXT = "Their Deck";
	public static final String CORRECT = "Correct! ";
	
	//Error messages
	public static final String ERROR_PLACING_RADIO = "You need to have an "+Constants.ICE_CREAM_NAME+" card on this deck to use a Radio card.";
	public static final String ERROR_INPUT_NO_INT = "Please enter a whole number.";
	public static final String ERROR_WRONG_ANSWER = "That is not the right answer. ";
	public static final String ERROR_NO_GAME_YET = "Please click on New Game first";
	public static final String ERROR_HURTING_YOURSELF = "You would be hurting yourself!";
	public static final String ERROR_PROTECT_OTHER = "You would be protecting your opponent!";
	
	//Info messages
	public static final String INFO_SHUFFLING = "Shuffling and dealing cards for the next round...be patient ok?";
	public static final String INFO_PLAY_AGAIN = "Would you like to play again?";
	public static final String INFO_NO_MOVES = "The computer has no more moves.  Would you like to move to the next round?";
	public static final String INFO_START_GAME_HELP = "You've started a new game, you'll take your turn by dragging one of your teammate cards onto your opponent's cards.";
	
	//Status messages
	public static final String STATUS_NEW_ROUND = "A new round has started; choose which tricks you want to play.";
	public static final String STATUS_OPPO_NO_MOVES = "The computer doesn't have any cards they want to play.";
	public static final String STATUS_CALC_FRACTION = "Looks like we need to calculate a fraction.";
	public static final String STATUS_YOUR_TURN = "Your turn.";
	public static final String STATUS_RIGHT_YOUR_TURN = CORRECT + STATUS_YOUR_TURN;
	public static final String STATUS_NICE_MOVE = "Nice Move! ";
	
	//Man messages
	public static final String MAN_MSG_HOW_MANY = "How many people were circled?";
	public static final String MAN_MSG_GROUPS_NOT_EQUAL = "Hey! The groups aren't equal!";
	public static final String MAN_MSG_NOT_COOL_MAN = "There aren't an equal number of groups...Not Cool Man!";
	
	//Question prompts
	public static final String ASK_FOR_RADIOS = "An "+Constants.ICE_CREAM_NAME+" fib has been played. How many Radios would you like to play?";
	
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
	
	//General Debugging and Other Future User Options
	public static boolean SHOW_COMPUTER_CARDS = true;
	public static boolean REGULAR_MODE = true;  //false gives green rectangle, doesn't have them calculate their mistakes, can be used for easy level
	public static boolean SHOW_DECK_LABEL_NUMBER = false;
	public static boolean SHOW_DECK_MANIPS = true;
	//Option not implemented yet...Whether or not to construct the question to the user
	//When they have something like .5 of 12 to have them figure out all the values.
	public static boolean GIVE_QUESTION_TO_USER = true;
	public static boolean TEXT_AS_IMAGES = true;  //Need to change ComboCardView superclass if you change this
	public static boolean ASK_USERS_FRACTION_QS = false;
	public static boolean MANIPS_OVERLAP = true;
	public static boolean DEBUG_MODE = true;

}
