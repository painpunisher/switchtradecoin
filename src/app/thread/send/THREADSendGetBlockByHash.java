package app.thread.send;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.log.PeerException;
import app.net.NETCOMMANDS;

/**
 * This class is only sending a command to peers to send the blockchain.
 * 
 * @author AT
 *
 */
public class THREADSendGetBlockByHash implements Runnable {

	private String mAddress = "";
	private int mPort = 0;
	private String mblockHashToGet = "";

	private String beforeCommand = "";
	private String afterCommand = "";
	private NETCOMMANDS mCommand = NETCOMMANDS.GETBLOCKBYHASH;

	public THREADSendGetBlockByHash(String address, int port, String blockHashToGet) {
		mAddress = address;
		mPort = port;
		mblockHashToGet = blockHashToGet;
	}

	@Override
	public void run() {
		beforeCommand = mCommand.name() + "";
		afterCommand = mCommand.name() + "_END";
		try {
			Socket socket = null;
			socket = new Socket(mAddress, mPort);

			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			OUT.DEBUG("COMMAND: " + beforeCommand + " SENDED TO: " + mAddress + " Port: " + mPort);

			oos.writeObject(Cons.RECEIVERPORT);

			oos.writeObject(beforeCommand);

			oos.writeObject(mblockHashToGet);

			oos.writeObject(afterCommand);

			oos.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			new PeerException(mCommand, mAddress, mPort, e);
		}

	}

}
