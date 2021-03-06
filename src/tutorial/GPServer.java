package tutorial;

import java.net.*;
import java.io.*;
 
public class GPServer {
    public static void main(String[] args) throws IOException {
 
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1602);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }
 
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
 
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String int1,int2;
	int num1=0,num2=0;
 
	out.println("server: Connected");//uncomment for debug
 
	int1 = in.readLine();
	System.out.println(int1);
	int2 = in.readLine();
	System.out.println("*"+int2);
 
 
	try
	{  
        num1=Integer.parseInt(int1);
        num2=Integer.parseInt(int2);
        }
        catch(NumberFormatException nfe)
        {
        	System.out.println("Numbers not intergers");
        	out.println("Numbers not intergers");
        }
        System.out.println("="+num1*num2);
        out.println(String.valueOf(num1*num2));
 
 
 
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}