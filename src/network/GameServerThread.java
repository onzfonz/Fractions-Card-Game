package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import basic.Constants;

public class GameServerThread extends Thread {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private GameProtocol sgp;
	private Map<String, GameServerThread> allSocks;
	private Map<String, String> serverPairs;

	public static final int INACTIVE_MS = 20000;

	public GameServerThread(Socket client, Map<String, GameServerThread> dict, Map<String, String> pairs) {
		clientSocket = client;
		try {
			//clientSocket.setSoTimeout(INACTIVE_MS);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("setting up client from: " + client.getInetAddress());
		}catch (SocketException se) {
			System.err.println("Unable to set socket option SO_TIMEOUT");
		}catch (IOException e) {
			e.printStackTrace();
		}
		allSocks = dict;
		serverPairs = pairs;
		sgp = new GameProtocol(dict, pairs, this);
	}

	public void run() {
		String inputLine, outputLine;

		try {
			while((inputLine = in.readLine()) != null) {
				outputLine = sgp.processInput(inputLine);
				if(outputLine != null) {
					out.println(outputLine);
				}
				if(outputLine != null && outputLine.equals("Bye")) {
					break;
				}
			}
			out.close();
			in.close();
			clientSocket.close();
		}catch(Exception e) {
			/*if(e instanceof InterruptedIOException) {
        		String name = sgp.getName();
        		System.out.println(name + " was idle for too long!");
				sgp.notifyAllSocksOfMIA();
				sgp.removePairing();
        	} else */if(e instanceof SocketException) {
        		String name = sgp.getName();
        		System.out.println(name + " quit!");
        		sgp.notifyAllSocksOfMIA();
        		sgp.removePairing();
        	}else if(e instanceof RuntimeException || e instanceof IOException) {
        		e.printStackTrace();
        	}
		}
	}

	public void relayClientCommand(String message) {
		String oppoName = serverPairs.get(sgp.getName());
		GameServerThread otherT = allSocks.get(oppoName);
		otherT.sendCommandToClient(message);
	}


	public void notifyAdd(String name) {
		if(sgp.inALobby()) {
			sendToClient(Constants.NET_CMD_ADD + " " + name);
		}
	}

	public boolean isInALobby() {
		return sgp.inALobby();
	}

	public void notifyOppoLeft(String name) {
		sgp.returnToLobby();
		sendToClient(Constants.NET_CMD_QUIT + " " + name);
	}

	public void notifyRemove(String name) {
		if(isInALobby()) {
			sendToClient(Constants.NET_CMD_REMOVE + " " + name);
		}
	}

	public void notifyTurn(boolean ownTurn) {
		String preMsg = Constants.CMD_PART_OPPO;
		if(ownTurn) {
			preMsg = Constants.CMD_PART_ME;
		}
		String msg = preMsg + Constants.CMD_TURN;
		sendCommandNameToClient(msg);
	}
	
	public void setInGameProtocol(InGameProtocol igp) {
		sgp.setInGameProtocol(igp);
	}

	public void startClientGame(String name, InGameProtocol igp) {
		assert(isInALobby());
		sgp.notifyGameStart(igp);
		sendToClient(Constants.NET_CMD_START + " " + name);
	}

	public void updateState(int s) {
		sgp.setGameState(s);
	}
	
	private void sendCommandNameToClient(String cmdName) {
		sendCommandToClient(cmdName + Constants.CMD_SEP);
	}

	public void sendCommandToClient(String msg) {
		sendToClient(Constants.NET_CMD_GAME + " " + msg);
	}

	public void sendToClient(String msg) {
		out.println(msg);
		System.out.println(sgp.getName() + " sent to other " + msg);
	}
}
