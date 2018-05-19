package app.config;

import java.io.File;
import java.util.Date;

public class FilePathCollection {
	public static String CONFIG_ONLY_PATH = "." + File.separator + "conf" + File.separator + "";
	public static String CONFIG_PATH_TO_FILE = "." + File.separator + "conf" + File.separator + "config.properties";
	
	public static String LOG_PATH = "."+File.separator + "log"+File.separator;
	public static String LOG_FILE = "."+File.separator + "log"+File.separator+"MyLogFile"+(new Date()).getTime()+".log";
	
	public static String HELP_PATH = "." + File.separator + "help" + File.separator + "";
	public static String HELP_FILE = "." + File.separator + "help" + File.separator + "readme.pdf";
	
	public static String NODE_DISCOVERY_PATH = "." + File.separator + "conf" + File.separator + "";
	public static String NODE_DISCOVERY_FILE = "." + File.separator + "conf" + File.separator + "deadnodes.conf";
	
	public static String DATA_PATH = "." + File.separator + "data" + File.separator + "";
	public static String BLOCKCHAIN_FILE = "." + File.separator + "data" + File.separator + "blockchain";
//	public static String WALLET_FILE = "." + File.separator + "data" + File.separator + "config.properties";
	
//	public static String CONFIG_ONLY_PATH = "." + File.separator + "conf" + File.separator + "";
//	public static String CONFIG_PATH_TO_FILE = "." + File.separator + "conf" + File.separator + "config.properties";
	
}
