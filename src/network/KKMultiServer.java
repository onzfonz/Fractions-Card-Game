package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
 
public class KKMultiServer {
    public static void main(String[] args) throws IOException {
 
        ServerSocket serverSocket = null;
        ArrayList<KnockKnockServerThread> sockets = new ArrayList<KnockKnockServerThread>();
        try {
            serverSocket = new ServerSocket(1603);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1603.");
            System.exit(1);
        }
 
        Socket clientSocket = null;
        while(true) {
        	try {
        		clientSocket = serverSocket.accept();
        		KnockKnockServerThread temp = new KnockKnockServerThread(clientSocket);
        		temp.start();
        		sockets.add(temp);
        	} catch (IOException e) {
        		System.err.println("Accept failed.");
        		System.exit(1);
        	}
        	
        }
 
    }
}