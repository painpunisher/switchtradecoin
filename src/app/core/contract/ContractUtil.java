package app.core.contract;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import app.core.MainMethods;
import app.log.OUT;
import app.model.block.Block;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.old.Wallet;
import app.start.util.GO;
import app.util.StringUtil;

public class ContractUtil {

	static LinkedList<Transaction> txHistory = null;
	private static SmartContract sc = null;

	static boolean setSmartContract(SmartContract psc) {
		sc = psc;
		return true;
	}

	static boolean loadTXHistory() {
		if (txHistory == null) {
			txHistory = getTransactionHistory(sc.publicKey);
		}
		return true;
	}

	static boolean checkDesignTransactionArrived() {
		BigInteger balance = getBalance(sc.publicKey);
		BigInteger designerTransferred = transferredFromPublicKeyToContract(StringUtil.getKey(sc.designer));

		// float acceptorTransferred =
		// transferredAmount(StringUtil.getKey(sc.acceptor));

		BigInteger realPrice = sc.price.multiply(new BigInteger("3"));

		OUT.DEBUG("Designer Transferred: " + designerTransferred);
		OUT.DEBUG("Smart Contract Balance: " + balance);
		OUT.DEBUG("Real Price: " + realPrice);

//		if (designerTransferred >= balance && realPrice <= designerTransferred) {
		
		if (designerTransferred.compareTo(balance) >= 0 && realPrice.compareTo(designerTransferred) <= 0) {
			return true;
		}

		return false;

	}

	/**
	 * Only use for acceptor or designer.
	 * 
	 * @param publickey
	 * @return
	 */
	static int getTransactionCountSender(PublicKey publickey) {
		LinkedList<Transaction> history = new LinkedList<>();
		for (Transaction tx : txHistory) {
			if (tx.sender.equals(publickey)) {
				history.add(tx);
			}
		}
		return history.size();
	}

	static boolean acceptorTransactionArrived() {
		BigInteger acceptorTransferred = transferredFromPublicKeyToContract(StringUtil.getKey(sc.acceptor));
		if (sc.price.compareTo(acceptorTransferred) >= 0) {
			return true;
		}
		return false;
	}

	static boolean recognizeAcceptorTransaction() {
		if (sc.acceptor.length() > 0) {
			return true;
		}
		LinkedList<Transaction> txHistory = getTransactionHistory(sc.publicKey);
		LinkedList<PublicKey> listOfDifferentAcceptors = new LinkedList<>();
		HashMap<PublicKey, Long> diffAcceptors = new HashMap<>();
		for (Transaction tx : txHistory) {
			if (!tx.sender.equals(StringUtil.getKey(sc.designer))) {
				if (!listOfDifferentAcceptors.contains(tx.sender)) {
					listOfDifferentAcceptors.add(tx.sender);
					diffAcceptors.put(tx.sender, tx.date);
				}
			}
		}

		if (listOfDifferentAcceptors.size() > 1) { // more than 1 acceptor
													// transactions recognized.
													// Check Date.
			while (diffAcceptors.size() > 1) {

				Iterator<Map.Entry<PublicKey, Long>> it = diffAcceptors.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<PublicKey, Long> pair1 = null;
					if(it.hasNext()){
						pair1 = (Map.Entry<PublicKey, Long>) it.next();
					} else {
						it = diffAcceptors.entrySet().iterator();
						if(it.hasNext()){
							pair1 = (Map.Entry<PublicKey, Long>) it.next();
						}
					}
					
					Map.Entry<PublicKey, Long> pair2 = null;
					if(it.hasNext()){
						pair2 = (Map.Entry<PublicKey, Long>) it.next();
					} else {
						it = diffAcceptors.entrySet().iterator();
						if(it.hasNext()){
							pair2 = (Map.Entry<PublicKey, Long>) it.next();
						}
					}

					if ((new Date((long)pair1.getValue())).before((new Date((long)pair2.getValue())))) {
						it.remove(); // avoids a ConcurrentModificationException
					}
				}
			}
		}

		if (diffAcceptors.size() == 1) {
			Iterator<Map.Entry<PublicKey, Long>> it = diffAcceptors.entrySet().iterator();
			Map.Entry<PublicKey, Long> pair = null;
			if (it.hasNext()) {
				pair = (Map.Entry<PublicKey, Long>) it.next();
			}
			sc.acceptor = StringUtil.getStringFromKey((PublicKey)pair.getKey());
			return true;
		}

		return false;
	}

	public static boolean isContractExpired() {
		if (new Date(sc.expireTimestamp).after(new Date())) {
			return false;
		}
		return true;
	}

	private static BigInteger transferredFromPublicKeyToContract(PublicKey publickey) {
		BigInteger total = new BigInteger("0");
		for (Transaction tx : txHistory) {
			if (tx.sender.equals(publickey)) {
				total = total.add(tx.value);
//				sum = sum.add(BigInteger.valueOf(i));
//				total += tx.value;
			}
		}
		return total;
	}

	private static BigInteger transferredFromContractToPublicKey(PublicKey publickey) {
		BigInteger total = new BigInteger("0");
		for (Transaction tx : txHistory) {
			if (tx.reciepient.equals(publickey)) {
				total.add(tx.value);
//				total += tx.value;
			}
		}
		return total;
	}

	public static BigInteger getBalance(PublicKey publickey) {
//		float total = 0;
//		for (Map.Entry<String, TransactionOutput> item : GO.UTXOs.entrySet()) {
//			TransactionOutput UTXO = item.getValue();
//			if (UTXO.isMine(publickey)) { // if output belongs to me ( if coins
//											// belong to me )
//				// UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent
//				// transactions.
//				total += UTXO.value;
//			}
//		}
//		return total;
		return (MainMethods.getBalance(publickey));
	}

	/**
	 * Check if arrived transactions are only from designer and acceptor it
	 * shouldnt be from another.
	 * 
	 * @return
	 */
	private static LinkedList<Transaction> getTransactionHistory(PublicKey scpublickey) {
		LinkedList<Transaction> history = new LinkedList<>();
		for (Block curr : GO.BLOCKCHAIN) {
			if (curr.transactions.size() > 0) {
				for (Transaction tx : curr.transactions) {
					if (tx.reciepient.equals(scpublickey)) {
						history.add(tx);
					}
					if (tx.sender.equals(scpublickey)) {
						history.add(tx);
					}
				}
			}
		}
		return history;
	}

	static boolean sendBackFunds(PublicKey pubkey, BigInteger optionalAmount) {
		BigInteger receivedAmount = transferredFromContractToPublicKey(pubkey);
		BigInteger contractBalance = getBalance(sc.publicKey);
		BigInteger transferredAmount = transferredFromPublicKeyToContract(pubkey);
		
//		if(transferredAmount<=optionalAmount){
//			
//		}
		
		if (optionalAmount.compareTo(new BigInteger("0")) > 0) {
			if (receivedAmount == optionalAmount) {
				return true;
			} else {
				if (contractBalance.compareTo((optionalAmount.subtract(receivedAmount))) >= 0) {
//					if (contractBalance >= (optionalAmount.subtract(receivedAmount))) {
					sendFund(pubkey, optionalAmount.subtract(receivedAmount));
					return true;
				}
			}
		}
		if ((transferredAmount.subtract(receivedAmount)).compareTo(new BigInteger("0")) == 0) {
			return true;
		}
		if (contractBalance.compareTo((transferredAmount.subtract(receivedAmount))) >= 0) {
//			if (contractBalance >= (transferredAmount.subtract(receivedAmount))) {
			sendFund(pubkey, transferredAmount.subtract(receivedAmount));
		}
		return false;
	}

	private static boolean sendFund(PublicKey pubkey, BigInteger amount) {
		Wallet contract = new Wallet(sc.privateKey, sc.publicKey);
		Transaction txsendback = null;
		txsendback = contract.sendFunds(pubkey, (amount));
		if (txsendback != null) {
			GO.mempool.add(txsendback);
			OUT.DEBUG("SIZE OF MEMPOOL>>>: " + GO.mempool.size());
			OUT.DEBUG("AFTER ADDING TO MEMPOOL: " + txsendback.transactionId);
			if (GO.sender != null) {
				GO.sender.sendTransaction("", 0, txsendback);
			}
		}
		return true;
	}

}
