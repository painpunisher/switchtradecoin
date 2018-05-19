package app.thread.receive;

import app.log.OUT;
import app.model.LatestBlockHolder;
import app.start.util.GO;
import app.thread.BuildBlockchainHandler;

public class THREADReceiveLatestBlockNo implements Runnable {

	private String mHost;
	private int mPort;
	private int mLatestBlockNo;
	private static int countOfReceivedLatestBlockNo = 0;
	// private LinkedList<LatestBlockHolder> listHolder = new
	// LinkedList<LatestBlockHolder>();
	private static LatestBlockHolder mainHolder = null;
	private static LatestBlockHolder newHolder = new LatestBlockHolder();

	public THREADReceiveLatestBlockNo(String host, int port, Object obj) {
		mHost = host;
		mPort = port;
		mLatestBlockNo = (int) obj;
		// collecting holder.
		LatestBlockHolder holder = new LatestBlockHolder();
		holder.host = mHost;
		holder.port = mPort;
		holder.size = mLatestBlockNo;
		newHolder = holder;

		if (mainHolder == null || newHolder.size > mainHolder.size) {
			mainHolder = newHolder;
			OUT.DEBUG("MAINHOLDER SETTING NEWHOLDER");
		}
		OUT.DEBUG("MAINHOLDER: " + mainHolder.size);
		OUT.DEBUG("NEWHOLDER: " + newHolder.size);

	}

	@Override
	public void run() {
		OUT.DEBUG("SET LATEST BLOCK CALLING BEFORE THIS");
		setLatestBlock();
		OUT.DEBUG("SET LATEST BLOCK CALLING AFTER THIS");
	}

	private void setLatestBlock() {
		OUT.DEBUG("SETLATESTBLOCK INNER METHOD STARTED");

		countOfReceivedLatestBlockNo++;
		OUT.DEBUG("BLOCKNOPEERS: " + GO.countOfGetLatestBlockNoPeers + " - COUNT OF RECEIVED LATESTBLOCKNO: "
				+ countOfReceivedLatestBlockNo);
		if (GO.countOfGetLatestBlockNoPeers == countOfReceivedLatestBlockNo) {
			OUT.DEBUG("SETTING NOW THE LATESTBLOCK VARIABLES AND STARTING ALLBLOCKSTILLLATEST GET METHOD");
			countOfReceivedLatestBlockNo = 0;
			BuildBlockchainHandler.LATESTBLOCKNONETWORK = mainHolder.size;
			BuildBlockchainHandler.LATESTBLOCKNOLOCAL = GO.BLOCKCHAIN.size();
			BuildBlockchainHandler.getAllBlocksTillLatest(mainHolder.host, mainHolder.port);
		}
	}
}
