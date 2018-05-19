package app.thread.receive;

import app.model.block.Block;
import app.start.util.GO;

public class THREADReceiveGetBlockByNo implements Runnable{
	
	private String mHost;
	private int mPort;
	private int mBlockNo;
	private Block mBlockToSend;
	
	public THREADReceiveGetBlockByNo(String host, int port, Object obj){
		mHost = host;
		mPort = port;
		mBlockNo = (int) obj;
	}
	
	@Override
	public void run() {
		if(GO.BLOCKCHAIN.size()==mBlockNo){
			mBlockToSend = GO.BLOCKCHAIN.get(mBlockNo - 1);
		} else {
			mBlockToSend = GO.BLOCKCHAIN.get(mBlockNo);
		}
		sendBlock();
	}
	
	private void sendBlock(){
		GO.sender.sendBlock(mHost, mPort, mBlockToSend);
	}
	
}
