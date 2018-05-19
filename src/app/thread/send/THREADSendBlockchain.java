package app.thread.send;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.log.PeerException;
import app.net.NETCOMMANDS;
import app.start.util.GO;

/**
 * This class is only sending the blockchain to a peer.
 * 
 * @author AT
 *
 */

public class THREADSendBlockchain implements Runnable {

	private String mAddress = "";
	private int mPort = 0;

	private String beforeCommand = "";
	private String afterCommand = "";
	private NETCOMMANDS mCommand = NETCOMMANDS.BLOCKCHAIN;

	public THREADSendBlockchain(String address, int port) {
		mAddress = address;
		mPort = port;
	}

	@Override
	public void run() {
		beforeCommand = mCommand.name();
		afterCommand = mCommand.name() + "END";
		try {
			Socket socket = null;
			socket = new Socket(mAddress, mPort);

			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			oos.writeObject(Cons.RECEIVERPORT);

			oos.writeObject(beforeCommand);

			OUT.DEBUG("[S] Sending Chain to " + mAddress);
			oos.writeObject(GO.BLOCKCHAIN);

			oos.writeObject(afterCommand);

			oos.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			new PeerException(mCommand, mAddress, mPort, e);
		}

	}

}
