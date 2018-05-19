package app.core.contract;

import java.math.BigInteger;
import java.util.LinkedList;

import app.database.DBContract;
import app.log.OUT;
import app.model.block.Block;
import app.model.contract.ContractStatus;
import app.model.contract.SmartContract;
import app.start.util.GO;
import app.util.StringUtil;

public class ContractStatusHandler {
	
	public static void ProofContracts(){
		LinkedList<SmartContract> history = new LinkedList<SmartContract>();
		for(Block curr : GO.BLOCKCHAIN){
			if(curr.contracts.size()>0){
				for(SmartContract sc : curr.contracts){
					history.add(sc);
				}
			}
		}
		
		for(SmartContract cur : history){
			process(cur);
		}
		
	}
	
	private static void process(SmartContract sc){
		ContractUtil.txHistory = null;
		ContractUtil.setSmartContract(null);
		ContractUtil.setSmartContract(sc);
		ContractUtil.loadTXHistory();
		sc.statusProofed++;
		OUT.DEBUG(sc.status.toString() + " - Proofed_Times: " + sc.statusProofed);
		
		switch (sc.status) {
		case DESIGNING:
			//TODO check if tx for this contract arrived from designer. check funds amount and contract price  (x2 or x3 here)
			//TODO if true than switch to WAITINGACCEPTOR
			
			if(ContractUtil.isContractExpired()){
				sc.status = ContractStatus.EXPIRED;
			}
			
			if(ContractUtil.checkDesignTransactionArrived()){
				sc.status = ContractStatus.WAITINGACCEPTOR;
				DBContract.insertContract(sc);
			}
			break;
		case WAITINGACCEPTOR:
			
			//TODO check for date expired too, if true switch too EXPIRED
			if(ContractUtil.isContractExpired()){
				sc.status = ContractStatus.EXPIRED;
			}
			//TODO waiting in this status till new tx arrived from another. 
			if(ContractUtil.recognizeAcceptorTransaction()){
				if(ContractUtil.acceptorTransactionArrived()){
					if(ContractUtil.getTransactionCountSender(StringUtil.getKey(sc.acceptor)) == 1){
						sc.status = ContractStatus.WAITINGACCEPTORCONFIRM;
						DBContract.insertContract(sc);
					}
				}
			}
			//TODO check balance of contract and compare if its more then designing price. If true then switch to WAITINGACCEPTORCONFIRM.
//			OUT.DEBUG(sc.status.toString());
			break;
		case WAITINGACCEPTORCONFIRM:
			//TODO waiting here till new tx arrived from acceptor 
			//TODO check if more tx arrived from acceptor then once, and check if arrived balance more then before multiply price and so on
			//TODO if true switch to WAITINGDESIGNERCONFIRM
			
			if(ContractUtil.getTransactionCountSender(StringUtil.getKey(sc.acceptor)) >= 2){
				sc.status = ContractStatus.WAITINGDESIGNERCONFIRM;
				DBContract.insertContract(sc);
			}
			
			
			break;
		case WAITINGDESIGNERCONFIRM:
			//TODO waiting here for designer confirmation if sum of funds arrived from designer more then first and more tx and so
			//TODO if true switch to CLOSED
			if(ContractUtil.getTransactionCountSender(StringUtil.getKey(sc.designer)) >= 2){
				sc.status = ContractStatus.CLOSED;
				DBContract.insertContract(sc);
			}
			break;
		case EXPIRED:
			//TODO contract expired no more actions here to switch.
			//TODO if contract has balance send back to designer.
			//TODO if balance 0 switch maybe to KILLSELF, sendback funds till balance is 0.
			if(ContractUtil.sendBackFunds(StringUtil.getKey(sc.acceptor),new BigInteger("0"))){
				sc.status = ContractStatus.KILLSELF;
				DBContract.insertContract(sc);
			}
			break;
		case CLOSED:
			//TODO sending funds back, which is used for confirmation. and sending 1x price from designer to acceptor
			
			if(sc.statusProofed >= 25){
				//TODO sendback funds to acceptor
				sc.status = ContractStatus.SENDBACKFUNDSACCEPTOR;
				DBContract.insertContract(sc);
			}
			break;
		case SENDBACKFUNDSACCEPTOR:
			
			if(ContractUtil.sendBackFunds(StringUtil.getKey(sc.acceptor), sc.price.multiply(new BigInteger("3")))){
				sc.status = ContractStatus.SENDBACKFUNDSDESIGNER;
				DBContract.insertContract(sc);
			}
			break;
		case SENDBACKFUNDSDESIGNER:
			if(ContractUtil.sendBackFunds(StringUtil.getKey(sc.designer), sc.price.multiply(new BigInteger("3")))){
				sc.status = ContractStatus.KILLSELF;
				DBContract.insertContract(sc);
			}
			break;
		case EXTERNALTRANSACTIONDETECTED:
			//TODO this status have to be checked everytime if external tx is 
			//TODO hier muss geprüft werden in welchem status das contract sich befindet, wenn schon acceptor im minus ist kann es nicht sein
			//TODO dass der acceptor schon work gemacht hat aber dann external transaction versaut alles. das geht nicht.
			//TODO soll nur an bestimmten punkten vorkommen können.
			break;
		case LIQUIDATECONTRACT:
			//TODO contract will be liquidated some where, have to specified.
			break;
		case KILLSELF:
			//TODO contract kill self have to specified.
			break;
			
		default:
			break;
		}
		
		
	}
	
}
