package app.thread.receive;

import app.model.block.Block;
import app.start.util.GO;

public class THREADReceiveGetBlockByHash implements Runnable {

	private String mHost;
	private int mPort;
	private String mHashOfBlock;
	private Block mBlockToSend;

	public THREADReceiveGetBlockByHash(String host, int port, Object obj) {
		mHost = host;
		mPort = port;
		mHashOfBlock = (String) obj;
	}

	@Override
	public void run() {
		for (Block cur : GO.BLOCKCHAIN) {
			if (cur.hash.equals(mHashOfBlock)) {
				mBlockToSend = cur;
				break;
			}
		}
		sendBlock();
	}

	private void sendBlock() {
		GO.sender.sendBlock(mHost, mPort, mBlockToSend);
	}

}
