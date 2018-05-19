package app.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import app.config.Cons;
import app.log.OUT;
import app.thread.ReceiveHandler;

public class RECEIVER extends Thread {

	public ServerSocket mServerSocket;

	public RECEIVER() {

	}

	@Override
	public void run() {
		// super.run();
		try {
			OUT.DEBUG("[R] Receiver started.");
			mServerSocket = new ServerSocket(Cons.RECEIVERPORT);
			OUT.DEBUG("[R] Receiver Waiting for Client ...");
			while (mServerSocket.isBound() && !mServerSocket.isClosed()) {
				Socket client = mServerSocket.accept();
				OUT.DEBUG("RECEIVED A MESSAGE FROM HOST: " + client.getInetAddress().getHostAddress() + " : " + client.getPort() + " : " + client.getLocalPort() + " !");
				ReceiveHandler.handle(client);
			}

		} catch (IOException e) {
			OUT.ERROR("ERROR RECEIVER IOException ", e);
		} catch (ClassNotFoundException e) {
			OUT.ERROR("ERROR RECEIVER ClassNotFoundException ", e);
		}
	}

}
