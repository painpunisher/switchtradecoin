package app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import app.config.Cons;
import app.config.FilePathCollection;
import app.database.DBBlock;
import app.log.OUT;
import app.model.block.Block;
import app.old.Wallet;
import app.start.util.GO;

public class LoadFromFileService {

	public static Wallet loadMyWallet() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		Wallet clientWalletLocal = new Wallet();
		boolean success = (new File(FilePathCollection.DATA_PATH)).mkdirs();
		if (!success) {
			// already exists
			OUT.DEBUG("PATH ALREADY EXISTS: " + FilePathCollection.DATA_PATH);
		} else {
			// created
			OUT.DEBUG("PATH CREATION: " + FilePathCollection.DATA_PATH);
		}
		clientWalletLocal.setKeyPair(KeyLoader.LoadKeyPair(FilePathCollection.DATA_PATH, FilePathCollection.DATA_PATH,
				Cons.ALGORITHM, true));
		return clientWalletLocal;
	}

	public static ArrayList<Block> loadBlockchain() {
		try {
			boolean success = (new File(FilePathCollection.DATA_PATH)).mkdirs();
			File f = null;
			f = new File(FilePathCollection.BLOCKCHAIN_FILE);
			if (!success) {
				// already exists
				OUT.DEBUG("PATH ALREADY EXISTS: " + FilePathCollection.DATA_PATH);
			} else {
				// created
				OUT.DEBUG("PATH CREATION: " + FilePathCollection.DATA_PATH);
			}
			if (f.exists() && !f.isDirectory()) {
				FileInputStream fin;
				fin = new FileInputStream(FilePathCollection.BLOCKCHAIN_FILE);
				ObjectInputStream ois = new ObjectInputStream(fin);
				@SuppressWarnings("unchecked")
				ArrayList<Block> blockchain = (ArrayList<Block>) ois.readObject();
				fin.close();
				if ((blockchain.size() - 1) >= 0) {
					OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN SIZE: " + (blockchain.size()) + "");
					OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN LASTBLOCK PREV. HASH: "
							+ blockchain.get(blockchain.size() - 1).previousHash + "");
					OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN LASTBLOCK HASH: " + blockchain.get(blockchain.size() - 1).hash
							+ "");
				}
				return blockchain;
			}
		} catch (IOException | ClassNotFoundException e) {
			OUT.ERROR("", e);
//			WarningError.showError("Error Loading Blockchain!");
		}
		return null;
	}
	
	public static ArrayList<Block> loadBlockchainFromDB() {
		int difference = 1; //loading show count
		ArrayList<Block> blockchain =null;
		
//		Block first = DBBlock.getBlock(new byte[]{48});
//		Block first = DBBlock.getGenesisBlock();
		Block last = GO.getCurrentBlockDB();
		if(last.hash!=null){
			blockchain = new ArrayList<>();
			blockchain.add(last);
		}
		String getThis = last.previousHash;
		
		
//		if(first.hash!=null){
//			blockchain = new ArrayList<>();
//			blockchain.add(first);
//		}
//		String getThis = first.hash;
		
		
		
		long i = 0;
		try {
			while(DBBlock.getBlock(getThis.getBytes()) != null){
				i++;
				if(i%difference == 0){
					OUT.DEBUG("GETTING NOW BLOCK: " + i + " FROM DB");
				}
				try {
					Block cur = DBBlock.getBlock(getThis.getBytes());
					getThis = cur.previousHash;
					if(cur.hash!=null){
//						blockchain.add(cur);
						blockchain.add(0, cur);
					}
				} catch (Exception e) {
					OUT.DEBUG("FAILED TO GET BLOCK FROM DB: "+i+ " END OF BLOCKCHAIN REACHED");
					break;
				}
				if(i%difference == 0){
					OUT.DEBUG("GOT BLOCK: " + i + " FROM DB AND ADDED");
				}
			}
		} catch (Exception e) {
			OUT.DEBUG("ALL Blocks getted: ");
			
		}
		if (blockchain != null) {

			if ((blockchain.size() - 1) >= 0) {
				OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN SIZE: " + (blockchain.size()) + "");
				OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN LASTBLOCK PREV. HASH: "
						+ blockchain.get(blockchain.size() - 1).previousHash + "");
				OUT.DEBUG("SYSTEM LOAD BLOCKCHAIN LASTBLOCK HASH: " + blockchain.get(blockchain.size() - 1).hash + "");
			}
		}
		return blockchain;
	}
}
