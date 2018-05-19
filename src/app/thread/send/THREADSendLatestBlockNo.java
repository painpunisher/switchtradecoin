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
 * This class is only sending a command to peers to send the block.
 * 
 * @author AT
 *
 */
public class THREADSendLatestBlockNo implements Runnable {

	private String mAddress = "";
	private int mPort = 0;
	private int mLatestBlockNo = 0;
	
	private String beforeCommand = "";
	private String afterCommand = "";
	private NETCOMMANDS mCommand = NETCOMMANDS.LATESTBLOCKNO;

	public THREADSendLatestBlockNo(String address, int port, int latestBlockNo) {
		mAddress = address;
		mPort = port;
		mLatestBlockNo = latestBlockNo;
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

			oos.writeObject(mLatestBlockNo);

			oos.writeObject(afterCommand);

			oos.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			new PeerException(mCommand, mAddress, mPort, e);
		}

	}

}
