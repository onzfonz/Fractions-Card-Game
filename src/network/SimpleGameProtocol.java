package network;

import java.util.Map;
import java.util.Set;

public class SimpleGameProtocol {
	public static final int INIT = 0;
	public static final int WAITING_FOR_NAME = 1;
	public static final int NO_PARTNER = 2;
	public static final int IN_GAME = 3;

	private static final int NUMJOKES = 5;

	private int state = WAITING_FOR_NAME;
	private int currentJoke = 0;

	private Map<String, SimpleGameServerThread> allSocks;
	private Map<String, String> serverPairs;
	private SimpleGameServerThread thread;
	private String name;

	public SimpleGameProtocol(Map<String, SimpleGameServerThread> dict, Map<String, String> pairs, SimpleGameServerThread t) {
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
					theOutput = ".error That name has been taken already! ";
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
					theOutput = ".play " + theInput;
					setGameState(IN_GAME);
				}
			}
		} else if (state == IN_GAME) {
			thread.propogateMessage(name, theInput);
			theOutput = name + ": " + theInput;
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
				SimpleGameServerThread otherT = allSocks.get(key);
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
			SimpleGameServerThread st = allSocks.get(key);
			st.notifyAdd(name);
		}
	}
	
	public void removePairing() {
		SimpleGameServerThread otherT = null;
		
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
				SimpleGameServerThread st = allSocks.get(key);
				st.notifyRemove(name);
			}
		}
	}

	private void notifyAllSocksOfNewlyweds(String other) {
		Set<String> keys = allSocks.keySet();
		for(String key:keys) {
			SimpleGameServerThread st = allSocks.get(key);
			st.notifyRemove(other);
			st.notifyRemove(name);
		}
	}

	private void addServerPair(String name, String otherName) {
		synchronized(serverPairs) {
			serverPairs.put(name, otherName);
			serverPairs.put(otherName, name);
		}
		SimpleGameServerThread otherT;
		synchronized(allSocks) {
			otherT = allSocks.get(otherName);
			otherT.startClientGame(name);
			notifyAllSocksOfNewlyweds(otherName);
		}
		otherT.updateState(IN_GAME);

	}

	public void setGameState(int s) {
		state = s;
	}
}
