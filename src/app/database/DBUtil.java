package app.database;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import app.config.FilePathCollection;
import app.log.OUT;
import app.model.block.Block;

class DBUtil {

	// Eine (versteckte) Klassenvariable vom Typ der eigenen Klasse
	private static DBUtil instance;
	private DB mDatabase = null;
	public LinkedList<DBEnum> dbStorage = new LinkedList<>();
	private DBEnum currentDB = null;
	// Verhindere die Erzeugung des Objektes über andere Methoden
	private DBUtil () {}

	// Eine Zugriffsmethode auf Klassenebene, welches dir '''einmal''' ein
	// konkretes
	// Objekt erzeugt und dieses zurückliefert.
	static synchronized DBUtil getInstance(DBEnum type) {
		DBUtil.instance = new DBUtil();
		DBUtil.instance.connect(type);
		return DBUtil.instance;
	}
	
	boolean connect(DBEnum db){
//		for(DBEnum dbStorage : DBEnum.values()){
//			if(dbStorage.toString().equals(db)){
//				
//			}
//		}
//		if(currentDB!=null){
//			if(!currentDB.toString().equals(db.toString())){
//				currentDB = null;
//				disconnect();
//				mDatabase = null;
//			}
//		}
		
		Options options = new Options();
//		options.
		try {
			if(currentDB == null){
				if(mDatabase==null){
					currentDB = db;
//					OUT.DEBUG("#######CREATED DB: " + db.toString());
					System.out.println("#######CREATED DB: " + db.toString());
					mDatabase = factory.open(new File(FilePathCollection.DATA_PATH + "" + db.toString().toUpperCase()),
							options);
				}
			}
			
			return true;
		} catch (IOException e) {
			OUT.ERROR("", e);
			return false;
		}
	}
	
	boolean insert(byte[] key, byte[] value){
		try {
			mDatabase.put(key,value);
//			iteration();
			return true;
		} catch (Exception e) {
//			OUT.ERROR("", e);
			return false;
		}
	}
	
	byte[] select(byte[] key){
		try {
			byte[] value = (mDatabase.get((key)));
			return value;
		} catch (Exception e) {
//			OUT.ERROR("", e);
			return null;
		}
	}
	
	public boolean delete(String key){
		try {
			mDatabase.delete(bytes(key));
			return true;
		} catch (Exception e) {
			OUT.ERROR("", e);
			return false;
		}
	}
	
	private boolean disconnect(){
		try {
			mDatabase.close();
			return true;
		} catch (Exception e) {
			OUT.ERROR("", e);
			return false;
		}
	}
	
	boolean destroyDB(DBEnum db){
		disconnect();
		Options options = new Options();
		try {
			factory.destroy(new File(FilePathCollection.DATA_PATH + "" + db.toString().toUpperCase()), options);
			mDatabase = null;
			currentDB = null;
			return true;
		} catch (IOException e) {
			OUT.ERROR("", e);
			return false;
		}
	}
	
	public boolean writeBatch(){
		WriteBatch batch = mDatabase.createWriteBatch();
		try {
		  batch.delete(bytes("Denver"));
		  batch.put(bytes("Tampa"), bytes("green"));
		  batch.put(bytes("London"), bytes("red"));

		  mDatabase.write(batch);
		} finally {
		  // Make sure you close the batch to avoid resource leaks.
		  try {
			batch.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return true;
	}
	
	boolean iteration(){
		DBIterator iterator = mDatabase.iterator();
		try {
		  for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
		    String key = asString(iterator.peekNext().getKey());
		    String value = asString(iterator.peekNext().getValue());
		    OUT.DEBUG(key+" = "+value);
		  }
		} finally {
		  // Make sure you close the iterator to avoid resource leaks.
		  try {
			iterator.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return true;
	}

	public Block searchGenesisBlock() {
		DBIterator iterator = mDatabase.iterator();
		try {
		  for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
		    String key = asString(iterator.peekNext().getKey());
		    Block value = new Block(iterator.peekNext().getValue());
		    OUT.DEBUG(key+" = "+value);
		    if(value.previousHash.equals("0")){
		    	try {
					iterator.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	return value;
		    }
		  }
		} finally {
		  // Make sure you close the iterator to avoid resource leaks.
		  try {
			iterator.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return null;
	}
	
//	public boolean snapshot(){
//		ReadOptions ro = new ReadOptions();
//		ro.snapshot(mDatabase.getSnapshot());
//		try {
//		  
//		  // All read operations will now use the same 
//		  // consistent view of the data.
////		  ... = db.iterator(ro);
//		  mDatabase.get(bytes("Tampa"), ro);
//
//		} finally {
//		  // Make sure you close the snapshot to avoid resource leaks.
//		  ro.snapshot().close();
//		}
//		return true;
//	}
	
}
