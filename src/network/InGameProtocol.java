package network;

/* 
 * This is the one specific to the game.
 * Here are the states that we want to do.
 * Starting the game
 * -----------------
 * xTell the game to initialize everything, but don't start the round yet (this might already be done)
 * Deal the cards
 * Send the 2 teammate cards and 4 trick cards to each player.
 * Decide who will go first
 * enable person to move their cards
 * 
 * receive move that they want, and propogate it to other side.
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basic.Constants;
import basic.ServerDealer;
import extras.RandomGenerator;

public class InGameProtocol {
	public static final int INIT = 0;
	public static final int NO_CARDS = 1;
	public static final int DEALING_CARDS = 2;
	public static final int IN_GAME = 3;

	private int state = NO_CARDS;
	
	private Map<String, String> serverPairs;
	private Map<String, GameServerThread> allSocks;
	private String currentPlayer = "";
	private ServerDealer dealer;
	private String oneName;
	private RandomGenerator rgen;
	private GameServerLogger log;

	public InGameProtocol(Map<String, GameServerThread> dict, Map<String, String> pairs, String n) {
		allSocks = dict;
		serverPairs = pairs;
		dealer = new ServerDealer();
		oneName = n;
		rgen = RandomGenerator.getInstance();
		currentPlayer = randomlySelectPlayer();
		log = GameServerLogger.getLogger();
		dealOutCards();
	}
	
	private String randomlySelectPlayer() {
		List<String> names = getPlayerNames();
		return names.get(rgen.nextInt(0, (names.size()-1)));
	}
	
	public void dealOutCards() {
		List<String> names = getPlayerNames();
		dealTeamsToThreads(names);
		dealTricksToThreads(names);
		enablePlayerTurn(names);
	}
	
	public void enablePlayerTurn(List<String> names) {
		enablePlayerToStart(currentPlayer, names);
		currentPlayer = serverPairs.get(currentPlayer);
	}
	
	private void enablePlayerToStart(String name, List<String> names) {
		GameServerThread gt = allSocks.get(name);
		gt.notifyTurn(true);
		notifyOthersTurnGiven(names, name);
	}
	
	private List<String> getPlayerNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.add(oneName);
		names.add(serverPairs.get(oneName));
		return names;
	}
	
	private void dealTricksToThreads(List<String> names) {
		for(String n:names) {
			dealTricksToThread(n, names);
		}
	}
	
	private void dealCardToClient(String name, String card, String command, boolean myHand) {
		GameServerThread t = allSocks.get(name);
		String preCmd = Constants.CMD_PART_OPPO;
		if(myHand) {
			preCmd = Constants.CMD_PART_ME;
		}
		String cardStr = preCmd + command + Constants.CMD_SEP_ARG + card + Constants.CMD_ARG_END;
		log.logMessage(name + " sending " + cardStr);
		t.sendCommandToClient(cardStr);
	}
	
	private void notifyOthersOfCardAdded(List<String> names, String name, String command, String card) {
		for(String n:names) {
			if(!n.equals(name)) {
				dealCardToClient(n, card, command, false);
			}
		}
	}
	
	private void notifyOthersTurnGiven(List<String> names, String name) {
//		Debug.println("turn given to " + name);
		for(String n:names) {
			if(!n.equals(name)) {
//				Debug.println("name: " +  n + " not equal to " + name);
				GameServerThread gt = allSocks.get(n);
				gt.notifyTurn(false);
			}
		}
	}
	
	private void notifyOthersOfMessage(String name, String command) {
		List<String> names = getPlayerNames();
		for(String n: names) {
			if(!n.equals(name)) {
				dealMessageToClient(n, command);
			}
		}
	}
	
	private void dealMessageToClient(String name, String command) {
		GameServerThread t = allSocks.get(name);
		t.sendCommandToClient(command);
	}
	
	private void dealTeamsToThreads(List<String> names) {
		for(String n:names) {
			dealTeamsToThread(n, names);
		}
	}
	
	private void dealTeamsToThread(String name, List<String> names) {
		String cmd = Constants.CMD_ADD_TEAM;
		String card = "";
		for(int i = 0; i < Constants.TEAM_HAND_SIZE; i++) { 
			if(i > 0) {
				card += Constants.CMD_CARD_DELIMITER;
			}
			card += dealer.dealTeammateCard();
		}
		dealCardToClient(name, card, cmd, true);
		notifyOthersOfCardAdded(names, name, cmd, card);
	}
	
	private void dealTricksToThread(String name, List<String> names) {
		String cmd = Constants.CMD_ADD_TRICK;
		String card = "";
		for(int i = 0; i < Constants.TRICK_HAND_SIZE; i++) {
			if(i > 0) {
				card += Constants.CMD_CARD_DELIMITER;
			}
			card += dealer.dealTrickCard();
		}
		dealCardToClient(name, card, cmd, true);
		notifyOthersOfCardAdded(names, name, cmd, card);
	}
	
	private List<GameServerThread> getPlayerThreads() {
		ArrayList<GameServerThread> threads = new ArrayList<GameServerThread>();
		threads.add(allSocks.get(oneName));
		threads.add(allSocks.get(serverPairs.get(oneName)));
		return threads;
	}

//	/* I might not even need this */
//	public String processInput(String name, String theInput) {
//		notifyOthersOfMessage(name, theInput);
//		return "successfully told everyone about " + name + "'s move";
//	}

	public String getName() {
		return oneName;
	}

	public boolean isInGame() {
		return state == IN_GAME;
	}

	public void setGameState(int s) {
		state = s;
	}
}
