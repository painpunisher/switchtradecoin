package app.model.account;

import java.math.BigInteger;

import app.database.util.ByteUtils;
import app.database.util.DBStore;

public class AccountState implements DBStore {
	public BigInteger balance=new BigInteger("0");

	/*
	 * This nonce is equal to the last verified transaction nonce - the tx nonce
	 * is signed (along with other data) as part of the transaction: it prevents
	 * others replaying a transaction to continously spending it as a new valid
	 * transaction must have a value larger than this recorded nonce value.
	 */
	public BigInteger nonce=new BigInteger("0");

	
	
	@Override
	public void readFromDB(byte[] store) {
		if(store== null){
			return;
		}
		int length = ByteUtils.getLengthNextByteObject(store) + 5;
		this.balance = ByteUtils.bytesToBigInteger(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + 5;
		this.nonce = ByteUtils.bytesToBigInteger(store);
		store = ByteUtils.removeReaded(length, store);
	}

	@Override
	public byte[] writeToDB() {
		Object[] content = new Object[2];
		content[0] = balance;
		content[1] = nonce;
		return ByteUtils.createParcel(content);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((nonce == null) ? 0 : nonce.hashCode());
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
		AccountState other = (AccountState) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (nonce == null) {
			if (other.nonce != null)
				return false;
		} else if (!nonce.equals(other.nonce))
			return false;
		return true;
	}

}
