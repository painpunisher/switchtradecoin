package app.net;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.start.util.GO;

public class Node implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6498301526852133011L;
	private String mIP = "";
	private int mPort = 0;
	private int mReadedFromDeadNodes = 0;
	private boolean mActive = false;
	Node(String ip, int port){
		setIP(ip);
		setPort(port);
		setActive(hostAvailabilityCheck());
	}
	
	Node(String ip, int port, int readed){
		setIP(ip);
		setPort(port);
		setReadedFromDeadNodes(readed);
		setActive(hostAvailabilityCheck());
	}

	public String getIP() {
		return mIP;
	}

	public void setIP(String mIP) {
		this.mIP = mIP;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int mPort) {
		this.mPort = mPort;
	}
	
	public boolean hostAvailabilityCheck() { 
		synchronized (this) {
			if (getIP().length() > 7) {
				boolean b = true;
				try {
					InetSocketAddress sa = new InetSocketAddress(getIP(), getPort());
					Socket ss = new Socket();
					ss.connect(sa, 500); // --> change from 1 to 500 (for
											// example)
					NETCOMMANDS command = NETCOMMANDS.ISALIVE;
					OutputStream os = ss.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					OUT.DEBUG("COMMAND: " + command + " SENDED TO: " + getIP() + " Port: " + getPort());
					OUT.DEBUG("MY IP: " + GO.receiver.mServerSocket.getInetAddress().getHostAddress() + " MY PORT: " + Cons.RECEIVERPORT);

					oos.writeObject(Cons.RECEIVERPORT);
					
					oos.writeObject(command.toString());

					oos.writeObject(command);

					oos.writeObject(command.toString());

					oos.close();
					os.close();
					ss.close();
				} catch (Exception e) {
					b = false;
				}
				return b;
			}
			return false;
		}
	}

	boolean isSameAs(final Node mNode) {
		boolean same = false;
		if(getIP().equals(mNode.getIP())){
			same = true;
		} else{
			return false;
		}
		if(getPort() == mNode.getPort()){
			same = true;
		} else {
			return false;
		}
		return same;
	}

	public int getReadedFromDeadNodes() {
		return mReadedFromDeadNodes;
	}

	public void setReadedFromDeadNodes(int mReadedFromDeadNodes) {
		this.mReadedFromDeadNodes = mReadedFromDeadNodes;
	}

	public boolean isActive() {
		return mActive;
	}

	public void setActive(boolean mActive) {
		this.mActive = mActive;
	}
	
}
