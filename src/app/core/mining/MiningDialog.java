package app.core.mining;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;

import app.model.transaction.Transaction;
import app.old.Wallet;
import app.start.gui.GUI;
import app.start.util.GO;

public class MiningDialog {

	public MiningDialog() {
		boolean miningProcessResult = miningProcess();
		System.out.println(miningProcessResult);
		if(MiningUnit.minedSuccessfuly){
			MiningUnit.minedSuccessfuly = false;
		}
	}

	public boolean miningProcess(){
		boolean success = false;
		if (GO.BLOCKCHAIN.size() > 1) {

			Wallet coinbase = new Wallet();
			Transaction reward = new Transaction(coinbase.publicKey, GO.clientWalletLocal.publicKey, new BigInteger("1000")/*,	null*/);
			reward.generateSignature(coinbase.privateKey);
			GO.mempool.addFirst(reward);
		}
		
		//TODO REMOVE THIS TEST A LOT TX PER BLOCK
//		Wallet TESTTX = new Wallet();
//		for(int i = 0; i<250;i++){
//			GO.mempool.add(GO.clientWalletLocal.sendFunds(TESTTX.publicKey, new BigInteger("4")));
//		}

		LinkedList<Transaction> delete = new LinkedList<>();
		for (Transaction tx : GO.mempool) {
			boolean successtx = GO.currentBlock.addTransaction(tx);
			if(!successtx){
				delete.add(tx);
			}
		}
		for (Transaction tx : delete) {
			GO.mempool.remove(tx);
		}
		
		
		for (int i = 0; i<GO.contractsPool.size();i++) {
			GO.currentBlock.addSmartContract(GO.contractsPool.poll());
		}
		System.out.println("BEGIN MINING: " + new Date());
		GUI.progrssbarRefresh(true, "Mining Block");
		MiningUnit.mine.start();
		try {
			MiningUnit.mine.join();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		if(MiningUnit.minedSuccessfuly){
			success = true;
			GUI.progrssbarRefresh(false,"");
		} else {
			success = false;
			GUI.progrssbarRefresh(false,"");			
		}
		System.out.println("END MINING: "+new Date());
		return success;
	}

}
