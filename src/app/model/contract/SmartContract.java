package app.model.contract;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import app.core.contract.ContractUtil;
import app.database.util.ByteUtils;
import app.database.util.DBStore;
import app.old.Wallet;
import app.start.util.GO;
import app.util.StringUtil;

public class SmartContract implements DBStore {
	public String getStatus() {
		return status.toString();
	}
	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public String getDesignedTimestamp() {
		return sdf.format(designedTimestamp);
	}
	public void setDesignedTimestamp(long designedTimestamp) {
		this.designedTimestamp = designedTimestamp;
	}
	public String getExpireTimestamp() {
		return sdf.format(expireTimestamp);
	}
	public void setExpireTimestamp(long expireTimestamp) {
		this.expireTimestamp = expireTimestamp;
	}
	public String getPrice() {
		return price.toString();
	}
	public void setPrice(BigInteger price) {
		this.price = price;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getBalance(){
		return ContractUtil.getBalance(publicKey) + "";
	}

	/**
	 * 
	 */
	

	
	public String id;//id of contract.
	public String designer;// publickey designer of the smartcontract.
	public String acceptor; // acceptor of contract.
	public long designedTimestamp;// the time this contract was designed.
	public long expireTimestamp; //the time this contract expires.
	public String contractDescription;//the text what is todo in this contract.
	public boolean isPublic;//defines if it should be visible or not. Public contracts will be setted private after expire reached.
	public BigInteger price; // the price for this contract.
	public String location;//where is this contract located.
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public ContractStatus status;
	public int statusProofed;
	public SmartContract(Date expire, String description, String location, BigInteger price, boolean isPublic){
		id = "sc_"+StringUtil.applySha256(new Date().getTime() + "" + new Random().nextLong() + "");
		designer = StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey);
		acceptor = "";
		designedTimestamp = new Date().getTime();
		this.expireTimestamp = expire.getTime() + 1000000;
		this.contractDescription = description;
		this.isPublic = isPublic;
		this.price = price;
		this.location = location;
		Wallet w = new Wallet();
		publicKey = w.publicKey;
		privateKey = w.privateKey;
		status = ContractStatus.DESIGNING;
		statusProofed = 0;
	}
	public SmartContract(byte[] select) {
		readFromDB(select);
	}
	@Override
	public void readFromDB(byte[] store) {
		if(store== null){
			return;
		}
		
		int additionalLength = 5;
		
		int length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.id = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);

		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.designer = ByteUtils.bytesToString(store);
//		this.sender = StringUtil.getKey(designer);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.acceptor = ByteUtils.bytesToString(store);
//		this.reciepient = StringUtil.getKey(strReciepient);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.designedTimestamp = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.expireTimestamp = ByteUtils.bytesToLong(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.contractDescription = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.isPublic = ByteUtils.bytesToBoolean(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.price = ByteUtils.bytesToBigInteger(store);

		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.location = ByteUtils.bytesToString(store);
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.publicKey = StringUtil.getKey(ByteUtils.bytesToString(store));
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.privateKey = StringUtil.getPrivateKeyFromByte(ByteUtils.getBytesRawFromTo(5, length, store));
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		for(ContractStatus cur : ContractStatus.values()){
			if(ByteUtils.bytesToString(store).equals(cur.toString())){
				this.status = cur;
			}
		}
		store = ByteUtils.removeReaded(length, store);
		
		length = ByteUtils.getLengthNextByteObject(store) + additionalLength;
		this.statusProofed = ByteUtils.bytesToInt(ByteUtils.getBytesRawFromTo(5, length, store));
		store = ByteUtils.removeReaded(length, store);
		

	}

	@Override
	public byte[] writeToDB() {
		Object[] content = new Object[13];
		content[0] = id;
		content[1] = designer;
		content[2] = acceptor;
		content[3] = designedTimestamp;
		content[4] = expireTimestamp;
		content[5] = contractDescription;
		content[6] = isPublic;
		content[7] = price;
		content[8] = location;
		content[9] = StringUtil.getStringFromKey(publicKey);
		content[10] = (privateKey.getEncoded());
		content[11] = status.toString();
		content[12] = statusProofed;
		return ByteUtils.createParcel(content);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acceptor == null) ? 0 : acceptor.hashCode());
		result = prime * result + ((contractDescription == null) ? 0 : contractDescription.hashCode());
		result = prime * result + (int) (designedTimestamp ^ (designedTimestamp >>> 32));
		result = prime * result + ((designer == null) ? 0 : designer.hashCode());
		result = prime * result + (int) (expireTimestamp ^ (expireTimestamp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((sdf == null) ? 0 : sdf.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + statusProofed;
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
		SmartContract other = (SmartContract) obj;
		if (acceptor == null) {
			if (other.acceptor != null)
				return false;
		} else if (!acceptor.equals(other.acceptor))
			return false;
		if (contractDescription == null) {
			if (other.contractDescription != null)
				return false;
		} else if (!contractDescription.equals(other.contractDescription))
			return false;
		if (designedTimestamp != other.designedTimestamp)
			return false;
		if (designer == null) {
			if (other.designer != null)
				return false;
		} else if (!designer.equals(other.designer))
			return false;
		if (expireTimestamp != other.expireTimestamp)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (sdf == null) {
			if (other.sdf != null)
				return false;
		} else if (!sdf.equals(other.sdf))
			return false;
		if (status != other.status)
			return false;
		if (statusProofed != other.statusProofed)
			return false;
		return true;
	}
}
