package basic;

import java.awt.Color;
import java.awt.Font;

public class Constants {
	public static final int TRICK_HAND_SIZE = 4;
	public static final int TEAM_HAND_SIZE = 2;
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
	public static final int SCORE_TO_WIN = 20;
	public static final Font FONT_TINY = new Font("sans-serif", Font.BOLD, 16);
	public static final Font FONT_SMALL = new Font("sans-serif", Font.BOLD, 24);
	public static final Font FONT_REG = new Font("sans-serif", Font.BOLD, 32);
	public static final Font FONT_LARGE = new Font("sans-serif", Font.BOLD, 48);
	public static final Color GAME_BACKGROUND = new Color(80, 100, 60);
	public static final Color STATUS_BACKGROUND = new Color(70, 90, 50);
	public static final Color STATUS_FOREGROUND = new Color(255, 255, 128);
	public static final Color TOOLBOX_FOREGROUND_LOUD = new Color(0, 192, 0);
	public static final Color TOOLBOX_FOREGROUND = new Color(0, 0, 0);
	public static final Color TOOLBOX_BACKGROUND = new Color(255, 255, 255);
	public static final String MAN_FRAME_NO_ANSWER_BTN_TEXT = "Not a whole number!";
	public static final int LINE_THICKNESS = 2;
	public static final int NUM_DASHES = 20;
	public static final int DRAG_THRESHOLD = 30;
	public static final int PENCIL_MODE = 1;
	public static final int LINE_MODE = 0;
	public static final int PPL_MODE = -1;
	public static final int PANEL_WIDTH = 1175;
	public static final int PANEL_HEIGHT = 750;
	public static final int SOCKET_PORT = 1604;
//	public static final String SERVER_IP = "128.12.200.60";
	//older one was 127.0.0.1
	public static final String SERVER_IP = "10.32.26.4";
//	public static final String LOCAL_SERVER_IP = "192.168.1.5";
	public static final String LOCAL_SERVER_IP = "192.168.0.15";
	public static final String SERVER_ADDR = "osvi.stanford.edu";
	public static final String LOCALHOST = "127.0.0.1";
	
	//Tooltip Message
	public static final String TIP_START = "Start playing a new game.";
	public static final String TIP_DONE_TURN = "Have your opponent make a move.";
	public static final String TIP_MANIP = "Play with the manipulatives.";
	public static final String TIP_USER_ANSWER = "Type your answer here.";
	public static final String TIP_ANSWER = "Answer the question with the text on the left.";
	public static final String TIP_NOT_WHOLE = "The answer to this question is not a whole number.";
	public static final String TIP_CLEAR = "Clear the screen of manipulatives and lines.";
	public static final String TIP_SHOW = "Show me how to calculate this fraction.";
	public static final String TIP_LINE = "Line tool.";
	public static final String TIP_PENCIL = "Pencil tool.";
	public static final String TIP_PPL = "Add-People tool.";
	public static final String TIP_DONE_SHAKING = "Have your opponent draw out a chip.";
	public static final String TIP_MANIP_AREA = "Use this area to figure out the answer to the question up top.  Click and drag or click in this area to get started.";
	
	//String messages
	public static final String ICE_CREAM_NAME = "Ice Cream Truck";
	public static final String ICE_DESC = "Choose one of the other player's groups.  Put the pebbles in the bag.  If the other player chooses one of the bad pebbles, then the kids will run to the " + Constants.ICE_CREAM_NAME + ".";
	public static final String STINK_DESC = "Choose one of the other player's groups.  Remove a fraction of teammates from that group.";
	public static final String AIR_DESC = "Choose one of the other player's groups.  Protect a fraction of the teammates from that group.";
	public static final String RADIO_DESC = "Choose one your teammate cards that has an " + ICE_CREAM_NAME + " on it.  That card now gets to remove two pebbles from the bag!";
	public static final String MONEY_DESC = "Use this card and combine it with other cards to buy more trick cards or teammates!";
	public static final String YOU_WON = "YOU WON!!";
	public static final String YOU_LOST = "You lost.";
	public static final String MAN_FRAME_YOUR_DECK_TEXT = "Your Deck";
	public static final String MAN_FRAME_THEIR_DECK_TEXT = "Their Deck";
	public static final String MAN_FRAME_DEFAULT_PLAY = "Play Here";
	public static final String SENTENCE_SEP = "  ";
	public static final String CORRECT = "Correct!" + SENTENCE_SEP;
	
	//Error messages
	public static final String ERROR_PLACING_RADIO = "You need to have an "+Constants.ICE_CREAM_NAME+" card on this deck to use a Radio card.";
	public static final String ERROR_INPUT_NO_INT = "Please enter a whole number.";
	public static final String ERROR_WRONG_ANSWER = "That is not the right answer." + SENTENCE_SEP;
	public static final String ERROR_NO_GAME_YET = "Please click on New Game first.";
	public static final String ERROR_HURTING_YOURSELF = "You would be hurting yourself!";
	public static final String ERROR_PROTECT_OTHER = "You would be protecting your opponent!";
	public static final String ERROR_TRY_AGAIN = "Try again.";
	public static final String ERROR_TRY_ONCE_MORE = "Please try again.";
	public static final String ERROR_NOT_QUITE = "Not Quite!";
	public static final String ERROR_NOT_AN_INTEGER = "Not an integer.";
	public static final String ERROR_SORRY = "Sorry";
	public static final String ERROR_CANT_PLACE_PREFIX = ERROR_SORRY + ", but you can't place the ";
	public static final String ERROR_CANT_PLACE_ON = " on ";
	
	//Info messages
	public static final String INFO_SHUFFLING = "Shuffling and dealing cards for the next round...be patient, OK?";
	public static final String INFO_PLAY_AGAIN = "Would you like to play again?";
	public static final String INFO_NO_MOVES = "Your opponent has no more moves.  Would you like to proceed to the next round?";
	public static final String INFO_START_GAME_HELP = "You've started a new game; to play one of your trick cards, drag it onto a teammate card.";
	public static final String INFO_STARTING = "Starting up the game.  This may take a bit ok?";
	
	//Status messages
	public static final String STATUS_NEW_ROUND = "A new round has started; choose which tricks you want to play.";
	public static final String STATUS_OPPO_NO_MOVES = "Your opponent doesn't have any cards they want to play.";
	public static final String STATUS_CALC_FRACTION = "Looks like we need to calculate a fraction.";
	public static final String STATUS_TURN = " turn.";
	public static final String STATUS_YOUR_TURN = "Your" + STATUS_TURN;
	public static final String STATUS_OPPO_TURN = "Your opponent's" + STATUS_TURN;
	public static final String STATUS_RIGHT_YOUR_TURN = CORRECT + STATUS_YOUR_TURN;
	public static final String STATUS_RIGHT_OPPO_TURN = CORRECT + STATUS_OPPO_TURN;
	public static final String STATUS_NICE_MOVE = "Nice Move!" + SENTENCE_SEP;
	public static final String STATUS_NO_SUCH_CMD = "Did not find a command in text.";
	public static final String STATUS_DECIDING_WHOS_FIRST = "Please wait while we figure out who starts.";
	
	public static final String STATUS_THEY = "They ";
	public static final String STATUS_FALL = "fell";
	public static final String STATUS_NO_FALL = "didn't fall";
	public static final String STATUS_FOR_THE = " for the ";
	public static final String STATUS_FIB = " fib!";
	public static final String STATUS_OWN_DECK = "your own deck";
	public static final String STATUS_OPPO_DECK = "the opponent's deck";
	public static final String STATUS_FIB_FIGURE_PREFIX = "Let's figure out if ";
	public static final String STATUS_FIB_FIGURE_MID = " teammates will fall for the ";
	public static final String STATUS_FIB_FIGURE_SUFFIX = " fib!";
	
	
	//Man messages
	public static final String MAN_MSG_HOW_MANY = "How many people were circled?";
	public static final String MAN_MSG_GROUPS_NOT_EQUAL = "Hey!  The groups aren't equal!";
	public static final String MAN_MSG_NOT_COOL_MAN = "There aren't an equal number of groups...Not Cool, Man!";
	public static final String MAN_HELP_PLACE_PREFIX = "Place ";
	public static final String MAN_HELP_PLACE_SUFFIX = " people going in a circle.";
	public static final String MAN_HELP_CIRCLE_PREFIX = "Circle ";
	public static final String MAN_HELP_CIRCLE_SUFFIX = " of those groups.";
	
	public static final String COMBO_MSG_SIDE = "Which side?";
	
	//Button names
	public static final String BTN_NEW_GAME = "New Game";
	public static final String BTN_DONE_TURN = "Done with Turn";
	public static final String BTN_PASS = "Pass";
	public static final String BTN_LAUNCH_MANIP = "Play with People";
	public static final String BTN_MAN_ANSWER = "Answer";
	public static final String BTN_MAN_CLEAR = "Clear Screen";
	public static final String BTN_MAN_HELP = "Show Me How";
	public static final String BTN_PEBBLE_DONE_SHAKING = "Done Shaking";
	
	public static final String[] YES_NO = {"Yes", "No"};
	public static final String[] RADIO_OPTIONS = {"No Radios", "One Radio", "Two Radios"};
	
	
	public static final String WINDOW_TITLE = "Fractions Card Game";
	public static final String TITLE_COMBO = "Which Card?";
	public static final String TITLE_ICE = "Ice Cream Truck";
	public static final String YOU_ARE = "You are ";
	public static final String YOUR_OPPO_IS = "Your opponent is ";
	
	public static final String YOUR_INIT_SCORE = "You: ";
	public static final String THEIR_INIT_SCORE = "Them: ";
	
	//Question prompts
	public static final String ASK_FOR_RADIOS = "An "+Constants.ICE_CREAM_NAME+" fib has been played. How many Radios would you like to play?";
	
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
	
	//General Debugging and Other Future User Options
	public static boolean SHOW_COMPUTER_CARDS = false;
	public static boolean REGULAR_MODE = true;  //false gives green rectangle, doesn't have them calculate their mistakes, can be used for easy level
	public static boolean SHOW_DECK_LABEL_NUMBER = false;
	public static boolean SHOW_DECK_MANIPS = true;
	public static boolean HAVE_MANIP_BUTTON = false;
	//Option not implemented yet...Whether or not to construct the question to the user
	//When they have something like .5 of 12 to have them figure out all the values.
	public static boolean GIVE_QUESTION_TO_USER = true;
	public static boolean TEXT_AS_IMAGES = true;  //Need to change ComboCardView superclass if you change this
	public static boolean ASK_USERS_FRACTION_QS = false;
	public static boolean MANIPS_OVERLAP = true;
	public static boolean DEBUG_MODE = false;
	public static boolean NETWORK_MODE = true;
}
