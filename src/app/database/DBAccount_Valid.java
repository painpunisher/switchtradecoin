package app.database;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;

import app.model.account.AccountState;
import app.model.transaction.Transaction;

public class DBAccount_Valid {

	private static DBUtil db = DBUtil.getInstance(DBEnum.ACCOUNT_VALID);
	public static boolean insertAccount(PublicKey key, AccountState state) {
		boolean success = false;
			
		success = db.insert(key.getEncoded(), state.writeToDB());
//			OUT.DEBUG((Arrays.toString(key.getEncoded())));
//		db.select(key.getEncoded());
		
		return success;
	}

	public static AccountState getAccountState(PublicKey key) {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.ACCOUNT);
		AccountState state = new AccountState();
//		System.out.println(key);
		state.readFromDB(db.select(key.getEncoded()));
		if (state == null || state.equals("")) {
			state = new AccountState();
		}
		return state;
	}

	public static boolean processTransactions(ArrayList<Transaction> tx) {
		boolean success = false;
		for (Transaction cur : tx) {
			AccountState sender = getAccountState(cur.sender);
			AccountState receiver = getAccountState(cur.reciepient);
			BigInteger funds = cur.value;

			if (sender.balance.compareTo(funds) >= 0 || cur.transactionId.equals("0") || tx.get(0) == cur) {//
				// reduce sender
				BigInteger bi = (sender.balance.subtract(funds));
				sender.balance = bi;
				sender.nonce = BigInteger.valueOf(sender.nonce.intValue() + 1);
				insertAccount(cur.sender, sender);
				// add funds to receiver
				BigInteger bx = (receiver.balance.add(funds));
				receiver.balance = bx;
				receiver.nonce = BigInteger.valueOf(receiver.nonce.intValue() + 1);
				insertAccount(cur.reciepient, receiver);

				success = true;
			} else {
				return false;
			}
		}
		return success;
	}

	public static void iterate() {
//		DBUtil db = DBUtil.getInstance();
//		db.connect(DBEnum.ACCOUNT);
		db.iteration();
	}
	
	public static boolean destroy(DBEnum type){
		try {
			db.destroyDB(type);
			if(db.connect(type)){
				return true;
			} 
		} catch (Exception e) {
			if(db.connect(type)){
				return true;
			} else {
				e.printStackTrace();
			}
		}
		
//		if(db.destroyDB(type)){
//			if(db.connect(type)){
//				return true;
//			}
//		}
		return false;
	}
}
