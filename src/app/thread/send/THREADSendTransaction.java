package app.thread.send;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.log.PeerException;
import app.model.transaction.Transaction;
import app.net.NETCOMMANDS;

public class THREADSendTransaction implements Runnable {
	private String mAddress = "";
	private int mPort = 0;
	private String beforeCommand = "";
	private String afterCommand = "";
	private NETCOMMANDS mCommand = NETCOMMANDS.TRANSACTION;
	private Transaction mTransaction;
	public THREADSendTransaction(String address, int port, Transaction transaction) {
		mAddress = address;
		mPort = port;
		mTransaction = transaction;
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

			OUT.DEBUG("[S] Sending Transaction to " + mAddress);
			oos.writeObject(mTransaction.writeToDB());

			oos.writeObject(afterCommand);

			oos.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			new PeerException(mCommand, mAddress, mPort, e);
		}

	}
}
