package app.util;

import java.util.ArrayList;

import app.config.Cons;
import app.database.DBAccount;
import app.database.DBAccount_Valid;
import app.database.DBEnum;
import app.log.OUT;
import app.model.account.AccountState;
import app.model.block.Block;
import app.model.transaction.Transaction;
import app.start.gui.GUI;
import app.start.util.GO;

public class CheckValidityFunctions {

	public static boolean isBlockValid(Block block) {
		if (!doBlockMeetsHashTarget(block)) {
			System.err.println("Block does not meets the hashtarget, DISCARDED!");
			return false; // doesnt seems like be mined
		}

		if (isBlockExists(block)) {
			System.err.println("Block is already exists in this Blockchain, DISCARDED!");
			return false;
		}

		if (block.hash.equals(block.calculateHash())) {
			return true; // valid
		}
		return false; // not valid
	}

	public static boolean doBlockMeetsHashTarget(Block block) {
		String hashTarget = new String(new char[Cons.DIFFICULTY]).replace('\0', '0');
		return block.hash.substring(0, Cons.DIFFICULTY).equals(hashTarget);
		// return true;
	}

	public static boolean isBlockExists(Block block) {
		for (Block cur : GO.BLOCKCHAIN) {
			if (cur.hash.equals(block.hash)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBlockValidForPrevBlock(Block block) {

		if (GO.BLOCKCHAIN.size() == 0) {
			if (block.previousHash.equals("0")) {
				return true;
			} else {
				return false;
			}
		}

		Block prev = GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size() - 1);
		if (prev.hash.equals(block.previousHash)) {
			return true;
		} else {
			OUT.DEBUG("HASH of Last Block in the Chain: " + prev.hash);
			OUT.DEBUG("PREV.HASH of Block to be added to Chain: " + block.previousHash);
			OUT.ERROR("Block can not be added to blockchain previous Hash not equal!");
			return false;
		}
	}

	public static void checkTransactionsIfExistsAndRemoveFromMempool(Block block) {
		OUT.DEBUG("CHECKING BLOCK IF TRANSACTIONS IN MEMPOOL MINED!");
		for (Transaction txBlock : block.transactions) {
			boolean detected = false;
			Transaction minedTX = null;
			for (Transaction txPool : GO.mempool) {
				if (txBlock.transactionId.equals(txPool.transactionId)) {
					detected = true;
					minedTX = txPool;
					OUT.DEBUG("DETECTED TRANSACTION MINED IN BLOCK: " + minedTX.transactionId);
					break;
				}
			}
			if (detected) {
				if (GO.mempool.remove(minedTX)) {
					OUT.DEBUG("DETECTED TRANSACTION REMOVED: " + minedTX.transactionId);
				}

			}
		}

	}

//	public static boolean finalChainIsValidCheck(final ArrayList<Block> blockchain) {
//		return isChainValid(blockchain);
//	}

	public static Boolean isChainValid(final ArrayList<Block> blockchain) {
		boolean ISVALID = true;

		ISVALID = isChainValidOriginal(blockchain);

		if (!ISVALID) {
			OUT.DEBUG("REPAIRING IS STARTING NOW");
			RepairBlockchain repair = new RepairBlockchain();
			repair.setVisible(true);
		}

		return ISVALID;
	}

	static Boolean isChainValidOriginal(ArrayList<Block> blockchain) {

		OUT.DEBUG("#################DB ACCOUNT DESTROYED NOW: ###############");
		if (!DBAccount_Valid.destroy(DBEnum.ACCOUNT_VALID)) {
			return false;
		} else {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>createAccount_Valid");
			createAccount_Valid();
		}

		boolean ISVALID = true;
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[Cons.DIFFICULTY]).replace('\0', '0');
		// loop through blockchain to check hashes:
		for (int i = 0; i < blockchain.size(); i++) {
			try {
				DBAccount_Valid.processTransactions(blockchain.get(i).transactions);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentBlock = blockchain.get(i);
			try {
				previousBlock = blockchain.get(i - 1);
			} catch (Exception e) {
				previousBlock = null;
			}
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				OUT.ERROR("#Current Hashes not equal");
				ISVALID = false;
			}
			if (previousBlock != null) {

				// compare previous hash and registered previous hash
				if (!previousBlock.hash.equals(currentBlock.previousHash)) {
					OUT.ERROR("#Previous Hashes not equal");
					ISVALID = false;
				}
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, Cons.DIFFICULTY).equals(hashTarget)) {
				OUT.ERROR("#This block hasn't been mined");
				ISVALID = false;
			}

			// loop thru blockchains transactions:
			// TransactionOutput tempOutput;
			for (int t = 1; t < currentBlock.transactions.size(); t++) {
				boolean isReward = t == 0;
				Transaction currentTransaction = currentBlock.transactions.get(t);
				if (isReward) {
					OUT.DEBUG("REAL REWARD DETECTED");
				}

				boolean isFakeReward = t > 0 /*
												 * && currentTransaction.inputs==
												 * null
												 */;
				if (isFakeReward) {
					// OUT.DEBUG("its reward");
					continue;
				} else {
					// OUT.DEBUG("NORMAL TRANSACTION DETECTED");
				}
				if (!currentTransaction.verifiySignature()) {
					OUT.ERROR("#Signature on Transaction(" + t + ") is Invalid");
					ISVALID = false;
				}
			}
		}
		
		GUI.GUIGlobalChanges();
		
		OUT.println("------------------------------------------------------------------------------");
		OUT.println(
				"Blockchain is valid! Size: " + (blockchain.size()) + " - BUILDER ACTIVE?- " + GO.mBuildingBlockchain);
		OUT.println("------------------------------------------------------------------------------");

		return ISVALID;
	}

	public static boolean isExisting(Transaction tx) {
		// prüfen ob schon vorhanden.
		for (Transaction curTX : GO.mempool) {
			if (curTX.transactionId.equals(tx.transactionId)) {
				return true;
			}
		}

		// double spending prüfen, ob eventuell im mempool schon eine
		// transaction von dem empfänger vorhanden ist.
		for (Transaction curTX : GO.mempool) {
			if (StringUtil.getStringFromKey(curTX.sender).equals(StringUtil.getStringFromKey(tx.sender))) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkIfBlockHasMoreThanOneRewardTX(Block block) {
		int rewardTX = 0;
		boolean isValid = false;
		if (rewardTX > 1) {
			OUT.DEBUG("Block with more reward Transactions than 1 Detected, DISCARDING BLOCK!");
			isValid = true;
		}
		if (rewardTX == 1) {
			isValid = false;
		}
		return isValid;
	}

	public static void calculateTransactions(ArrayList<Block> blockchain) {
		DBAccount.destroy(DBEnum.ACCOUNT);
		// loop through blockchain to calculate transactions:
		for (int i = 0; i < blockchain.size(); i++) {
			try {
				DBAccount.processTransactions(blockchain.get(i).transactions);
			} catch (Exception e) {
			}
		}
	}
	
	public static boolean calculateTransactionsAfterSynchronize(){
		 calculateTransactions(GO.BLOCKCHAIN);
		 return true;
	}
	
	public static boolean createAccount_Valid(){
		for (int i = 0; i < GO.BLOCKCHAIN.size(); i++) {
			for (int t = 0; t < GO.BLOCKCHAIN.get(i).transactions.size(); t++) {
				AccountState acc = new AccountState();
				DBAccount_Valid.insertAccount(GO.BLOCKCHAIN.get(i).transactions.get(t).sender, acc);
				DBAccount_Valid.insertAccount(GO.BLOCKCHAIN.get(i).transactions.get(t).reciepient, acc);
			}
		}
		return true;
	}
	
}
