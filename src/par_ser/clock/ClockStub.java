package par_ser.clock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Protokol: # => regrex Positionen von # beim Senden an Server: 1. Command 2.
 * Time = Int || null
 * 
 * Positionen von # beim Erhalten vom Server: 1. Time = Int || null 2. Exception
 * || null 3. Message || null 4. ...
 */

public class ClockStub implements Clocks {

	Socket talkSocket;
	BufferedReader fromServer;
	OutputStreamWriter toServer;

	public ClockStub(String ipadress, int port) throws UnsupportedEncodingException, IOException {

		talkSocket = new Socket(ipadress, port);
		fromServer = new BufferedReader(new InputStreamReader(talkSocket.getInputStream(), "Cp1252"));
		toServer = new OutputStreamWriter(talkSocket.getOutputStream(), "Cp1252");
	}

	public ClockStub(InetAddress inetAdress, int port) throws IOException {

		talkSocket = new Socket(inetAdress, port);
		fromServer = new BufferedReader(new InputStreamReader(talkSocket.getInputStream(), "Cp1252"));
		toServer = new OutputStreamWriter(talkSocket.getOutputStream(), "Cp1252");
	}

	public void start() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("s\n");
		String[] msg =  receiveMsg();
		determineException(msg);
	}

	public void reset() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("r\n");
		String[] msg =  receiveMsg();
		determineException(msg);
	}

	public long getTime() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("g\n");
		String[] msg =  receiveMsg();
		determineException(msg);
		return Long.parseLong(msg[0]);
	}

	public void waitTime(long time) throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("w#" + time + "\n");
		String[] msg =  receiveMsg();
		determineException(msg);
	}

	public long halt() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("h\n");
		String[] msg =  receiveMsg();
		determineException(msg);
		return Long.parseLong(msg[0]);
	}

	public void conTinue() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("c\n");
		String[] msg =  receiveMsg();
		determineException(msg);
	}

	public void exit() throws IllegalCmdException, IllegalArgumentException, IOException {
		sendMsg("e\n");


		toServer.close(); // close writer
		fromServer.close(); // close reader
		talkSocket.close(); // close socket

	}

	// Hilfsfunktion -- Sendet zum Server und empfängt
	private void sendMsg(String msg) throws IOException{
		toServer.write(msg);
		toServer.flush(); // force message to be sent
		
	}
	private String[] receiveMsg() throws IOException{
		String result = fromServer.readLine();
		return result.split("#");
	}

	private void determineException(String[] msg) throws IllegalCmdException, IllegalArgumentException {
		if (msg[1].equals("IllegalCmdException")) { // Unerlaubter Befehl (Zustand Clock)
			throw new IllegalCmdException(msg[2]);
		} else if (msg[1].equals("IllegalArgumentException")) { // Grammatikfehler
			throw new IllegalArgumentException(msg[2]);
		}
	}

}
