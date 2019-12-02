package par_ser.clock;



public class Clock implements Clocks{
	// clock state keycodes
	static final int ST_READY = 0;
	static final int ST_RUNNING = 1;
	static final int ST_HALTED = 2;
	static final int ST_EXIT = 3;

	// internal state variables
	int state; // state is one of {ST_READY, ST_RUNNING ... }
	long startTime; // to store start time
	long elapsedTime; // to store elapsed time after halt command

	// constructor
	public Clock() {
		state = ST_READY; // initial state is ST_READY
		startTime = 0;
		elapsedTime = 0;
	}

	// command execute methods
	public void start() throws IllegalCmdException {
		if (state != ST_READY)
			throw new IllegalCmdException("'start' not allowed in the actual context"+ state);
		startTime = System.currentTimeMillis();
		state = ST_RUNNING;
	}

	public void reset() throws IllegalCmdException {
		if ((state != ST_HALTED) && (state != ST_RUNNING))
			throw new IllegalCmdException("'reset' not allowed in the actual context "+ state);
		startTime = 0;
		elapsedTime = 0;
		state = ST_READY;
	}

	public long getTime() throws IllegalCmdException {
		if ((state != ST_HALTED) && (state != ST_RUNNING))
			throw new IllegalCmdException("'gettime' not allowed in the actual context "+ state);
		if (state == ST_RUNNING) {
			elapsedTime = System.currentTimeMillis() - startTime;
		}
		return elapsedTime;
	}

	// wait for time ms
	public void waitTime(long time) throws IllegalCmdException {
		if (state != ST_RUNNING)
			throw new IllegalCmdException("'wait' not allowed in the actual context"+ state);
		try {
			Thread.sleep(time);
		} catch (Exception ignore) {
		}
	}

	public long halt() throws IllegalCmdException {
		if (state != ST_RUNNING)
			throw new IllegalCmdException("'halt' not allowed in the actual context"+ state);
		elapsedTime = System.currentTimeMillis() - startTime;
		state = ST_HALTED;
		return elapsedTime;
	}

	public void conTinue() throws IllegalCmdException {
		if (state != ST_HALTED)
			throw new IllegalCmdException("'continue' not allowed in the actual context"+ state);
		startTime = System.currentTimeMillis() - elapsedTime;
		state = ST_RUNNING;
	}

	public void exit() throws IllegalCmdException {
		// Fehlerhaft mit Clockclient unverändert (Basis ist falsch)
//		if (state != ST_READY)	
//			throw new IllegalCmdException("'exit' not allowed in the actual context"+ state);
		state = ST_EXIT; // no way out
	}
}
