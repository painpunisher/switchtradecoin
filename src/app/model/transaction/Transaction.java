package app.model.transaction;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import app.core.MainMethods;
import app.database.util.ByteUtils;
import app.database.util.DBStore;
import app.log.OUT;
import app.start.util.GO;
import app.util.StringUtil;

public class Transaction implements /* Serializable */DBStore {

	public String transactionId; // this is also the hash of the transaction.
	// @Expose(serialize = false, deserialize = true)
	public PublicKey sender; // senders address/public key.
	// @Expose(serialize = false, deserialize = true)
	public PublicKey reciepient; // Recipients address/public key.
	public String strSender;
	public String strReciepient;
	public BigInteger value;
	public byte[] signature; // this is to prevent anybody else from spending
								// funds in our wallet.
	public long date = new Date().getTime();
	public long sequence = 0; // a rough count of how many transactions have
								// been generated.
	public boolean isProcessed = false;
	public boolean mIsReward = false;

	public Transaction(byte[] store) {
		readFromDB(store);
	}

	// Constructor:
	public Transaction(PublicKey from, PublicKey to,
			BigInteger value/* , ArrayList<TransactionInput> inputs */) {
		this.sender = from;
		this.strSender = StringUtil.getStringFromKey(sender);
		this.reciepient = to;
		this.strReciepient = StringUtil.getStringFromKey(reciepient);
		this.value = value;
		// this.inputs = inputs;
		generateTransactionID();
	}

	private void generateTransactionID() {
		transactionId = calulateHash();
	}

	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		// sequence++; //increase the sequence to avoid 2 identical transactions
		// having the same hash
		sequence = new Random().nextLong(); // random numbergenerating to avoid
											// 2 exact same transactions.
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ date + new String(value.toByteArray()) + sequence);
	}

	// Signs all the data we dont wish to be tampered with.
	public byte[] generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ new String(value.toByteArray());
		signature = StringUtil.applyECDSASig(privateKey, data);
		return signature;
	}

	// Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
		if (sender == null) {
			sender = StringUtil.getKey(strSender);
		}
		if (reciepient == null) {
			reciepient = StringUtil.getKey(strReciepient);
		}
		String data = (strSender) + (strReciepient) + new String(value.toByteArray());
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	// Returns true if new transaction could be created.
	public boolean processTransaction() {
		boolean isReward = mIsReward;

		if (isProcessed) {
			OUT.DEBUG("Transaction failed to process. Already Exists.");
			return false;
		}
		if (verifiySignature() == false) {
			OUT.DEBUG("#Transaction Signature failed to verify");
			return false;
		}

		// check if transaction is valid:
		if (MainMethods.getBalance(sender).intValue() < GO.minimumTransaction) {
			if (isReward) { // reward check
				OUT.DEBUG("REWARD SKIP CHECK");
			} else {
				OUT.DEBUG("#Transaction Inputs to small: " /*
															 * +
															 * getInputsValue()
															 */);
				return false;
			}
		}

		isProcessed = true;
		return true;
	}

	@Override
	public byte[] writeToDB() {
		Object[] content = new Object[9];
		content[0] = transactionId;
		content[1] = strSender;
		content[2] = strReciepient;
		content[3] = value;
		content[4] = signature;
		content[5] = date;
		content[6] = sequence;
		content[7] = isProcessed;
		content[8] = mIsReward;
		return ByteUtils.createParcel(content);
	}

	@Override
	public void readFromDB(byte[] store) {
		if (store == null) {
			return;
		}

		int additionalLength = 5;

		int length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.transactionId = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.strSender = ByteUtils.bytesToString(store);
		this.sender = StringUtil.getKey(strSender);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.strReciepient = ByteUtils.bytesToString(store);
		this.reciepient = StringUtil.getKey(strReciepient);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.value = ByteUtils.bytesToBigInteger(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.signature = ByteUtils.getBytesRaw(length, store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.date = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.sequence = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.isProcessed = ByteUtils.bytesToBoolean(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.mIsReward = ByteUtils.bytesToBoolean(store);
		store = ByteUtils.removeReaded(length, store);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (date ^ (date >>> 32));
		result = prime * result + (isProcessed ? 1231 : 1237);
		result = prime * result + (mIsReward ? 1231 : 1237);
		result = prime * result + ((reciepient == null) ? 0 : reciepient.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + (int) (sequence ^ (sequence >>> 32));
		result = prime * result + Arrays.hashCode(signature);
		result = prime * result + ((strReciepient == null) ? 0 : strReciepient.hashCode());
		result = prime * result + ((strSender == null) ? 0 : strSender.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Transaction other = (Transaction) obj;
		if (date != other.date)
			return false;
		if (isProcessed != other.isProcessed)
			return false;
		if (mIsReward != other.mIsReward)
			return false;
		if (reciepient == null) {
			if (other.reciepient != null)
				return false;
		} else if (!reciepient.equals(other.reciepient))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		if (sequence != other.sequence)
			return false;
		if (!Arrays.equals(signature, other.signature))
			return false;
		if (strReciepient == null) {
			if (other.strReciepient != null)
				return false;
		} else if (!strReciepient.equals(other.strReciepient))
			return false;
		if (strSender == null) {
			if (other.strSender != null)
				return false;
		} else if (!strSender.equals(other.strSender))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getStrSender() {
		return strSender;
	}

	public void setStrSender(String strSender) {
		this.strSender = strSender;
	}

	public String getStrReciepient() {
		return strReciepient;
	}

	public void setStrReciepient(String strReciepient) {
		this.strReciepient = strReciepient;
	}

	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		return sdf.format(new Date(date));
	}

	public void setDate(long date) {
		this.date = date;
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

}