package par_ser.clock;

import java.io.IOException;

public interface Clocks {

	public void start() throws IllegalCmdException, IllegalArgumentException, IOException;
	public void reset() throws IllegalCmdException, IllegalArgumentException, IOException;
	public long getTime() throws IllegalCmdException, IllegalArgumentException, IOException;
	public void waitTime(long time) throws IllegalCmdException, IllegalArgumentException, IOException;
	public long halt() throws IllegalCmdException, IllegalArgumentException, IOException;
	public void conTinue() throws IllegalCmdException, IllegalArgumentException, IOException;
	public void exit() throws IllegalCmdException, IllegalArgumentException, IOException;
}
