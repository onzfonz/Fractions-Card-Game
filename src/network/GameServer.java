package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import basic.Constants;
 
public class GameServer {
    public static void main(String[] args) throws IOException {
 
        ServerSocket serverSocket = null;
        ArrayList<GameServerThread> sockets = new ArrayList<GameServerThread>();
        Map<String, GameServerThread> socketDict = Collections.synchronizedMap(new HashMap<String, GameServerThread>());
        Map<String, String> pairings = Collections.synchronizedMap(new HashMap<String, String>());
        try {
            serverSocket = new ServerSocket(Constants.SOCKET_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + Constants.SOCKET_PORT + ".");
            System.exit(1);
        }
 
        Socket clientSocket = null;
        System.out.println("Server started");
        while(true) {
        	try {
        		clientSocket = serverSocket.accept();
        		GameServerThread temp = new GameServerThread(clientSocket, socketDict, pairings);
        		temp.start();
        		sockets.add(temp);
			} catch (Exception e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
        }
 
    }
}