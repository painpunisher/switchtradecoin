package app.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ReceiveExecuter {

	public static ScheduledExecutorService RECEIVEEXECUTER;
	
	public static void init(){
		RECEIVEEXECUTER = Executors.newScheduledThreadPool(32);
	}
	
}
