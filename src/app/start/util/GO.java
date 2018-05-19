package app.start.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import app.config.Cons;
import app.database.DBAccount;
import app.database.DBBlock;
import app.log.OUT;
import app.model.block.Block;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.net.Node;
import app.net.NodeRegister;
import app.net.RECEIVER;
import app.net.SENDER;
import app.old.CreateMain;
import app.old.Wallet;
import app.thread.BuildBlockchainHandler;
import app.thread.ReceiveExecuter;
import app.thread.SendExecuter;
import app.util.CheckValidityFunctions;
import app.util.SaveToFileService;

public class GO {

	public static ArrayList<Block> BLOCKCHAIN = new ArrayList<Block>();
//	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	public static Wallet clientWalletLocal;
	public static Transaction genesisTransaction;
	public static LinkedList<Transaction> mempool = new LinkedList<Transaction>();
	public static LinkedList<SmartContract> contractsPool = new LinkedList<SmartContract>();
	public static LinkedList<Block> arrivedBlocks = new LinkedList<Block>();
	public static int minimumTransaction = 1;
	public static Block currentBlock;
	public static Block previousBlock;
	public static SENDER sender;
	public static RECEIVER receiver;
	private static Timer blockTime;
	private static Timer waitForPeers;
	public static boolean mBuildingBlockchain = false;
	public static boolean STOPMINING = false;
	public static int countOfGetLatestBlockNoPeers = 0;
	public static boolean appStartedFirstTime = false;
	
	public static boolean initializeApplication() {
		START_UTILS.initBouncyCastle();
		
		if(initSocket()){
			new START();
		}
		
		return true;
	}

	private static boolean initSocket() {
		OUT.println("###INIT SOCKETS###");
		GO.receiver = new RECEIVER();
		ReceiveExecuter.init();
		GO.receiver.setDaemon(true);
		GO.receiver.start();

		GO.sender = new SENDER();
		SendExecuter.init();
		GO.sender.setDaemon(true);
		GO.sender.start();
		
		return true;
	}

	public static void noPeersAvailable(){
		if (START.checkIfBlockchainExist()) {
			BuildBlockchainHandler.startMiningAfterSynchronize();
		} else {
			if(!Cons.CLIENTMODE){
				if(!CreateMain.create()){
					GO.waitForPeersAvailable();	
				}
			} else {
				GO.waitForPeersAvailable();
			}
		}
	}
	
	public static void waitForPeersAvailable(){
		waitForPeers = new Timer();
		waitForPeers.schedule(new TimerTask() {
			@Override
			public void run() {
				OUT.DEBUG("CHECKING IF PEERS AVAILABLE");
				boolean isAlive = false;
				for (Node node : NodeRegister.getInstance().getNodes()) {
					if (node.hostAvailabilityCheck()) {
						OUT.DEBUG("AVAILABLE > Peer: " + node.getIP() + " - Port: " + node.getPort() + " !");
						isAlive = true;
					} else {
						OUT.DEBUG("NOT AVAILABLE > Peer: " + node.getIP() + " - Port: " + node.getPort() + " !");
					}
				}
				if(isAlive){
					peerAlive();
				}
			}
		}, Cons.BLOCKTIME, Cons.BLOCKTIME);
	}
	
	private static void peerAlive(){
		if(waitForPeers!=null){
			waitForPeers.cancel();
			waitForPeers = null;
		}
		new START();
	}
	
	public static void startBlockTimeMiner() {
		if (Cons.MININGMODE) {
			if (blockTime == null) {
				OUT.DEBUG("BLOCKTIMER STARTET AFTER THIS: ");
				blockTime = new Timer();
				blockTime.schedule(new TimerTask() {

					@Override
					public void run() {
						if(!appStartedFirstTime){
							appStartedFirstTime = true;
							START.checkIfBlockchainExist();
						}
						GO.STOPMINING = false;
						if (GO.previousBlock == null) {
							OUT.DEBUG("PREV. BLOCK: IS NULL");
							return;
						}

						checkArrivedBlocks();// checking before making the same
												// block again.

//						START.checkIfBlockchainExist();

						GO.currentBlock = new Block(GO.previousBlock.hash,GO.previousBlock.blockNo+1);

						mineBlockAndAddToBlockchain(GO.currentBlock);
						GO.previousBlock = GO.currentBlock;
					}
				}, Cons.BLOCKTIME, Cons.BLOCKTIME);
			}
		} else {
			OUT.println("##MINING MODE: " + Cons.MININGMODE + " ##");
		}
	}
	
	public static void mineBlockAndAddToBlockchain(Block newBlock) {
		OUT.DEBUG("###BEGIN MINING###");
		newBlock.mineBlock(Cons.DIFFICULTY);
		if (!GO.STOPMINING) {
			if (CheckValidityFunctions.doBlockMeetsHashTarget(newBlock)) {
				OUT.DEBUG(" SIZE: " + GO.BLOCKCHAIN.size() + " ");
				if(GO.masterBlockChainAddBlock(newBlock)){
					if (GO.sender != null) {
						GO.sender.sendBlock("", 0, newBlock);
					}
				}
			}
		}
	}
	
	private static void checkArrivedBlocks(){
		//checking arrived blocks if it fits the previous block and append it.
		//if not check if arrived block exists if its exist delete from list.
		OUT.DEBUG("ARRIVED BLOCKS SIZE BEFORE: "+GO.arrivedBlocks.size() + "");
		OUT.DEBUG("LIST OF ARRIVED BLOCKS");
//		for(Block x : GO.arrivedBlocks){
//			OUT.DEBUG("NO: " +GO.arrivedBlocks.indexOf(x) + " HASH     : "+x.hash);
//			OUT.DEBUG("NO: " +GO.arrivedBlocks.indexOf(x) + " PREV.HASH: "+x.previousHash);
//		}
		
		LinkedList<Block> remove = new LinkedList<Block>();
		
		for(Block x : GO.arrivedBlocks){
			if(x.previousHash.equals(GO.previousBlock.hash)){
				if(addBlockToBlockchainWithoutmining(x)){
					//successfully added
					//remove it.
					//remove x from list.
					remove.add(x);
				}
				
			}
		}
		for(Block x : GO.arrivedBlocks){
			for(Block y : GO.BLOCKCHAIN){
				if(x.hash.equals(y.hash)){
					//arrivedblock exists in blockchain
					//remove it.
					remove.add(x);
				}
			}
		}
		
		for(Block x:remove){
			GO.arrivedBlocks.remove(x);
		}
		
		
		OUT.DEBUG("ARRIVED BLOCKS SIZE AFTER: "+GO.arrivedBlocks.size() + "");
	}
	
	private static void checkMinedTransaction(Block freshMinedBlock) {
		if(!GO.mBuildingBlockchain){
			CheckValidityFunctions.checkTransactionsIfExistsAndRemoveFromMempool(freshMinedBlock);
		}
	}
	
	public static boolean closeApplication() {
		save();
		if (GO.blockTime != null) {
			GO.blockTime.cancel();
		}
		System.out.println("---------------------------------------");
		System.out.println("----------- PROGRAMM CLOSE -----------");
		System.out.println("---------------------------------------");
		System.exit(0);// cierra aplicacion
		return true;
	}

	private static boolean save() {
		SaveToFileService.saveBlockchain(BLOCKCHAIN);
		if (GO.clientWalletLocal != null) {
			SaveToFileService.saveMyWallet(GO.clientWalletLocal.keyPair);
		}
		return true;
	}

	public static boolean addBlockToBlockchainWithoutmining(final Block block) {
		boolean added = false;
		if (CheckValidityFunctions.isBlockValidForPrevBlock(block)) {
			added = GO.masterBlockChainAddBlock(block);
		}
		return added;
	}

	public static boolean masterBlockChainAddBlock(Block block) {
		boolean added = false;
		if (CheckValidityFunctions.isBlockValidForPrevBlock(block)) {
			checkMinedTransaction(block);
			if(applyAccountChangesByTransactions(block)){
				added = GO.BLOCKCHAIN.add(block);
				DBBlock.processBlock(block);
				GO.setCurrentBlockDB(block);
			}
		}else {
			//TODO here something with run blockbuilder again
			BuildBlockchainHandler.buildBlockchainWithBlocks();
		}
		CheckValidityFunctions.isChainValid(GO.BLOCKCHAIN);
		return added;
	}

	private static boolean applyAccountChangesByTransactions(Block block) {
		boolean success = true;
		if(block.transactions.size()>0){
			success = DBAccount.processTransactions(block.transactions);
			if(!success){
				OUT.ERROR("Processing Transactions into Account");
			}
		}
		return success;
	}

	public static Block getCurrentBlockDB(){
		String CURRENTBLOCK = "CURRENTBLOCK";
		Block block = DBBlock.getBlock(CURRENTBLOCK.getBytes());
		return block;
	}
	
	public static boolean setCurrentBlockDB(Block block){
		return DBBlock.insertCurrentBlock(block);
	}
	
}
