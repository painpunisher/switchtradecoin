package app.util;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import app.database.DBBlock;
import app.log.OUT;
import app.start.util.GO;
import app.thread.BuildBlockchainHandler;

public class RepairBlockchain extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 228862938329072096L;

	public RepairBlockchain(){
        setTitle("REPAIRING BLOCKCHAIN");
        setSize(200,200);
        setModal(true);
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				OUT.DEBUG("BEGIN REPAIR INVOKELATER");
				beginRepair();
			}
		});
        
	}
	
	private boolean isValidChain() {
		OUT.DEBUG("CHECKING IF CHAIN IS VALID");
		return CheckValidityFunctions.isChainValidOriginal(GO.BLOCKCHAIN);
	}

	private boolean beginRepair() {
		OUT.DEBUG("INNER METHOD OF BEGINREPAIR");
		if (isValidChain()) {
			OUT.DEBUG("IS VALID CHAIN");
			return true;
		} else {
			 boolean repairSuccessfull = repairRoutine();
			 if(repairSuccessfull){
				 OUT.DEBUG("REPAIR WAS A  SUCCESS");
				 synchronizeWithNetworkAfterRepair();
				 OUT.DEBUG("DISPOSE DIALOG!");
				 RepairBlockchain.this.dispose();
			 }
			 return repairSuccessfull;
		}
	}

	private void synchronizeWithNetworkAfterRepair(){
		OUT.DEBUG("AFTER REPAIR SYNCHRONIZE WITH NETWORK");
		BuildBlockchainHandler.buildBlockchainWithBlocks();
	}
	
	private boolean repairRoutine() {
		OUT.DEBUG("LOOP FOR REPAIR CHAIN");
		while (isValidChain() == false) {
			OUT.DEBUG("REMOVING LAST BLOCK");
			removeLastBlock();
		}
		return true;
	}

	public static boolean removeLastBlock() {
		OUT.DEBUG("INNER METHOD REMOVING LAST BLOCK");
		int beforeRemove = GO.BLOCKCHAIN.size();
		GO.BLOCKCHAIN.remove(GO.BLOCKCHAIN.size()-1);
		DBBlock.remove(GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size()-1));
		int afterRemove = GO.BLOCKCHAIN.size();
		OUT.DEBUG("SIZE BEFORE REMOVE: " + beforeRemove + " - SIZE AFTER REMOVE: " + afterRemove);
		if (beforeRemove == afterRemove) {
			OUT.DEBUG("CANNOT REMOVE BLOCK");
			return false;
		} else {
			OUT.DEBUG("DAMAGED BLOCK SUCCESSFULLY REMOVED");
			return true;
		}
	}

}
