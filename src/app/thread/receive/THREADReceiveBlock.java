package app.thread.receive;

import app.log.OUT;
import app.model.block.Block;
import app.start.util.GO;
import app.thread.BuildBlockchainHandler;
import app.util.CheckValidityFunctions;
import app.util.SaveToFileService;

public class THREADReceiveBlock implements Runnable {

	private Object mBlock;

	public THREADReceiveBlock(Object obj) {
		mBlock = obj;
	}

	@Override
	public void run() {
		Block block = new Block().receiveFromNetwork((byte[])mBlock);
		if (block != null) {
//			OUT.DEBUG("#NEW BLOCK RECEIVED# HASH: " + block.hash);
//			OUT.DEBUG("#NEW BLOCK RECEIVED# PREV.HASH: " + block.previousHash);
			OUT.DEBUG("#NEW BLOCK RECEIVED# BLOCKNO: " +block.blockNo);
		}
		handleReceivedBlock(block);
	}

	private void handleReceivedBlock(Block block) {

		if (CheckValidityFunctions.checkIfBlockHasMoreThanOneRewardTX(block)) {
			return;
		}

		if (GO.mBuildingBlockchain) {
			if (block.blockNo >= BuildBlockchainHandler.LATESTBLOCKNOLOCAL
					&& block.blockNo < BuildBlockchainHandler.LATESTBLOCKNONETWORK) {
				BuildBlockchainHandler.blockArrived(block);
				return;
			}
			
			if(block.hash.equals(GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size()-1).hash)){
				//received block is the latest block
				BuildBlockchainHandler.startMiningAfterSynchronize();
			}
			
			return;
		}

		if (CheckValidityFunctions.isBlockValid(block)) {
			GO.STOPMINING = true;
			if (GO.addBlockToBlockchainWithoutmining(block)) {
				GO.previousBlock = GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1);
				SaveToFileService.saveBlockchain(GO.BLOCKCHAIN);
				OUT.DEBUG("Block received and added to chain!");
			} else{
				OUT.DEBUG("Block received but not added to chain!");
			}
		} else {
			System.err.println("Block is not valid discarded!");
		}
	}

}
