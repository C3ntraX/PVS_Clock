package mux.clock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Protokol: # => regrex Positionen von # beim Erhalten vom Client: 1. Command
 * 2. Time = Int || null
 * 
 * Positionen von # beim Senden an Client: 0. Time = Int || null 1. Exception ||
 * null 2. Message || null 4. ...
 */

public class ClockServer {

	private Selector events = null; // event multiplexer
	private ServerSocketChannel listenChannel;
	private Map<SocketChannel, Clock> clockMap; // SocketAdress => IP+Port || Channel selber (bessere Lösung) => key.attach

	// constructor puts server into listen mode
	public ClockServer() {
		try {
			
			// constructor puts server into listen mode
			events = Selector.open();
			// create a non-blocking server socket and
			// register it for connection request events
			listenChannel = ServerSocketChannel.open();
			listenChannel.configureBlocking(false);
			listenChannel.socket().bind(new InetSocketAddress(4711));
			listenChannel.register(events, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void main(String[] args) throws IOException {
		ClockServer server = new ClockServer();
		server.serverLoop();
	}

	private void serverLoop() throws IOException, ClosedSelectorException {
		Iterator<SelectionKey> selKeys;
		// infinite server loop

		while (true) {
			System.out.println("Server running......");
			events.select();
			// blocks until event occurs
			// process all pending events (might be more than 1)
			selKeys = events.selectedKeys().iterator();
			while (selKeys.hasNext()) {
				// get the selection key for the next event ...
				SelectionKey selKey = selKeys.next();
				// ... and remove it from the list to indicate
				// that it is being processed
				selKeys.remove();

				if (selKey.isReadable()) {
					// it is a "data are available to be read" event
					processRead(selKey);
				} else if (selKey.isAcceptable()) {
					// it is a "remote socket wants to connect" event
					processAccept();
				} else {
					System.out.println("Unknown event occured");
				}
			}
		}
	}

	private void processRead(SelectionKey selKey) {
		String cmdText;
		String[] toClientMsg;

		SocketChannel talkChan = null;

		// Gemeinsame Stoppuhr
//		Clocks clock = new Clock();

		try {
			talkChan = (SocketChannel) selKey.channel();
			Clock clock = clockMap.get(talkChan);
			
			Pattern commandSyntax = Pattern.compile("s|c|h|r|e|g|w#+[1-9][0-9]*");

			// incoming message
			cmdText = ChannelRW.recvTextMessage(talkChan);

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
			ChannelRW.sendTextMessage(talkChan, toClientMsg[0] + toClientMsg[1] + toClientMsg[2]);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			try {
				// always try to close talkChannel
				talkChan.close();
			} catch (IOException ignore) {
			}
		}
	}

	private void processAccept() {
		SocketChannel talkChannel = null;

		try {
			talkChannel = listenChannel.accept();

			talkChannel.configureBlocking(false);
			talkChannel.register(events, SelectionKey.OP_READ);
			clockMap.put(talkChannel, new Clock());
//			key.(new Clock()); // Eleganter
			System.out.println(talkChannel.getLocalAddress());
		} catch (IOException io) {
			io.printStackTrace();
			try {
				talkChannel.close();
			} catch (IOException ignore) {

			}
		}
	}
}
