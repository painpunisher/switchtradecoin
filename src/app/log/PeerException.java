package app.log;

import java.io.IOException;

import app.net.NETCOMMANDS;

public class PeerException extends IOException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6181090006692295144L;

	public PeerException(NETCOMMANDS cmd, String address, int port, Exception e){
		OUT.ERROR("["+cmd+"] " + "Cannot connect to Peer: " + address + " > Port: " + port + "!");
//		if(Cons.DEBUGMODE){
//			OUT.ERROR("ERROR PEER CONNECTION ", e);
//		}
	}
	
}
