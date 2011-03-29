package network;
import java.io.*;
import java.net.*;
 
public class SimpleGameClient implements Runnable, GClientInterface {
	private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader read = null;
    private SimpleGameClientThread clientThread = null;
	private Thread thread = null;
	
	public static void main(String[] args) throws IOException {
    	new SimpleGameClient();
    }
    
    public SimpleGameClient() {
        try {
            socket = new Socket("127.0.0.1", 1604);
            out = new PrintWriter(socket.getOutputStream(), true);
            start();
       } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }
    
    public void start() throws IOException {
    	read = new BufferedReader(new InputStreamReader(System.in));
        if(thread == null) {
        	clientThread = new SimpleGameClientThread(this, socket);
        	thread = new Thread(this);
        	thread.start();
        }
    }
    
    public void run() {
    	System.out.println("Starting:");
    	while(thread != null) {
    		try {
    			String fromUser = read.readLine();
    			if(fromUser != null) {
    				System.out.println("Client: " + fromUser);
    				out.println(fromUser);
    			}else{
    				thread = null;
    			}
    		}catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	closeAll();
    }
    
    public void handleMsg(String fromServer) {
    	System.out.println(fromServer);
    }
    
    public void closeAll() {
    	try {
    		if(out != null) out.close();
            if(read != null) read.close();
            if(socket != null) socket.close();
            clientThread.close();
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }
}
