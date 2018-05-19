package app.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import app.log.OUT;
import app.net.NETCOMMANDS;
import app.thread.receive.THREADReceiveBlock;
import app.thread.receive.THREADReceiveGetBlockByHash;
import app.thread.receive.THREADReceiveGetBlockByNo;
import app.thread.receive.THREADReceiveGetLatestBlockNo;
import app.thread.receive.THREADReceiveGetNodes;
import app.thread.receive.THREADReceiveLatestBlockNo;
import app.thread.receive.THREADReceiveNode;
import app.thread.receive.THREADReceiveTransaction;

public class ReceiveHandler {

	public static void handle(final Socket client) throws IOException, ClassNotFoundException {
		InputStream in = client.getInputStream();

		ObjectInputStream ois = new ObjectInputStream(in);

		int port = (int) ois.readObject();
		
		String firstString = (String) ois.readObject();
		NETCOMMANDS command = null;
		for (NETCOMMANDS cmd : NETCOMMANDS.values()) {
			if (cmd.name().equals(firstString)) {
				command = cmd;
			}
		}

		String ip = client.getInetAddress().getHostAddress();

		switch (command) {
		case BLOCKCHAIN:
			// blockchain arriving here
//			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveBlockchain(ois.readObject()));
			break;
		case GETBLOCKCHAIN:
			// command for getblockchain arrives here
//			OUT.DEBUG((NETCOMMANDS) ois.readObject() + "");
//			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveGetBlockchain(ip, port));
			break;
		case BLOCK:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveBlock(ois.readObject()));
			break;
		case TRANSACTION:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveTransaction(ois.readObject()));
			break;
		case GETBLOCKBYHASH:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveGetBlockByHash(ip, port, ois.readObject()));
			break;
		case GETBLOCKBYNO:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveGetBlockByNo(ip, port, ois.readObject()));
			break;
		case GETLATESTBLOCKNO:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveGetLatestBlockNo(ip, port, ois.readObject()));
			break;
		case LATESTBLOCKNO:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveLatestBlockNo(ip, port, ois.readObject()));
			break;
		case GETNODES:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveGetNodes(ip, port, ois.readObject()));
			break;
		case NODE:
			ReceiveExecuter.RECEIVEEXECUTER.submit(new THREADReceiveNode(ois.readObject()));
			break;
		default:
			OUT.DEBUG("NOT HANDLEABLE COMMAND IN RECEIVER: " + command.name().toString());
			ois.readObject();
			break;
		}
		ois.readObject();

		client.close();
	}
}
