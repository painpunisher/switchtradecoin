package app.net;

import java.util.LinkedList;
import java.util.List;

import app.config.Config;
import app.log.OUT;

public class NodeRegister {
	private static NodeRegister mInstance; 
	private LinkedList<Node> mNodes = new LinkedList<Node>();
	
	static {
	}
	
	public static NodeRegister getInstance() {
		if (mInstance == null) {
			mInstance = new NodeRegister();
//			mInstance.addTestAddresses();
			mInstance.loadNodes();
		}
		return mInstance;
	}
	
	public List<Node> getNodes() {
		return mNodes;
	}
	
	public void addNode(String ip, int port) {
		Node node = new Node(ip, port);
		if(node.getIP().length()>7){
			mNodes.add(node);
		}
	}
	
	public void saveNodes(){
		int i =1;
		for(Node cur : mNodes){
			Config.INSTANCE.setProperty("peer" + i, cur.getIP());
			Config.INSTANCE.setProperty("port" + i, ""+cur.getPort());
			i++;
		}
		Config.saveProperties();
	}
	
	private void loadNodes(){
		try {
			for(int i = 1; i<999;i++){
				addNode(Config.INSTANCE.getProperty("peer"+i), Integer.parseInt(Config.INSTANCE.getProperty("port"+i)));
			}
		} catch (Exception e) {
			OUT.DEBUG("Loading the Nodes finished!");
		}
	}
	
	public void addTestAddresses() {
		addNode(Config.INSTANCE.getProperty("peer1"), Integer.parseInt(Config.INSTANCE.getProperty("port1")));
		addNode(Config.INSTANCE.getProperty("peer2"), Integer.parseInt(Config.INSTANCE.getProperty("port2")));
		addNode(Config.INSTANCE.getProperty("peer3"), Integer.parseInt(Config.INSTANCE.getProperty("port3")));
		addNode(Config.INSTANCE.getProperty("peer4"), Integer.parseInt(Config.INSTANCE.getProperty("port4")));
		addNode(Config.INSTANCE.getProperty("peer5"), Integer.parseInt(Config.INSTANCE.getProperty("port5")));
		addNode(Config.INSTANCE.getProperty("peer6"), Integer.parseInt(Config.INSTANCE.getProperty("port6")));
		addNode(Config.INSTANCE.getProperty("peer7"), Integer.parseInt(Config.INSTANCE.getProperty("port7")));
		addNode(Config.INSTANCE.getProperty("peer8"), Integer.parseInt(Config.INSTANCE.getProperty("port8")));
	}
}
