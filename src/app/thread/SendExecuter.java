package app.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SendExecuter  {

	public static ScheduledExecutorService SENDEXECUTER;
	
	public static void init(){
//		SENDEXECUTER = Executors.newCachedThreadPool();
		SENDEXECUTER = Executors.newScheduledThreadPool(32);
	}
	
}
