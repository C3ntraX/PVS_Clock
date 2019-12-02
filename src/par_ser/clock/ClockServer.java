package par_ser.clock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Protokol: # => regrex Positionen von # beim Erhalten vom Client: 1. Command
 * 2. Time = Int || null
 * 
 * Positionen von # beim Senden an Client: 0. Time = Int || null 1. Exception ||
 * null 2. Message || null 4. ...
 */

public class ClockServer {

	public static void main(String[] args) throws IOException {

		ServerSocket socket = new ServerSocket(4711);
		
		while (true) {
			// Paralleler Serieller Server
			new ClockThread(socket.accept()).start();

//			 Serieller Server
//			processClient(socket.accept());

		}
//		socket.close();
	}

	static void processClient(Socket talkSocket) {
		BufferedReader fromClient;
		OutputStreamWriter toClient;
		String cmdText;
		String[] toClientMsg;

		try {
			Pattern commandSyntax = Pattern.compile("s|c|h|r|e|g|w#+[1-9][0-9]*");

			// Gemeinsame Stoppuhr
			Clocks clock = new Clock();
			// incoming messages are char based (text)
			fromClient = new BufferedReader(new InputStreamReader(talkSocket.getInputStream(), "Cp1252"));

			// outgoing messages are char based (text)
			toClient = new OutputStreamWriter(talkSocket.getOutputStream(), "Cp1252");

			boolean isRunning = true;

			while (isRunning) {
				// incoming message
				cmdText = fromClient.readLine();

				toClientMsg = new String[3]; // 0 = Time; 1 = Exception; 2 = Message
				try {

					if (commandSyntax.matcher(cmdText).matches()) {
						// decode command and return with Command object

						String[] fromClientMsg = cmdText.split("#"); // Regrex = #

						switch (fromClientMsg[0]) { // 0. =command
						case "c":
							clock.conTinue();
							break;
						case "g":
							toClientMsg[0] = clock.getTime() + "";
							break;
						case "s":
							clock.start();
							break;
						case "w":
							clock.waitTime(Long.parseLong(fromClientMsg[1]));
							break;
						// 2. token=parameter
						case "h":
							toClientMsg[0] = clock.halt() + "";
							break;
						case "r":
							clock.reset();
							break;
						default: // case 'e' and anything else
							clock.exit();			
							clock = null; // Garbage Collector wird aktiv (Juen)

							isRunning = false; // While Schleife unterbrechen
							
							toClient.close();// close writer
							fromClient.close(); // close reader
							talkSocket.close(); // close talksocket
						}
					} else {
						throw new IllegalArgumentException("Wrong expression, use command [s|c|h|r|e|g|w]: ");
					}
				} catch (IllegalCmdException e) {
					toClientMsg[1] = "IllegalCmdException";
					toClientMsg[2] = e.getMessage();
				} catch (IllegalArgumentException e) {
					toClientMsg[1] = "IllegalArgumentException";
					toClientMsg[2] = e.getMessage();
				}

				// State von Exit berücksichtigen!!!
				if (isRunning) {
					// Message to Client
					System.out.println(toClientMsg[0] + "#" + toClientMsg[1] + "#" + toClientMsg[2]);
					toClient.write(toClientMsg[0] + "#" + toClientMsg[1] + "#" + toClientMsg[2] + "#\n");
					toClient.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
