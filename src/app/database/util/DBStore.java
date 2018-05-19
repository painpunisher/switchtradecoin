package app.database.util;

public interface DBStore {
	
	public void readFromDB(byte[] store);
	
	public byte[] writeToDB();
	
//	public void receiveFromNetwork(byte[] block);
//	
//	public byte[] writeToNetwork();
}
