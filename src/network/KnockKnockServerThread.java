package network;

import java.io.*;
import java.net.*;

public class KnockKnockServerThread extends Thread {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private KnockKnockProtocol kkp;

	public KnockKnockServerThread(Socket client) {
		clientSocket = client;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}catch (IOException e) {
			e.printStackTrace();
		}
        kkp = new KnockKnockProtocol();
	}
	
	public void run() {
		String inputLine, outputLine;
		outputLine = kkp.processInput(null);
        out.println(outputLine);
 
        try {
        	while((inputLine = in.readLine()) != null) {
        		outputLine = kkp.processInput(inputLine);
        		out.println(outputLine);
        		if(outputLine.equals("Bye")) {
        			break;
        		}
        	}
        	out.close();
        	in.close();
        	clientSocket.close();
        }catch(IOException e) {
        	e.printStackTrace();
        }
	}
}
