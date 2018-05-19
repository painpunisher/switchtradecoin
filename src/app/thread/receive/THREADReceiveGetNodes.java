package app.thread.receive;

import app.net.NETCOMMANDS;
import app.start.util.GO;

public class THREADReceiveGetNodes implements Runnable {

	private String mHost;
	private NETCOMMANDS mGetNodes;
	private int mPort;

	public THREADReceiveGetNodes(String host, int port, Object obj) {
		mHost = host;
		mPort = port;
		mGetNodes = (NETCOMMANDS) obj;
	}

	@Override
	public void run() {
		sendNodes();
	}

	private void sendNodes() {
		if (mGetNodes == NETCOMMANDS.GETNODES) {
			GO.sender.sendNodes(mHost, mPort);
		}
	}

}
