package app.thread.receive;

public class THREADReceiveBlockchain implements Runnable{
	
//	private Object blockchain;
	
	public THREADReceiveBlockchain(Object obj){
//		blockchain = obj;
	}
	
	@Override
	public void run() {
//		@SuppressWarnings("unchecked")
//		ArrayList<Block> chain = (ArrayList<Block>)blockchain;
//		if (chain!=null){OUT.DEBUG("SIZE OF RECEIVED BLOCKCHAIN : "+chain.size());}
//		chainReceived(chain);
	}

//	private void chainReceived(ArrayList<Block> chain){
//		boolean isValid = CheckValidityFunctions.isChainValid(chain);
//		if(isValid){
//			GO.BLOCKCHAIN = chain;
//			GO.previousBlock = GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size()-1);
//			SaveToFileService.saveBlockchain(GO.BLOCKCHAIN);
//			OUT.DEBUG("Blockchain wurde geliefert und ersetzt!");
////			GO.startBlockTimeMiner();
//		}
//	}
	
}
