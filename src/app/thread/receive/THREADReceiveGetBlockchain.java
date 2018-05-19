package app.thread.receive;

public class THREADReceiveGetBlockchain implements Runnable{
	
	private String mHost;
	private int mPort;
	
	public THREADReceiveGetBlockchain(String host, int port){
		mHost = host;
		mPort = port;
		System.out.println(mHost + " - " + mPort);
	}
	
	@Override
	public void run() {
//		GO.sender.sendBlockchain(mHost, mPort);
	}
	
}
