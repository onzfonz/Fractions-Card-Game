package tutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class SimpleGameServerThread extends Thread {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private SimpleGameProtocol sgp;
	private Map<String, SimpleGameServerThread> allSocks;
	private Map<String, String> serverPairs;
	
	public static final int INACTIVE_MS = 20000;

	public SimpleGameServerThread(Socket client, Map<String, SimpleGameServerThread> dict, Map<String, String> pairs) {
		clientSocket = client;
		try {
			//clientSocket.setSoTimeout(INACTIVE_MS);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}catch (SocketException se) {
			System.err.println("Unable to set socket option SO_TIMEOUT");
		}catch (IOException e) {
			e.printStackTrace();
		}
		allSocks = dict;
		serverPairs = pairs;
        sgp = new SimpleGameProtocol(dict, pairs, this);
	}
	
	@Override
	public void run() {
		String inputLine, outputLine;
 
        try {
        	while((inputLine = in.readLine()) != null) {
        		outputLine = sgp.processInput(inputLine);
        		out.println(outputLine);
        		if(outputLine.equals("Bye")) {
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
	
	public void propogateMessage(String n, String input) {
		String key = sgp.getName();
		String otherKey = serverPairs.get(key);
		SimpleGameServerThread otherThread = allSocks.get(otherKey);
		otherThread.sendToClient("" + n + ": " + input);
	}
	
	public void notifyAdd(String name) {
		if(sgp.inALobby()) {
			sendToClient(".add " + name);
		}
	}
	
	public boolean isInALobby() {
		return sgp.inALobby();
	}
	
	public void notifyOppoLeft(String name) {
		sgp.returnToLobby();
		sendToClient(".oppoleft " + name);
	}
	
	public void notifyRemove(String name) {
		if(isInALobby()) {
			sendToClient(".remove " + name);
		}
	}
	
	public void startClientGame(String name) {
		assert(isInALobby());
		updateState(SimpleGameProtocol.IN_GAME);
		sendToClient(".start " + name);
	}
	
	public void updateState(int s) {
		sgp.setGameState(s);
	}
	
	public void sendToClient(String msg) {
		out.println(msg);
	}
}
