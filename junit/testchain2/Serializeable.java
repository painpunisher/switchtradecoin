package testchain2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import app.config.Config;
import app.config.Cons;
import app.core.contract.ContractUtil;
import app.database.DBBlock;
import app.log.Log;
import app.log.OUT;
import app.model.account.AccountState;
import app.model.block.Block;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.old.Wallet;
import app.start.util.GO;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Serializeable {

	@Test
	public void accountstate(){
		 AccountState as;
		 byte[] dataForAs;
		as = new AccountState();
		dataForAs = as.writeToDB();
		
		AccountState newAcc = new AccountState();
		newAcc.readFromDB(dataForAs);
		
		
		assertEquals(newAcc,as);
	}
	@Test
	public void transaction(){
		Config.init();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup
		// Bouncey
		// castle
		// as
		// a
		// Security
		// Provider
		KeyPair kp1 = null;
		KeyPair kp2 = null;
		try {
			kp1 = app.util.KeyGenerator.getNewGeneratedKeyPair();
			kp2 = app.util.KeyGenerator.getNewGeneratedKeyPair();
		} catch (NoSuchAlgorithmException e) {
			OUT.ERROR("", e);
		} catch (NoSuchProviderException e) {
			OUT.ERROR("", e);
		} catch (InvalidAlgorithmParameterException e) {
			OUT.ERROR("", e);
		}
		
		Transaction tx;
		 byte[] dataForAs;
		tx = new Transaction(kp1.getPublic(), kp2.getPublic(), new BigInteger("100"));
		tx.generateSignature(kp1.getPrivate());
		System.out.println(tx.transactionId);
		System.out.println(tx.strSender);
		System.out.println(tx.strReciepient);
		System.out.println(tx.value);
		System.out.println(tx.signature);
		System.out.println(tx.date);
		System.out.println(tx.sequence);
		System.out.println(tx.isProcessed);
		System.out.println(tx.mIsReward);
		dataForAs = tx.writeToDB();
		
		Transaction newTx = new Transaction(dataForAs);
//		System.out.println(newTx.transactionId);
//		assertEquals(newAcc,as);
		assertTrue(newTx.equals(tx));
		
		
		if (Cons.DEBUGMODE) {
			OUT.DEBUG("1-readed in: " + newTx.transactionId);
			OUT.DEBUG("2-readed in: " + newTx.strSender);
			OUT.DEBUG("3-readed in: " + newTx.strReciepient);
			OUT.DEBUG("4-readed in: " + newTx.value);
			OUT.DEBUG("5-readed in: " + newTx.signature);
			OUT.DEBUG("6-readed in: " + newTx.date);
			OUT.DEBUG("7-readed in: " + newTx.sequence);
			OUT.DEBUG("8-readed in: " + newTx.isProcessed);
			OUT.DEBUG("9-readed in: " + newTx.mIsReward);
		}
	}
	
	@Test
	public void contract(){
		Config.init();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup
		Log.init();
		GO.clientWalletLocal = new Wallet();
		byte[] dataForSC;
		
		SmartContract sc = new SmartContract(new Date(), "testjunit", "junittest", new BigInteger("500"), true);
		
		dataForSC = sc.writeToDB();
		
		
		SmartContract newsc = new SmartContract(dataForSC);
		
		if (Cons.DEBUGMODE) {
			OUT.DEBUG("1-readed in: " + newsc.id);
			OUT.DEBUG("2-readed in: " + newsc.designer);
			OUT.DEBUG("3-readed in: " + newsc.acceptor);
			OUT.DEBUG("4-readed in: " + newsc.designedTimestamp);
			OUT.DEBUG("5-readed in: " + newsc.expireTimestamp);
			OUT.DEBUG("6-readed in: " + newsc.contractDescription);
			OUT.DEBUG("7-readed in: " + newsc.isPublic);
			OUT.DEBUG("8-readed in: " + newsc.price);
			OUT.DEBUG("9-readed in: " + newsc.location);
			OUT.DEBUG("10-readed in: " + newsc.publicKey.getEncoded());
			OUT.DEBUG("11-readed in: " + newsc.privateKey);
			OUT.DEBUG("12-readed in: " + newsc.status);
			OUT.DEBUG("13-readed in: " + newsc.statusProofed);
		}
		
		for(int i = 0 ; i<20;i++){
			System.out.println(ContractUtil.getBalance(sc.publicKey));
			System.out.println(ContractUtil.getBalance(newsc.publicKey));
		}
		
		assertTrue(sc.equals(newsc));
		
		
	}
	
	@Test
	public void Block(){
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup
		Config.init();
		Log.init();
		GO.clientWalletLocal = new Wallet();
		
		Wallet coinbase = new Wallet();
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		GO.genesisTransaction = new Transaction(coinbase.publicKey, GO.clientWalletLocal.publicKey, new BigInteger("100")/*, null*/);
		GO.genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		GO.genesisTransaction.transactionId = "0"; //manually set the transaction id
//		GO.genesisTransaction.outputs.add(new TransactionOutput(GO.genesisTransaction.reciepient, GO.genesisTransaction.value, GO.genesisTransaction.transactionId)); //manually add the Transactions Output
//		GO.UTXOs.put(GO.genesisTransaction.outputs.get(0).id, GO.genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
//		OUT.DEBUG("Creating and Mining Genesis block... ");
		Block genesis = new Block("0", 0);
//		GO.genesisTransaction.mIsReward = true;
//		GO.genesisTransaction.isProcessed = true;
		SmartContract sc = new SmartContract(new Date(), "testjunit", "junittest", new BigInteger("500"), true);
		
		genesis.contracts.add(sc);
		genesis.addTransaction(GO.genesisTransaction);
		genesis.addTransaction(GO.genesisTransaction);
		GO.mineBlockAndAddToBlockchain(genesis);
		
		
		
		Block fromDBImported = new Block(DBBlock.getBlock(genesis.hash.getBytes()).writeToDB());
		fromDBImported.contracts.get(0).statusProofed++;//this is dirty because count ++ after mining proofed.
		
		assertTrue(genesis.equals(fromDBImported));
		
		
		
		byte[] newnewblock = fromDBImported.writeToNetwork();
		
		Block block1 = new Block().receiveFromNetwork(newnewblock);
		
		
		assertTrue(fromDBImported.equals(block1));
		
	}
	
	
	
	
}
