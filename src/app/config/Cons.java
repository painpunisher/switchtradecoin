package app.config;

public class Cons {
	
	public final static String ALGORITHM = Config.INSTANCE.getProperty("ALGORITHM");
	public final static int RECEIVERPORT = Integer.parseInt(Config.INSTANCE.getProperty("RECEIVERPORT"));
	public final static int DIFFICULTY = 4; //Integer.parseInt(Conf.INSTANCE.getProperty("DIFFICULTY"));
	public final static int BLOCKTIME = 30000; //Integer.parseInt(Conf.INSTANCE.getProperty("BLOCKTIME"));
	public static boolean CLIENTMODE = Boolean.parseBoolean(Config.INSTANCE.getProperty("CLIENTMODE"));
	public static boolean DEBUGMODE = Boolean.parseBoolean(Config.INSTANCE.getProperty("DEBUGMODE"));
	public static boolean MININGMODE = Boolean.parseBoolean(Config.INSTANCE.getProperty("MININGMODE"));
	public static boolean NODEDISCOVERY = Boolean.parseBoolean(Config.INSTANCE.getProperty("NODEDISCOVERY"));
	//NODEDISCOVERY
	
	public static int MAXNODECOUNT = 99;	
}
