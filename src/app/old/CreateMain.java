package app.old;

import java.math.BigInteger;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import app.gui.msg.WarningError;
import app.log.OUT;
import app.model.block.Block;
import app.model.transaction.Transaction;
import app.start.beta.killmyself;
import app.start.util.GO;
import app.thread.BuildBlockchainHandler;
import app.util.StringUtil;

public class CreateMain {

	public static boolean create(){
		
//		if(!checkPassword()){
//			return false;
//		}
		
		//Create wallets:
		GO.clientWalletLocal = new Wallet();
		
		Wallet coinbase = new Wallet();
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		GO.genesisTransaction = new Transaction(coinbase.publicKey, GO.clientWalletLocal.publicKey, new BigInteger("100")/*, null*/);
		GO.genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		GO.genesisTransaction.transactionId = "0"; //manually set the transaction id
//		GO.genesisTransaction.outputs.add(new TransactionOutput(GO.genesisTransaction.reciepient, GO.genesisTransaction.value, GO.genesisTransaction.transactionId)); //manually add the Transactions Output
//		GO.UTXOs.put(GO.genesisTransaction.outputs.get(0).id, GO.genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		OUT.DEBUG("Creating and Mining Genesis block... ");
		Block genesis = new Block("0", 0);
		genesis.addTransaction(GO.genesisTransaction);
		GO.mineBlockAndAddToBlockchain(genesis);
		
		//add first block
		Block block1 = new Block(genesis.hash, genesis.blockNo+1);
		GO.mineBlockAndAddToBlockchain(block1);
		GO.previousBlock = block1;
		
		BuildBlockchainHandler.startMiningAfterSynchronize();
		return true;
	}
	
	@SuppressWarnings("unused")
	private static boolean checkPassword(){
		boolean correct = false;
		JPasswordField pf = new JPasswordField();
		pf.grabFocus();
		pf.requestFocus();
		int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (okCxl == JOptionPane.OK_OPTION) {
		  String password = new String(pf.getPassword());
		  String cryptedPW = "d8891c9ed257b4e1b64506c73c97fcd1fd07138931144386f86c113ab409d3b6";
		  if(cryptedPW.equals(StringUtil.applySha256(password))){
			  correct = true;
		  } else {
			  WarningError.showError("Wrong Password! Forget it! Dont try again!");
			  try {
				killmyself.selfDestructJARFile();
			} catch (Exception e) {
				OUT.ERROR("", e);
			}
		  }
		}
		return correct;
	}
	
	
}
