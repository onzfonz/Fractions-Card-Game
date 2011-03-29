package network;

/* 
 * This will be modified from simple so that it will be able to follow the game rules.
 */

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import basic.Constants;

public class GameProtocol {
	public static final int INIT = 0;
	public static final int WAITING_FOR_NAME = 1;
	public static final int NO_PARTNER = 2;
	public static final int IN_GAME = 3;

	private int state = WAITING_FOR_NAME;
	
	private Map<String, GameServerThread> allSocks;
	private Map<String, String> serverPairs;
	private GameServerThread thread;
	private String name;
	private InGameProtocol inGameRules;

	public GameProtocol(Map<String, GameServerThread> dict, Map<String, String> pairs, GameServerThread t) {
		allSocks = dict;
		serverPairs = pairs;
		thread = t;
	}

	/* Ewww....this is messy */
	public String processInput(String theInput) {
		String theOutput = null;
		if(theInput != null) {
			theInput = theInput.toLowerCase();
		}
		if (state == WAITING_FOR_NAME) {
			synchronized(allSocks) {
				if (!allSocks.containsKey(theInput)) {
					name = theInput;
					theOutput = enterGameLobby();
				}else{
					theOutput = Constants.NET_CMD_ERR + " That name has been taken already! ";
				}
			}
		} else if (state == NO_PARTNER) {
			if(name.equals(theInput)) {
				return "You can't connect to yourself! Try again";
			}
			if(serverPairs.containsKey(name)) {
				setGameState(IN_GAME);
				return "You already have your partner - " + serverPairs.get(name);
			}
			synchronized(allSocks) {
				if(!allSocks.containsKey(theInput)) {
					return "That name isn't on the list! Try again";
				}
			}
			synchronized(serverPairs) {
				if(serverPairs.containsKey(theInput)) {
					theOutput = "That person already has a partner. Try again";            		
				}else {
					addServerPair(name, theInput);
					theOutput = Constants.NET_CMD_PLAY + " " + theInput;
					setGameState(IN_GAME);
					/* will need to be a way of setting rules for other thread as well */
				}
			}
		} else if (state == IN_GAME) {
			if(theInput.equals(Constants.NET_CMD_READY_TO_START)) {
				/* Maybe this should happen once osvaldo has said that he is ready */
				inGameRules = new InGameProtocol(allSocks, serverPairs, name);
				setInGameProtocolForPartner(inGameRules);
			}else if(theInput.startsWith(Constants.CMD_NEW_ROUND)) {
				inGameRules.dealOutCards();
			}else {
				thread.relayClientCommand(theInput);
				theOutput = "Successfully sent command" + theInput;
			}
		}
		return theOutput;
	}

	public String getName() {
		return name;
	}

	private String generateNames() {
		String nameList = name + ", who are you playing against?";
		Set<String> keys = allSocks.keySet();
		for(String key:keys) {
			if(!key.equals(name)) {
				//nameList+=key+", ";
				GameServerThread otherT = allSocks.get(key);
				if(otherT.isInALobby()) {
					thread.notifyAdd(key);
				}
			}

		}
		//		nameList += ")";
		return nameList;
	}

	public boolean isInGame() {
		return state == IN_GAME;
	}

	public boolean inALobby() {
		return state == NO_PARTNER;
	}
	
	/* Assumes you have access already */
	private String enterGameLobby() {
		setGameState(NO_PARTNER);
		notifyAllSocksOfNewcomer();
		allSocks.put(name, thread);
		return generateNames();
	}
	
	public void returnToLobby() {
		synchronized(allSocks) {
			enterGameLobby();
		}
	}

	private void notifyAllSocksOfNewcomer() {
		Set<String> keys = allSocks.keySet();
		for(String key:keys) {
			GameServerThread st = allSocks.get(key);
			st.notifyAdd(name);
		}
	}
	
	public void notifyGameStart(InGameProtocol igp) {
		setGameState(IN_GAME);
		inGameRules = igp;
	}
	
	public void removePairing() {
		GameServerThread otherT = null;
		
		synchronized(serverPairs) {
			if(serverPairs.containsKey(name)) {
				String affected = serverPairs.get(name);
				serverPairs.remove(affected);
				serverPairs.remove(name);
				otherT = allSocks.get(affected);
			}
		}
		if(otherT != null) {
			otherT.notifyOppoLeft(name);
		}
	}

	public void notifyAllSocksOfMIA() {
		synchronized(allSocks) {
			allSocks.remove(name);
			Set<String> keys = allSocks.keySet();
			for(String key:keys) {
				GameServerThread st = allSocks.get(key);
				st.notifyRemove(name);
			}
		}
	}

	private void notifyAllSocksOfNewlyweds(String other) {
		Set<String> keys = allSocks.keySet();
		for(String key:keys) {
			GameServerThread st = allSocks.get(key);
			st.notifyRemove(other);
			st.notifyRemove(name);
		}
	}

	private void addServerPair(String name, String otherName) {
		synchronized(serverPairs) {
			serverPairs.put(name, otherName);
			serverPairs.put(otherName, name);
		}
		GameServerThread otherT;
		synchronized(allSocks) {
			otherT = allSocks.get(otherName);
			otherT.startClientGame(name, inGameRules);
			notifyAllSocksOfNewlyweds(otherName);
		}
	}
	
	private void setInGameProtocolForPartner(InGameProtocol igp) {
		GameServerThread gst = getPartnerThread(name);
		gst.setInGameProtocol(igp);
	}
	
	public void setInGameProtocol(InGameProtocol igp) {
		inGameRules = igp;
	}
	
	private GameServerThread getPartnerThread(String n) {
		return allSocks.get(serverPairs.get(n));
	}

	public void setGameState(int s) {
		state = s;
	}
}
