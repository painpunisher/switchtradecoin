package app.util;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;

import app.config.FilePathCollection;
import app.gui.msg.WarningError;
import app.log.OUT;
import app.model.block.Block;

public class SaveToFileService {

	
	public static void saveMyWallet(final KeyPair kp){
		try {
			boolean success = (new File(FilePathCollection.DATA_PATH)).mkdirs();
			if(!success){
				//already exists
				OUT.DEBUG("PATH ALREADY EXISTS: "+FilePathCollection.DATA_PATH);
			} else {
				//created
				OUT.DEBUG("PATH CREATION: "+FilePathCollection.DATA_PATH);
			}
			KeySaver.SaveKeyPair(FilePathCollection.DATA_PATH, kp, true);
		} catch (IOException e) {
			WarningError.showError("Error loading Wallet!");
			OUT.ERROR("ERROR LOADING WALLET ", e);
		}
	}
	
	public static void saveBlockchain(final ArrayList<Block> blockchain) {
//		try {
//			boolean success = (new File(FilePathCollection.DATA_PATH)).mkdirs();
//			if(!success){
//				//already exists
//				OUT.DEBUG("PATH ALREADY EXISTS: "+FilePathCollection.DATA_PATH);
//			} else {
//				//created
//				OUT.DEBUG("PATH CREATION: "+FilePathCollection.DATA_PATH);
//			}
//			FileOutputStream fout = null;
//			fout = new FileOutputStream(FilePathCollection.BLOCKCHAIN_FILE);
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(blockchain);
//			fout.close();
//		} catch (IOException e) {
//			WarningError.showError("Error Loading the Blockchain!");
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
