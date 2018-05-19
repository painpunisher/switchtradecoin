package app.database;

import app.log.OUT;
import app.model.contract.SmartContract;

public class DBContract {

	private static DBUtil db = DBUtil.getInstance(DBEnum.CONTRACT);
	public static boolean insertContract(SmartContract state) {
//		db.connect(DBEnum.TRANSACTION);
		boolean success = db.insert(state.id.getBytes(), state.writeToDB());
		OUT.DEBUG("SmartContract added to DB: " + state.id);
//		db.select(state.transactionId.getBytes());
		return success;
	}

	/**
	 * Getting tx from db, if null txid not found.
	 * 
	 * @param scid
	 * @return
	 */
	public static SmartContract getContract(String scid) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.TRANSACTION);
		SmartContract state = new SmartContract(db.select(scid.getBytes()));
		// state.readFromDB(db.select(txid.getBytes()));
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}

	public static SmartContract getContract(byte[] data) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.TRANSACTION);
		SmartContract state = new SmartContract(db.select(data));
		// state.readFromDB(db.select(txid.getBytes()));
		if (state == null || state.equals("")) {
			state = null;
		}
		return state;
	}
	
	static boolean processInsertContract(SmartContract tx){
		boolean success = false;
		success = insertContract(tx); 
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
