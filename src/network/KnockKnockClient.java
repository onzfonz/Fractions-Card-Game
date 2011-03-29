package network;
import java.io.*;
import java.net.*;
 
public class KnockKnockClient {
    public static void main(String[] args) throws IOException {
 
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
 
        try {
            socket = new Socket("127.0.0.1", 1603);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
 
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String fromServer, fromUser;
 
		//System.out.println(in.readLine()); //Uncomment to debug
 
		while((fromServer = in.readLine()) != null) {
			System.out.println("Server: " + fromServer);
			if(fromServer.equals("Bye.")) {
				break;
			}
			fromUser = read.readLine();
			if(fromUser != null) {
				System.out.println("Client: " + fromUser);
				out.println(fromUser);
			}
		}
 
        out.close();
        in.close();
        read.close();
        socket.close();
    }
}
