package app.thread;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import app.config.Cons;
import app.database.DBBlock;
import app.log.OUT;
import app.model.block.Block;
import app.old.CreateMain;
import app.start.util.GO;
import app.thread.send.THREADSendGetBlockByNo;
import app.util.CheckValidityFunctions;
import app.util.LoadFromFileService;

public class BuildBlockchainHandler {

	public static int LATESTBLOCKNONETWORK = 0;
	public static int LATESTBLOCKNOLOCAL = 0;

	private static int checkLatestFromNetworkTimes = 0;
	
	private static String mHost;
	private static int mPort;
	
	public static void getAllBlocksTillLatest(String host, int port) {
		mHost = host;
		mPort = port;
		OUT.DEBUG("BLOCK NO NETWORK: " + LATESTBLOCKNONETWORK);
		OUT.DEBUG("BLOCK NO LOCAL: " + LATESTBLOCKNOLOCAL);
		int firstCounter = 0;
//		if(StartChain.programm != null){
//			StartChain.programm.progressBar.setMinimum(0);
//			StartChain.programm.progressBar.setMaximum(LATESTBLOCKNONETWORK-LATESTBLOCKNOLOCAL);
//		}
		for (int i = LATESTBLOCKNOLOCAL; i < LATESTBLOCKNONETWORK; i++) {
//			if(StartChain.programm != null){
//				StartChain.programm.progressBar.setString("Sending Get Block Command!");
//				StartChain.programm.progressBar.setValue(firstCounter);
//			}
			firstCounter++;
			// SendExecuter.SENDEXECUTER.submit(new THREADSendGetBlockByNo(host,
			// port, i));
			int between = LATESTBLOCKNONETWORK - LATESTBLOCKNOLOCAL;
			int counter = (between - (LATESTBLOCKNONETWORK - i));
			int delay = counter * 100;
//			OUT.DEBUG("Delay: " + delay + " Sending Get Block: " + i);
			if(firstCounter < 4){
				OUT.DEBUG("Delay: " + 1 + " Sending Get Block: " + i);
				SendExecuter.SENDEXECUTER.schedule((new THREADSendGetBlockByNo(mHost, mPort, i)), 1,
						TimeUnit.MILLISECONDS);	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					OUT.ERROR("", e);
				}
			} else {
				OUT.DEBUG("Delay: " + delay + " Sending Get Block: " + i);
				SendExecuter.SENDEXECUTER.schedule((new THREADSendGetBlockByNo(mHost, mPort, i)), delay,
						TimeUnit.MILLISECONDS);
			}
//			SendExecuter.SENDEXECUTER.schedule((new THREADSendGetBlockByNo(host, port, i)), delay,
//					TimeUnit.MILLISECONDS);
		}
//		if(StartChain.programm != null){
//			StartChain.programm.progressBar.setMinimum(BuildBlockchainHandler.LATESTBLOCKNOLOCAL);
//			StartChain.programm.progressBar.setMaximum(BuildBlockchainHandler.LATESTBLOCKNONETWORK);
//		}
		if (LATESTBLOCKNONETWORK == 0) {
			if (LATESTBLOCKNOLOCAL == 0) {
				if (!Cons.CLIENTMODE) {
					if (!CreateMain.create()) {
						GO.waitForPeersAvailable();
					}
				} else {
					GO.waitForPeersAvailable();
				}
			}
		}
		if (LATESTBLOCKNONETWORK == LATESTBLOCKNOLOCAL) { // last block from
															// network
															// blockchain
															// arrived recognize
															// it here. check it
															// up and catch it
															// here
			SendExecuter.SENDEXECUTER.submit(new THREADSendGetBlockByNo(mHost, mPort, LATESTBLOCKNONETWORK - 1));
		}
	}

	public static void buildBlockchainWithBlocks() {
		GO.arrivedBlocks = new LinkedList<Block>();
		GO.mBuildingBlockchain = true;
		GO.sender.sendGetLatestBlockNoCommand("", 0);
	}

//	private static boolean checkInstantlyIfArrivedBlockCanBeAdded(Block block){
//		
//		if (block.previousHash.equals("0")) {
//			LATESTBLOCKNOLOCAL++;
//			GO.masterBlockChainAddBlock(block);
//			OUT.DEBUG("###GENESIS BLOCK WAS ADDED INSTANTLY YESS");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				OUT.ERROR("", e);
//			}
//			return true;
//		}
//		
//		
//		if (block.previousHash.equals(GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1).hash)) {
//			LATESTBLOCKNOLOCAL++;
//			GO.masterBlockChainAddBlock(block);
//			OUT.DEBUG("###BLOCK WAS ADDED INSTANTLY YESS");
////			try {
////				Thread.sleep(1000);
////			} catch (InterruptedException e) {
////				OUT.ERROR("", e);
////			}
//			return true;
//		} else{
//			return false;
//		}
//	}
	
//	private static void sortBlocksByBlockNo(LinkedList<Block> blocks){
//		
//	}
	
	public static void blockArrived(Block block) {
		//TODO NEW LOGIC FOR BLOCK ADDING
		//add directly to db
		if(block.blockNo+1 == LATESTBLOCKNONETWORK){
			GO.setCurrentBlockDB(block);
		}
		GO.arrivedBlocks.add(block);
		System.out.println(GO.BLOCKCHAIN);
		OUT.DEBUG("RECEIVED: " + GO.arrivedBlocks.size() + " - NETWORK: " + (LATESTBLOCKNONETWORK-LATESTBLOCKNOLOCAL));
		if (GO.arrivedBlocks.size() >= (LATESTBLOCKNONETWORK - LATESTBLOCKNOLOCAL)) {
			int i = 0;
			for(Block cur : GO.arrivedBlocks){
				DBBlock.processBlock(cur);
				i++;
			}
			if(i==GO.arrivedBlocks.size()){
//				new START();
				GO.BLOCKCHAIN = LoadFromFileService.loadBlockchainFromDB();
				blockBuilderStarter();
				CheckValidityFunctions.calculateTransactionsAfterSynchronize();
//				LoadFromFileService.loadBlockchainFromDB();
			}
		}
		
		return;
		
		
		
//		if(StartChain.programm != null){
//			StartChain.programm.progressBar.setString("Waiting to get all Blocks! " + (BuildBlockchainHandler.LATESTBLOCKNONETWORK - BuildBlockchainHandler.LATESTBLOCKNOLOCAL));
//			StartChain.programm.progressBar.setValue(BuildBlockchainHandler.LATESTBLOCKNOLOCAL);
//		}
//		boolean instantlyAdded = checkInstantlyIfArrivedBlockCanBeAdded(block);
//		if(!instantlyAdded){
//			GO.arrivedBlocks.add(block);
//		} else {
//			if(LATESTBLOCKNOLOCAL != LATESTBLOCKNONETWORK){
//				return;
//			}
//		}
//		
//		OUT.DEBUG("### ARRIVED BLOCKS ### >>: " + GO.arrivedBlocks.size());
//		if (GO.arrivedBlocks.size() >= (LATESTBLOCKNONETWORK - LATESTBLOCKNOLOCAL)) {
//			blockBuilderStarter();
//		}
//		if (LATESTBLOCKNONETWORK == LATESTBLOCKNOLOCAL) { // last block from
//															// network
//															// blockchain
//															// arrived recognize
//															// it here. check it
//															// up and catch it
//															// here
//			if (GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1).hash.equals(block.hash)) {
//				OUT.DEBUG("RECOGNIZED THE END OF BLOCKCHAIN - BLOCKCHAIN IS UP-TO-DATE! GO FOR MINING!");
//				blockBuilderStarter();
//			}
//		}
	}

	private static void blockBuilderStarter(){
		OUT.DEBUG("###BLOCKBUILDER STARTET###");
		blockBuilder();
		OUT.DEBUG("###BLOCKBUILDER ENDED###");
	}
	
	private static void blockBuilder() {
		// if (GO.arrivedBlocks.size() >= (LATESTBLOCKNONETWORK -
		// LATESTBLOCKNOLOCAL)) {
//		if(StartChain.programm != null){
//			StartChain.programm.progressBar.setMinimum(BuildBlockchainHandler.LATESTBLOCKNOLOCAL);
//			StartChain.programm.progressBar.setMaximum(BuildBlockchainHandler.LATESTBLOCKNONETWORK);
//		}
//		if(StartChain.programm != null){
//			StartChain.programm.progressBar.setString("Building Blockchain with arrived Blocks!");
////			StartChain.programm.progressBar.setValue(GO.BLOCKCHAIN.size());
//		}
//		OUT.DEBUG("BLOKS ARRIVED COUNT BEFORE: " + GO.arrivedBlocks.size());
//		Iterator<Block> elementListIterator = GO.arrivedBlocks.iterator();
//		while (elementListIterator.hasNext()) {
//			Block x = elementListIterator.next();
//			if (x.previousHash.equals("0")) {
//				GO.masterBlockChainAddBlock(x);
//				elementListIterator.remove();
//				LATESTBLOCKNOLOCAL++;
//				continue;
//			}
//			if (CheckValidityFunctions.isBlockExists(x)) {
//				elementListIterator.remove();
//				LATESTBLOCKNOLOCAL++;
//				continue;
//			}
//		}

//		OUT.DEBUG("BLOKS ARRIVED COUNT AFTER: " + GO.arrivedBlocks.size());

//		while ((GO.BLOCKCHAIN.size() != LATESTBLOCKNONETWORK)) {
//			// while ((GO.BLOCKCHAIN.size() - 1 != LATESTBLOCKNONETWORK)) {
//			OUT.DEBUG("Try do add arrived Blocks into Blockchain end!");
//			modifyArrivedList();
//			OUT.DEBUG("Blockchain Size after modify: " + GO.BLOCKCHAIN.size());
//			OUT.DEBUG("Arrived Blocks Size after modify: " + GO.arrivedBlocks.size());
//			StartChain.programm.progressBar.setValue(GO.BLOCKCHAIN.size());
//		}
//		// OUT.DEBUG(">>>BLOCKCHAIN SUCCESSFULLY SYNCHRONIZED WITH NETWORK<<<");
//		OUT.DEBUG(">>>ALL ARRIVED BLOCKS FROM NETWORK ADDED SUCCESSFULLY TO THE BLOCKCHAIN<<<");
//		SaveToFileService.saveBlockchain(GO.BLOCKCHAIN);
		// }

		if (checkLatestFromNetworkTimes <= 3) {
			OUT.DEBUG("CHECK NETWORK AGAIN TIMES");
			OUT.DEBUG("TIMES: " + checkLatestFromNetworkTimes);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				OUT.ERROR("", e);
			}
			checkLatestFromNetworkTimes++;
			checkNetworkBlockSizeAgain();
		}

		if (GO.BLOCKCHAIN.size() >= LATESTBLOCKNONETWORK) {
			OUT.DEBUG("XXXX>>>STARTING MINING AFTER SYNCHRONIZE!!");
			startMiningAfterSynchronize();
		}
	}

	private static void checkNetworkBlockSizeAgain() {
		buildBlockchainWithBlocks();
	}

	public static void startMiningAfterSynchronize() {
		if (CheckValidityFunctions.isChainValid(GO.BLOCKCHAIN)) {
			GO.mBuildingBlockchain = false;
			OUT.DEBUG("STARTBLOCKTIMER-->>>");
			GO.startBlockTimeMiner();
		}
	}

//	private static void modifyArrivedList() {
//		Iterator<Block> elementListIterator = GO.arrivedBlocks.iterator();
//		int size = GO.arrivedBlocks.size();
//		boolean exist = false;
//		while (elementListIterator.hasNext()) {
//			Block x = elementListIterator.next();
//			OUT.DEBUG("Count of Left Blocks to be checked if its fits to the end: " + size);
//			// OUT.DEBUG("arrived block current element previous hash: " +
//			// x.previousHash);
//			// OUT.DEBUG("Last Blockchain Blocks Hash: " +
//			// GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1).hash);
//			if (x.previousHash.equals(GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1).hash)) {
//				// OUT.DEBUG("GO.masterBlockChainAddBlock: ");
//				GO.masterBlockChainAddBlock(x);
//				elementListIterator.remove();
//				LATESTBLOCKNOLOCAL++;
//				OUT.DEBUG("latestblocknolocal: " + LATESTBLOCKNOLOCAL);
//				// size--;
//				exist = true;
////				return;
//			}
//			size--;
//		}
//		if(!exist){
//			if(RepairBlockchain.removeLastBlock()){
//				OUT.DEBUG("Repair: Send Command to get the Block: " + GO.BLOCKCHAIN.size());
//				SendExecuter.SENDEXECUTER.schedule((new THREADSendGetBlockByNo(mHost, mPort, GO.BLOCKCHAIN.size())), 100,
//						TimeUnit.MILLISECONDS);
//			} else {
//				modifyArrivedList();
//			}
//		}
//		
//		
//	}

}
