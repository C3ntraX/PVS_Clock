package mux.clock;


import java.io.*;
import java.util.StringTokenizer; // for command decoding
import java.util.regex.Pattern; // for command syntax validation

public class ClockClient {

	class Command {
		public int cmd;
		public long parameter;

		public Command(int cmd) {
			this.cmd = cmd;
		}

		public Command(int cmd, long parameter) {
			this.cmd = cmd;
			this.parameter = parameter;
		}
	}

	// command keycodes
	static final int CMD_START = 1;
	static final int CMD_STOP = 2;
	static final int CMD_EXIT = 3;
	static final int CMD_HALT = 4;
	static final int CMD_WAITTIME = 5;
	static final int CMD_CONTINUE = 6;
	static final int CMD_GETTIME = 7;
	static final int CMD_RESET = 8;

	void display(String msg)
	// displays a messsage on the screen
	{
		System.out.println("  " + msg);
	}

	void prompt(String msg)
	// sends a prompt messsage for user (command) input
	{
		System.out.print(msg);
	}

	Command getCommand()
	// read user command from keyboard
	{
		// allowed command pattern
		Pattern commandSyntax = Pattern.compile("s|c|h|r|e|g|w +[1-9][0-9]*");

		String cmdText = null;

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

//		 loop until correct command has been entered
		while (true) {
			try {
				prompt("command [s|c|h|r|e|g|w]: ");
				cmdText = in.readLine();
				if (!commandSyntax.matcher(cmdText).matches())
					throw new Exception();
				break; // leave loop here if correct command detected
			} catch (Exception e) {
				display("command syntax error");
			}
		}

		// decode command and return with Command object
		StringTokenizer st = new StringTokenizer(cmdText);
		switch (st.nextToken().charAt(0)) { // 1. token=command
		case 'c':
			return new Command(CMD_CONTINUE);
		case 'g':
			return new Command(CMD_GETTIME);
		case 's':
			return new Command(CMD_START);
		case 'w':
			return new Command(CMD_WAITTIME, Long.parseLong(st.nextToken()));
		// 2. token=parameter
		case 'h':
			return new Command(CMD_HALT);
		case 'r':
			return new Command(CMD_RESET);
		default: // case 'e' and anything else
			return new Command(CMD_EXIT);
		}
	}

	void run() {
		
		try {
			Clocks clock = new ClockStub("localhost", 4711);
			prompt("accepted commands:\n");
			display("s[tart]     h[old]   c[ontinue]   r[eset])");
			display("g[et time]  e[xit]   w[ait] 4711\n");

			Command command;

			do {
				command = getCommand();
				try {
					switch (command.cmd) {
					case CMD_CONTINUE:
						clock.conTinue();
						display("clock continued");
						break;
					case CMD_GETTIME:
						display("elapsed time = " + clock.getTime() + "ms");
						break;
					case CMD_START:
						clock.start();
						display("clock started");
						break;
					case CMD_WAITTIME:
						clock.waitTime(command.parameter);
						display("wait finished");
						break;
					case CMD_HALT:
						display("clock halted, elapsed time = " + clock.halt() + "ms");
						break;
					case CMD_RESET:
						clock.reset();
						display("clock resetted");
						break;
					case CMD_EXIT:
						clock.exit();
						clock = null; // exit darf nur im State Ready sein, somit darf nicht in jeder Situation clock = null sein!
						display("program stop");
						break;
					default:
						display("Illegal command");
						break;
					}
				} catch (IllegalCmdException| IllegalArgumentException | IOException e) {
					display(e.getMessage());
				}
			} while (command.cmd != CMD_EXIT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClockClient clockClient = new ClockClient();
		clockClient.run();
	}
}
