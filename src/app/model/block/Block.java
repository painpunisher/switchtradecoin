package app.model.block;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

import app.core.block.BLOCKMINING;
import app.core.mining.MiningDialog;
import app.core.mining.MiningUnit;
import app.database.util.ByteUtils;
import app.database.util.DBStore;
import app.log.OUT;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.start.util.GO;
import app.util.StringUtil;

public class Block implements /*Serializable*/ DBStore {

	public String hash;
	public String previousHash;
	private String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timeStamp; // as number of milliseconds since 1/1/1970.
	private int nonce;
	public long blockNo;

	public ArrayList<SmartContract> contracts = new ArrayList<SmartContract>();
	
	/**
	 * Additional to this constructor u have to call the receiveBlockNetwork.
	 */
	public Block(){
		
	}
	/**
	 * This constructor is for local db savings.
	 * @param store byte[].
	 */
	public Block(byte[] store){
		readFromDB(store);
	}
	
	public Block(Block b) {
		this.hash = b.hash;
		this.previousHash = b.previousHash;
		this.merkleRoot = b.merkleRoot;
		this.transactions = b.transactions;
		this.timeStamp = b.timeStamp;
		this.nonce = b.nonce;
	}

	// Block Constructor.
	public Block(String previousHash, long blockno) {
		this.blockNo = blockno;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash(); // Making sure we do this after we set the
										// other values.
	}

	// Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty); // Create a
																	// string
																	// with
																	// difficulty
																	// * "0"

		BLOCKMINING mine = new BLOCKMINING(hash, previousHash, merkleRoot, timeStamp, difficulty, target);

		MiningUnit.mine = mine;

		new MiningDialog();

		if (!GO.STOPMINING) {
			nonce = BLOCKMINING.nonce;
			hash = BLOCKMINING.hash;
		}
	}

	// Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		// process transaction and check if valid, unless block is genesis block
		// then ignore.
		if (transaction == null) {
			return false;
		}
		
		boolean isReward = false;
		if(transactions.indexOf(transaction)==0 || transactions.size()==0){
			isReward = true;
		}
		
		if ((previousHash != "0")) {
			if (!isReward) {
				if ((transaction.processTransaction() != true)) {
					OUT.DEBUG("Transaction failed to process. Discarded.");
					return false;
				}
			}
		}
		transactions.add(transaction);
		OUT.DEBUG("Transaction Successfully added to Block");
		return true;
	}
	
	// Add transactions to this block
	public boolean addSmartContract(SmartContract smartcontract) {
		// process transaction and check if valid, unless block is genesis block
		// then ignore.
		if (smartcontract == null) {
			return false;
		}
//		boolean isReward = smartcontract.inputs == null;
//		if ((previousHash != "0")) {
//			if (!isReward) {
//				if ((smartcontract.processTransaction() != true)) {
//					OUT.DEBUG("Transaction failed to process. Discarded.");
//					return false;
//				}
//			}
//		}
		contracts.add(smartcontract);
		OUT.DEBUG("SmartContract Successfully added to Block");
		return true;
	}

	@Override
	public byte[] writeToDB() {
		Object[] content = new Object[8];
		content[0] = hash;
		content[1] = previousHash;
		content[2] = merkleRoot;
		content[3] = timeStamp;
		content[4] = nonce;
		content[5] = blockNo;
		
		//write tx otherwise not this type
		String[] txs = new String[transactions.size()];
		for(int i = 0; i< transactions.size();i++){
			txs[i] = transactions.get(i).transactionId;
		}
		content[6] = txs;
		//write sc otherwise not this type
//		content[7] = contracts;
		String[] scs = new String[contracts.size()];
		for(int i = 0; i< contracts.size();i++){
			scs[i] = contracts.get(i).id;
		}
		content[7] = scs;
		return ByteUtils.createParcel(content);
	}
	
	@Override
	public void readFromDB(byte[] store) {
		if(store== null){
			return;
		}
		
		int additionalLength = 5;
		
		int length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.hash = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.previousHash = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.merkleRoot = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.timeStamp = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.nonce = ByteUtils.bytesToInt(ByteUtils.getBytesRawFromTo(5, length, store));
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.blockNo = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) /*+ additionalLength*/;
//		int value = (length & 0xff)
		while (length > 0) {
//		for (int i = 0; i < (store.length); i++) {
			if (store.length != length) {
				if (length != 0) {
					if(store[0] == ByteUtils.PREFIXSTRINGARRAY){
						store = ByteUtils.removeReaded(additionalLength, store);
					}
					int lengthOfTx = new BigInteger(ByteUtils.getBytesRawFromTo(0, 4, store)).intValue();
					this.transactions.add(ByteUtils.bytesToTransaction(ByteUtils.getBytesRawFromTo(4, 4+lengthOfTx, store)));
					store = ByteUtils.removeReaded(lengthOfTx+4, store);
					length = length - (lengthOfTx+4);
				}
			}
		}

		//CONTRACTS BEGIN
		if(store.length==0){
			return;
		}
		length = ByteUtils.getLengthNextByteObject(store) /*+ additionalLength*/;
		while (length > 0) {
			if (store.length != length) {
				if (length != 0) {
					if(store[0] == ByteUtils.PREFIXSTRINGARRAY){
						store = ByteUtils.removeReaded(additionalLength, store);
					}
					int lengthOfTx = new BigInteger(ByteUtils.getBytesRawFromTo(0, 4, store)).intValue();
					this.contracts.add(ByteUtils.bytesToContract(ByteUtils.getBytesRawFromTo(4, 4+lengthOfTx, store)));
					store = ByteUtils.removeReaded(lengthOfTx+4, store);
					length = length - (lengthOfTx+4);
				}
			}
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blockNo ^ (blockNo >>> 32));
		result = prime * result + ((contracts == null) ? 0 : contracts.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((merkleRoot == null) ? 0 : merkleRoot.hashCode());
		result = prime * result + nonce;
		result = prime * result + ((previousHash == null) ? 0 : previousHash.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
		result = prime * result + ((transactions == null) ? 0 : transactions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (blockNo != other.blockNo)
			return false;
		if (contracts == null) {
			if (other.contracts != null)
				return false;
		} else if (!contracts.equals(other.contracts))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (merkleRoot == null) {
			if (other.merkleRoot != null)
				return false;
		} else if (!merkleRoot.equals(other.merkleRoot))
			return false;
		if (nonce != other.nonce)
			return false;
		if (previousHash == null) {
			if (other.previousHash != null)
				return false;
		} else if (!previousHash.equals(other.previousHash))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		return true;
	}

	
	public Block receiveFromNetwork(byte[] store){
		if(store== null){
			return null;
		}
		
		int additionalLength = 5;
		
		int length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.hash = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.previousHash = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.merkleRoot = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.timeStamp = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.nonce = ByteUtils.bytesToInt(ByteUtils.getBytesRawFromTo(5, length, store));
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.blockNo = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) /*+ additionalLength*/;
//		int value = (length & 0xff)
//		while (length > 0) {
		while(store[0] != ByteUtils.PREFIXCONTRACTS){
//		for (int i = 0; i < (store.length); i++) {
			if (store.length != length) {
				if (length != 0) {
					if(store[0] == ByteUtils.PREFIXTRANSACTIONS){
						store = ByteUtils.removeReaded(additionalLength, store);
					}
					int lengthOfTx = new BigInteger(ByteUtils.getBytesRawFromTo(0, 4, store)).intValue();
					this.transactions.add(new Transaction(ByteUtils.getBytesRawFromTo(4, 4+lengthOfTx, store)));
//					this.transactions.add(ByteUtils.bytesToTransaction(ByteUtils.getBytesRawFromTo(1, 4+lengthOfTx, store)));
					store = ByteUtils.removeReaded(lengthOfTx+4, store);
					length = length - (lengthOfTx+4);
				} else {
					store = ByteUtils.removeReaded(additionalLength, store);
//					break;
				}
			}
		}

		//CONTRACTS BEGIN
		if(store.length==0){
			return this;
		}
		length = ByteUtils.getLengthNextByteObject(store) /*+ additionalLength*/;
		while (length > 0) {
			if (store.length != length) {
				if (length != 0) {
					if(store[0] == ByteUtils.PREFIXCONTRACTS){
						store = ByteUtils.removeReaded(additionalLength, store);
					}
					int lengthOfTx = new BigInteger(ByteUtils.getBytesRawFromTo(0, 4, store)).intValue();
					this.contracts.add(new SmartContract(ByteUtils.getBytesRawFromTo(4, 4+lengthOfTx, store)));
//					this.contracts.add(ByteUtils.bytesToContract(ByteUtils.getBytesRawFromTo(4, 4+lengthOfTx, store)));
					store = ByteUtils.removeReaded(lengthOfTx+4, store);
					length = length - (lengthOfTx+4);
				}
			}
		}
		
		return this;
	}
	
	public byte[] writeToNetwork(){
		Object[] content = new Object[8];
		content[0] = hash;
		content[1] = previousHash;
		content[2] = merkleRoot;
		content[3] = timeStamp;
		content[4] = nonce;
		content[5] = blockNo;
		
		//write tx otherwise not this type
		Transaction[] txs = new Transaction[transactions.size()];
		for(int i = 0; i< transactions.size();i++){
			txs[i] = transactions.get(i);
		}
		content[6] = txs;
		//write sc otherwise not this type
//		content[7] = contracts;
		SmartContract[] scs = new SmartContract[contracts.size()];
		for(int i = 0; i< contracts.size();i++){
			scs[i] = contracts.get(i);
		}
		content[7] = scs;
		return ByteUtils.createParcel(content);
	}




}