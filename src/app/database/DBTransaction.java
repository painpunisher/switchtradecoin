package app.database;

import app.log.OUT;
import app.model.transaction.Transaction;

public class DBTransaction {

	private static DBUtil db = DBUtil.getInstance(DBEnum.TRANSACTION);
	private static boolean insertTransaction(Transaction state) {
//		db.connect(DBEnum.TRANSACTION);
		boolean success = db.insert(state.transactionId.getBytes(), state.writeToDB());
		OUT.DEBUG("Transaction added to DB: " + state.transactionId);
//		db.select(state.transactionId.getBytes());
		return success;
	}

	/**
	 * Getting tx from db, if null txid not found.
	 * 
	 * @param txid
	 * @return
	 */
	public static Transaction getTransaction(String txid) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.TRANSACTION);
		Transaction state = new Transaction(db.select(txid.getBytes()));
		// state.readFromDB(db.select(txid.getBytes()));
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}

	public static Transaction getTransaction(byte[] data) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.TRANSACTION);
		Transaction state = new Transaction(db.select(data));
		// state.readFromDB(db.select(txid.getBytes()));
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}
	
	static boolean processInsertTransaction(Transaction tx){
		boolean success = false;
		success = insertTransaction(tx); 
		return success;
	}
	
	// public static boolean processTransactions(ArrayList<Transaction> tx){
	// boolean success = false;
	// for(Transaction cur : tx){
	// AccountState sender = getAccountState(cur.sender);
	// AccountState receiver = getAccountState(cur.reciepient);
	// int funds = (int)cur.value;
	//
	// if(sender.balance.intValue() >= funds || cur.transactionId.equals("0") ||
	// tx.get(0)==cur){//
	// //reduce sender
	// BigInteger bi = BigInteger.valueOf(sender.balance.intValue() - funds);
	// sender.balance = bi;
	// sender.nonce = BigInteger.valueOf(sender.nonce.intValue() + 1);
	// insertAccount(cur.sender, sender);
	// //add funds to receiver
	// BigInteger bx = BigInteger.valueOf(receiver.balance.intValue() + funds);
	// receiver.balance = bx;
	// receiver.nonce = BigInteger.valueOf(receiver.nonce.intValue() + 1);
	// insertAccount(cur.reciepient, receiver);
	//
	// success = true;
	// } else {
	// return false;
	// }
	// }
	// return success;
	// }

	public static void iterate() {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.TRANSACTION);
		db.iteration();
	}

}
