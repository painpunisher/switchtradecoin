package app.thread.send;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.log.PeerException;
import app.net.NETCOMMANDS;
import app.net.NodeRegister;
import app.start.util.GO;

/**
 * This class is only sending a command to peers to send the block.
 * 
 * @author AT
 *
 */
public class THREADSendGetLatestBlockNo implements Runnable {

	private String mAddress = "";
	private int mPort = 0;
	private String beforeCommand = "";
	private String afterCommand = "";
	private NETCOMMANDS mCommand = NETCOMMANDS.GETLATESTBLOCKNO;

	public static int cannotConnect = 0;

	public THREADSendGetLatestBlockNo(String address, int port) {
		mAddress = address;
		mPort = port;
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
			OUT.DEBUG("MY IP: " + GO.receiver.mServerSocket.getInetAddress().getHostAddress() + " MY PORT: "
					+ Cons.RECEIVERPORT);

			oos.writeObject(Cons.RECEIVERPORT);

			oos.writeObject(beforeCommand);

			oos.writeObject(mCommand);

			oos.writeObject(afterCommand);

			oos.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			cannotConnect++;
			GO.countOfGetLatestBlockNoPeers--;
			new PeerException(mCommand, mAddress, mPort, e);
			if (NodeRegister.getInstance().getNodes().size() == cannotConnect) {
				OUT.ERROR("No Peers available to Synchronize begin Solo-Mining!");
			}
		}

	}

}
