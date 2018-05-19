package app.start.util;

import java.util.ArrayList;

import app.config.Cons;
import app.log.OUT;
import app.model.block.Block;
import app.net.NodeDiscovery;
import app.old.Wallet;
import app.thread.BuildBlockchainHandler;
import app.util.CheckValidityFunctions;
import app.util.LoadFromFileService;
import javafx.application.Platform;

public class START {

	public START() {
		init_Blockchain_Wallet();
		//refreshing NODES
		if (Cons.NODEDISCOVERY) {
			NodeDiscovery.startRefreshNodes();
		}
		if(!GO.mBuildingBlockchain){
			buildBlockchainWithBlocks();
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				CheckValidityFunctions.calculateTransactionsAfterSynchronize();
			}
		});
	}
	
	private static void buildBlockchainWithBlocks(){
		BuildBlockchainHandler.buildBlockchainWithBlocks();
	}

	private static boolean init_Blockchain_Wallet() {
		OUT.println("###INIT WALLET###");
		try {
			GO.clientWalletLocal = LoadFromFileService.loadMyWallet();
		} catch (Exception e) {
			if(GO.clientWalletLocal == null){
				GO.clientWalletLocal = new Wallet();
			}
		}
		OUT.println("###INIT BLOCKCHAIN###");
		GO.BLOCKCHAIN = LoadFromFileService.loadBlockchainFromDB();
		if(GO.BLOCKCHAIN==null){
			GO.BLOCKCHAIN = new ArrayList<Block>();
		} else{
			START.checkIfBlockchainExist();
		}

		return true;
	}

	static boolean checkIfBlockchainExist() {
		if (GO.BLOCKCHAIN != null && GO.BLOCKCHAIN.size() > 0) {
			GO.previousBlock = GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1);
			GO.genesisTransaction = GO.BLOCKCHAIN.get(0).transactions.get(0);
			CheckValidityFunctions.isChainValid(GO.BLOCKCHAIN);
			return true;
		} else {
			return false;
		}
	}

}
