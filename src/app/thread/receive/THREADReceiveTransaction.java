package app.thread.receive;

import app.log.OUT;
import app.model.transaction.Transaction;
import app.start.util.GO;
import app.util.CheckValidityFunctions;

public class THREADReceiveTransaction implements Runnable {

	private Object mTransaction;

	public THREADReceiveTransaction(Object obj) {
		mTransaction = obj;
	}

	@Override
	public void run() {
		Transaction tx = new Transaction((byte[])mTransaction);
		if (tx != null) {
			OUT.DEBUG("NEW TRANSACTION RECEIVED HASH: " + tx.transactionId);
		}
		transaction(tx);
	}

	private void transaction(Transaction tx) {
		if (!CheckValidityFunctions.isExisting(tx)) {
			GO.mempool.add(tx);
			GO.sender.sendTransaction("", 0, tx);
			OUT.DEBUG("Transaction received and added to mempool!");
		} else {
			System.err.println("Transaction exists and discarded!");
		}
	}

}
