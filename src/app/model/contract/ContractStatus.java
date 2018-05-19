package app.model.contract;

public enum ContractStatus {
	
	DESIGNING, //tx confirmation waited
	WAITINGACCEPTOR, // waiting for an aceptor to accept the contract
	WAITINGACCEPTORCONFIRM, //waiting for acceptor to confirm work done
	EXPIRED, //smartcontract is expired no more actions should be done
	WAITINGDESIGNERCONFIRM, //waiting for the designer to confirm the work of ACCEPTOR
	CLOSED, // smartcontract closed after designer confirmed this state will appear
	SENDBACKFUNDSACCEPTOR, //contract sends back acceptor funds
	SENDBACKFUNDSDESIGNER, // contract sends back designer funds
	KILLSELF, //smartcontract killing itself after expired or failed to add to blockchain
	EXTERNALTRANSACTIONDETECTED, // an external transaction detected 
	LIQUIDATECONTRACT, //contract will be terminated after EXTERNALTRANSACTIONDETECTED. 
	
	
	
	/**
	 * Step by step explaining for this status.
	 * 
	 * 1. DESIGNING  after design contract and broadcast to network - and tx send to this contract from designer wallet.
	 * 1.1	EXPIRED  the contract is expired when no one accepts the contract and the expired date is passed by real world date.
	 * 1.2  KILLSELF contract will kill itself.
	 * 2. WAITINGACCEPTOR   after contract designed, the contract is waiting for acceptor, read contract and accept if it could be done by the acceptor - 
	 * 						accepting contract by sending transaction to this contract.
	 * 3. WAITINGACCEPTORCONFIRM	after accepted the contract the acceptor has to confirm after the work is done.
	 * 
	 * 4. WAITINGDESIGNERCONFIRM  after work done by acceptor, the designer has to proof the work and make the final confirmation for this contract.
	 * 5. CLOSED	after designer confirmed work - the contract will be stated CLOSED and the funds will be sended to each other.
	 * 
	 * EXCEPTIONS:
	 * 
	 *		EXTERNALTRANSACTIONDETECTED - if another wallet is sending funds to this contract then this state will appear. after that change to LIQUIDATECONTRACT
	 *
	 *		LIQUIDATECONTRACT - after EXTERNALTRANSACTIONDETECTED  this status will appear this will send funds back to booth and split the EXTERNAL funds to designer and acceptor.
	 *
	 *
	 */
}
