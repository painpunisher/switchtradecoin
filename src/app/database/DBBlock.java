package app.database;

import app.log.OUT;
import app.model.block.Block;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;

public class DBBlock {

	private static DBUtil db = DBUtil.getInstance(DBEnum.BLOCK);
	private static boolean insertBlock(Block block) {
//		db.connect(DBEnum.BLOCK);
		boolean success = db.insert(block.hash.getBytes(), block.writeToDB());
		OUT.DEBUG("Block added to DB: " + block.hash);
//		new Block(db.select(block.hash.getBytes()));
		return success;
	}

	/**
	 * Getting tx from db, if null txid not found.
	 * 
	 * @param blockid
	 * @return
	 */
	public static Block getBlock(byte[] blockid) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.BLOCK);
		Block state = new Block(db.select(blockid));
		// state.readFromDB(db.select(txid.getBytes()));
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}

	public static Block getGenesisBlock(){
		Block state = searchGenesisBlock();
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}
	
	public static boolean processBlock(Block block) {
		boolean success = false;
		
		insertBlock(block);
		
		for(Transaction tx : block.transactions){
			success = DBTransaction.processInsertTransaction(tx);
			if(!success){
				return success;
			}
		}
		
		for(SmartContract sc : block.contracts){
			success = DBContract.processInsertContract(sc);
			if(!success){
				return success;
			}
		}
		
		return success;
	}

//	public static boolean processTransactions(ArrayList<Transaction> tx) {
//		boolean success = false;
//		for (Transaction cur : tx) {
//			AccountState sender = getAccountState(cur.sender);
//			AccountState receiver = getAccountState(cur.reciepient);
//			int funds = (int) cur.value;
//
//			if (sender.balance.intValue() >= funds || cur.transactionId.equals("0") || tx.get(0) == cur) {//
//				// reduce sender
//				BigInteger bi = BigInteger.valueOf(sender.balance.intValue() - funds);
//				sender.balance = bi;
//				sender.nonce = BigInteger.valueOf(sender.nonce.intValue() + 1);
//				insertAccount(cur.sender, sender);
//				// add funds to receiver
//				BigInteger bx = BigInteger.valueOf(receiver.balance.intValue() + funds);
//				receiver.balance = bx;
//				receiver.nonce = BigInteger.valueOf(receiver.nonce.intValue() + 1);
//				insertAccount(cur.reciepient, receiver);
//
//				success = true;
//			} else {
//				return false;
//			}
//		}
//		return success;
//	}

	public static void iterate() {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.BLOCK);
		db.iteration();
		
	}
	
	private static Block searchGenesisBlock(){
		return db.searchGenesisBlock();
	}

	public static boolean remove(Block remove){
		return db.delete(remove.hash);
	}

	
	public static boolean insertCurrentBlock(Block block) {
//		db.connect(DBEnum.BLOCK);
		boolean success = db.insert("CURRENTBLOCK".getBytes(), block.writeToDB());
		OUT.DEBUG("Block added to DB: " + block.hash);
//		new Block(db.select(block.hash.getBytes()));
		return success;
	}
}
