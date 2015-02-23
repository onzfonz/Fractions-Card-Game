package tutorial;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
 
public class SimpleGameServer {
    public static void main(String[] args) throws IOException {
 
        ServerSocket serverSocket = null;
        ArrayList<SimpleGameServerThread> sockets = new ArrayList<SimpleGameServerThread>();
        Map<String, SimpleGameServerThread> socketDict = Collections.synchronizedMap(new HashMap<String, SimpleGameServerThread>());
        Map<String, String> pairings = Collections.synchronizedMap(new HashMap<String, String>());
        try {
            serverSocket = new ServerSocket(1604);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1604.");
            System.exit(1);
        }
 
        Socket clientSocket = null;
        System.out.println("Server started");
        while(true) {
        	try {
        		clientSocket = serverSocket.accept();
        		SimpleGameServerThread temp = new SimpleGameServerThread(clientSocket, socketDict, pairings);
        		temp.start();
        		sockets.add(temp);
			} catch (Exception e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
        }
 
    }
}