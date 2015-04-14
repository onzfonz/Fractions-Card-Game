package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class SimpleGameClientThread extends Thread {
	private Socket socket = null;
	private GClientInterface client = null;
	private BufferedReader streamIn = null;

	
	// in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	 
	public SimpleGameClientThread(GClientInterface sgc, Socket sock) {  
		client = sgc;
		socket = sock;
		open();  
		start();
	}
	
	public void open() {
		try {  
			streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		}
		catch(IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			//	client.stop();
		}
	}
	public void close() throws IOException {  
		if (streamIn != null) streamIn.close();
	}
	@Override
	public void run() {
		while (true) {  
			try {
				String fromServer = streamIn.readLine();
				client.handleMsg(fromServer);
			}
			catch(IOException ioe) {  
				System.out.println("Listening error: " + ioe.getMessage());
			//	client.stop();
			}
		}
	}

}
