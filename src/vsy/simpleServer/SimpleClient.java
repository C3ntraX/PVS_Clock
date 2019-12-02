package vsy.simpleServer;

import java.net.*;
import java.io.*;

public class SimpleClient {

	public static void main (String args[]) {
		String s="down$Das Pferd frisst Gurkensalat\n";
//		String s ="ende\n";
		try {
			Socket talkSocket = new Socket ("localhost", 4711);
			
			BufferedReader fromServer = new BufferedReader (new InputStreamReader(talkSocket.getInputStream(),"Cp1252"));

			OutputStreamWriter toServer = new OutputStreamWriter(talkSocket.getOutputStream(), "Cp1252");
			
			System.out.println("Send: "+s);
			
			
			
			toServer.write(s);
			toServer.flush();	//force message to be sent
			
			String result = fromServer.readLine();	//blocking read
			System.out.println("Receive: "+result);
			
			toServer.close();	//close writer
			fromServer.close();	//close reader
			talkSocket.close();	//close socket
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
