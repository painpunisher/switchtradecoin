package app.thread.receive;

import app.net.NETCOMMANDS;
import app.start.util.GO;

public class THREADReceiveGetLatestBlockNo implements Runnable {

	private String mHost;
	private NETCOMMANDS mLatestBlockNo;
	private int mPort;

	public THREADReceiveGetLatestBlockNo(String host, int port, Object obj) {
		mHost = host;
		mPort = port;
		mLatestBlockNo = (NETCOMMANDS) obj;
	}

	@Override
	public void run() {
		sendBlock();
	}

	private void sendBlock() {
		if (mLatestBlockNo == NETCOMMANDS.GETLATESTBLOCKNO) {
			GO.sender.sendLatestBlockNo(mHost, mPort, GO.BLOCKCHAIN.size());
		}
	}

}
