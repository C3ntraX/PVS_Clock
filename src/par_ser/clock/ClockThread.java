package par_ser.clock;

import java.net.Socket;

public class ClockThread extends Thread{
	private Socket talkSocket;
	
	public ClockThread (Socket talkSocket){
		this.talkSocket = talkSocket;
	}
	
	@Override
	public void run(){
		ClockServer.processClient(talkSocket);
		
	}

}
