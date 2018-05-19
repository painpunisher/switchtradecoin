package app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import app.log.OUT;

public class Config {

	public static Properties INSTANCE = new Properties();

	public static void saveProperties() {
		try {
			FileOutputStream fr;
			fr = new FileOutputStream(FilePathCollection.CONFIG_PATH_TO_FILE);
			INSTANCE.store(fr, "Properties");
			fr.close();
			System.out.println("After saving properties: " + INSTANCE);
		} catch (IOException e) {
			OUT.ERROR("ERROR SAVING PROPERTIES: ", e);
		}
	}

	public static void loadProperties() {
		try {
			FileInputStream fi;
			fi = new FileInputStream(FilePathCollection.CONFIG_PATH_TO_FILE);
			INSTANCE.load(fi);
			fi.close();
			System.out.println("After Loading properties: " + INSTANCE);
		} catch (IOException e) {
			OUT.ERROR("ERROR LOADING PROPERTIES: ", e);
		}
	}

	private static boolean checkIfAllConfigParameterExist() {
		try {
			loadProperties();

			if (INSTANCE.getProperty("ALGORITHM") == null)
				return false;
			if (INSTANCE.getProperty("RECEIVERPORT") == null)
				return false;
			if (INSTANCE.getProperty("DIFFICULTY") == null)
				return false;
			if (INSTANCE.getProperty("BLOCKTIME") == null)
				return false;
			if (INSTANCE.getProperty("CLIENTMODE") == null)
				return false;
			if (INSTANCE.getProperty("DEBUGMODE") == null)
				return false;
			if (INSTANCE.getProperty("MININGMODE") == null)
				return false;
			if (INSTANCE.getProperty("NODEDISCOVERY") == null)
				return false;

			if (Config.INSTANCE.getProperty("peer1") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port1")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer2") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port2")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer3") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port3")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer4") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port4")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer5") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port5")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer6") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port6")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer7") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port7")) == null)
				return false;
			if (Config.INSTANCE.getProperty("peer8") == null)
				return false;
			if ((Config.INSTANCE.getProperty("port8")) == null)
				return false;

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void init() {

		boolean success = (new File(FilePathCollection.CONFIG_ONLY_PATH).mkdirs());
		System.out.println("Config file created: " + success);
		if (!checkIfAllConfigParameterExist()) {
			File f = new File(FilePathCollection.CONFIG_PATH_TO_FILE);
			f.delete();
		}

		File f = new File(FilePathCollection.CONFIG_PATH_TO_FILE);
		if (f.exists() && !f.isDirectory()) {
			loadProperties();
			System.out.println("Config already exists doesnt set default values! Skip!");
			return;
		} else {
			System.out.println("Config does not exists, create and set default values!");
			File yourFile = new File(FilePathCollection.CONFIG_PATH_TO_FILE);
			try {
				yourFile.createNewFile();
			} catch (IOException e) {
				OUT.ERROR("Error while creating Config File ", e);
			} // if file already exists will do nothing
		}

		INSTANCE.setProperty("ALGORITHM", "ECDSA");
		INSTANCE.setProperty("RECEIVERPORT", "2000");
		INSTANCE.setProperty("DIFFICULTY", "5");
		INSTANCE.setProperty("BLOCKTIME", "10000");
		INSTANCE.setProperty("CLIENTMODE", "true");
		INSTANCE.setProperty("DEBUGMODE", "true");
		INSTANCE.setProperty("MININGMODE", "true");
		INSTANCE.setProperty("NODEDISCOVERY", "false");

		INSTANCE.setProperty("peer1", "0");
		INSTANCE.setProperty("peer2", "0");
		INSTANCE.setProperty("peer3", "0");
		INSTANCE.setProperty("peer4", "0");
		INSTANCE.setProperty("peer5", "0");
		INSTANCE.setProperty("peer6", "0");
		INSTANCE.setProperty("peer7", "0");
		INSTANCE.setProperty("peer8", "0");

		INSTANCE.setProperty("port1", "2000");
		INSTANCE.setProperty("port2", "2001");
		INSTANCE.setProperty("port3", "2002");
		INSTANCE.setProperty("port4", "2003");
		INSTANCE.setProperty("port5", "2004");
		INSTANCE.setProperty("port6", "2005");
		INSTANCE.setProperty("port7", "2006");
		INSTANCE.setProperty("port8", "2007");

		Config.saveProperties();
	}
}
