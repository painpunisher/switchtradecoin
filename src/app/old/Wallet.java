package app.old;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;

import app.core.MainMethods;
import app.log.OUT;
import app.model.block.Block;
import app.model.transaction.Transaction;
import app.start.util.GO;
import app.util.KeyGenerator;

public class Wallet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6753965257558815285L;
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public KeyPair keyPair;
//	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //only UTXOs owned by this wallet.
	
	public Wallet() {
		try {
			setKeyPair(KeyGenerator.getNewGeneratedKeyPair());
		} catch (NoSuchAlgorithmException e) {
			OUT.ERROR("GENERATING KEYPAIR NoSuchAlgorithmException", e);
		} catch (NoSuchProviderException e) {
			OUT.ERROR("GENERATING KEYPAIR NoSuchProviderException ", e);
		} catch (InvalidAlgorithmParameterException e) {
			OUT.ERROR("GENERATING KEYPAIR InvalidAlgorithmParameterException ", e);
		}
	}
	
	public Wallet(PrivateKey privkey, PublicKey pubkey) {
		if(keyPair == null){
			setKeyPair(new KeyPair(pubkey, privkey));
		}
	}
	
	public LinkedList<Transaction> getTransactionHistory(){
		LinkedList<Transaction> history = new LinkedList<>();
		for(Block curr : GO.BLOCKCHAIN){
			if(curr.transactions.size()>0){
				for(Transaction tx : curr.transactions){
					if(tx.sender==null){
						OUT.DEBUG("stahp");
						return null;
					}
					if(tx.sender.equals(publicKey)){
						history.add(tx);
					}
					if(tx.reciepient.equals(publicKey)){
						history.add(tx);
					}
				}
				
			}
		}
		return history;
	}
	
	public void setKeyPair(final KeyPair kp) {
		try {
			keyPair = kp;
			
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private BigInteger total = new BigInteger("0");
	//returns balance and stores the UTXO's owned by this wallet in this.UTXOs
		public synchronized BigInteger  getBalance() {
			try {
				total = new BigInteger("0");
				total = new BigInteger(""+MainMethods.getBalance(publicKey));
			} catch (Exception e) {
			}
			
			return total;
		}
		//Generates and returns a new transaction from this wallet.
		public Transaction sendFunds(PublicKey _recipient,BigInteger value ) {
			if(getBalance().compareTo(new BigInteger("0")) == 0){
				OUT.DEBUG("#No funds to send transaction. Transaction Discarded.");
				return null;
			}
			if(getBalance().compareTo(value) < 0) { //gather balance and check funds.
				OUT.DEBUG("#Not Enough funds to send transaction. Transaction Discarded.");
				return null;
			}
			Transaction newTransaction = new Transaction(publicKey, _recipient , value/*, inputs*/);
			newTransaction.generateSignature(privateKey);
			
			return newTransaction;
		}

}