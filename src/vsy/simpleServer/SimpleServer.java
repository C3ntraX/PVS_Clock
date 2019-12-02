package vsy.simpleServer;

import java.net.*;
import java.io.*;

public class SimpleServer {
	
	public static void main (String args[]) {
		Socket talkSocket;
		BufferedReader fromClient;
		OutputStreamWriter toClient;
		String stringToConvert;
		
		try {
			ServerSocket listenSocket = new ServerSocket (4711);
			// wait for a client and serve it
			while(true) {
				talkSocket = listenSocket.accept();
				// incoming messages are char based (text)
				fromClient = new BufferedReader (new InputStreamReader(talkSocket.getInputStream(), "Cp1252"));
				
				// outgoing messages are char based (text)
				toClient = new OutputStreamWriter(talkSocket.getOutputStream(),"Cp1252");
				
				stringToConvert = fromClient.readLine();
				
				String[] text = stringToConvert.split("\\$");
				
				String cost = ((" "+text[1].length()*1.5)+"Ct").replace(".", ",");
				if(text[1].toUpperCase().equals("ENDE")) {
					toClient.write("Server shutdown" + cost + "\n");
					
					toClient.close();	// close writer & da er geschlossen wird, flush(t) er auch
					fromClient.close(); // close reader
					talkSocket.close(); // close talksocket
					listenSocket.close();	// close listensocket
					break;	
				} else if(text[0].equals("down")) {
					toClient.write(text[1].toLowerCase()+ cost + "\n");
				} else if(text[0].equals("up")) {
					toClient.write(text[1].toUpperCase()+ cost + "\n");
				}
				
				toClient.close();// close writer & da er geschlossen wird, flush(t) er auch
				fromClient.close(); // close reader
				talkSocket.close(); // close talksocket
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Anmerkung:	Price vorab und nicht im String mit einbauen; Server immer überprüfen, ob der EIngang Grammatisch korrekt ist => Pattern mit reg expression
	 * 
	 * 
	 */
}
