package app.net;

import java.util.concurrent.TimeUnit;

import app.log.OUT;
import app.model.block.Block;
import app.model.transaction.Transaction;
import app.start.util.GO;
import app.thread.SendExecuter;
import app.thread.send.THREADSendBlock;
import app.thread.send.THREADSendGetBlockchain;
import app.thread.send.THREADSendGetLatestBlockNo;
import app.thread.send.THREADSendGetNodes;
import app.thread.send.THREADSendLatestBlockNo;
import app.thread.send.THREADSendNodes;
import app.thread.send.THREADSendTransaction;

public class SENDER extends Thread {

	@Override
	public void run() {
		// super.run();
		OUT.DEBUG("[S] Sender started.");
		while (true) {
			synchronized (this) {
				try {
					OUT.DEBUG("[S] Sender Waiting");
					wait();
				} catch (Exception e) {
					OUT.ERROR("", e);
				}
			}
		}
	}

	public void sendGetBlockchainCommand(String exactNode, int port) {
		synchronized (this) {
			if (exactNode.length() > 0) {
				SendExecuter.SENDEXECUTER.submit(new THREADSendGetBlockchain(exactNode, port));
			} else {
				for (Node node : NodeRegister.getInstance().getNodes()) {
					SendExecuter.SENDEXECUTER.submit(new THREADSendGetBlockchain(node.getIP(), node.getPort()));
				}
			}
			notifyAll();
		}
	}

//	public void sendBlockchain(String exactNode, int port) {
//		synchronized (this) {
//			if (exactNode.length() > 0) {
//				SendExecuter.SENDEXECUTER.submit(new THREADSendBlockchain(exactNode, port));
//			} else {
//				for (Node node : NodeRegister.getInstance().getNodes()) {
//					SendExecuter.SENDEXECUTER.submit(new THREADSendBlockchain(node.getIP(), node.getPort()));
//				}
//			}
//			notifyAll();
//		}
//	}

	public void sendTransaction(String exactNode, int port, Transaction tx) {
		synchronized (this) {
			if (exactNode.length() > 0) {
				SendExecuter.SENDEXECUTER.submit(new THREADSendTransaction(exactNode, port, tx));
			} else {
				for (Node node : NodeRegister.getInstance().getNodes()) {
					SendExecuter.SENDEXECUTER.submit(new THREADSendTransaction(node.getIP(), node.getPort(), tx));
				}
			}
			notifyAll();
		}
	}

	public void sendBlock(String exactNode, int port, Block block) {
		synchronized (this) {
			if (exactNode.length() > 0) {
//				SendExecuter.SENDEXECUTER.submit(new THREADSendBlock(exactNode, port, block));
				SendExecuter.SENDEXECUTER.schedule(new THREADSendBlock(exactNode, port, block),25, TimeUnit.MILLISECONDS);
			} else {
				for (Node node : NodeRegister.getInstance().getNodes()) {
					SendExecuter.SENDEXECUTER.submit(new THREADSendBlock(node.getIP(), node.getPort(), block));
				}
			}
			notifyAll();
		}
	}

	public void sendGetLatestBlockNoCommand(String exactNode, int port) {
		synchronized (this) {
			if (exactNode.length() > 0) {
				SendExecuter.SENDEXECUTER.submit(new THREADSendGetLatestBlockNo(exactNode, port));
			} else {
				THREADSendGetLatestBlockNo.cannotConnect = 0;
				GO.countOfGetLatestBlockNoPeers = 0; 
				for (Node node : NodeRegister.getInstance().getNodes()) {
					if(node.hostAvailabilityCheck()){
						GO.countOfGetLatestBlockNoPeers++;
						SendExecuter.SENDEXECUTER.submit(new THREADSendGetLatestBlockNo(node.getIP(), node.getPort()));
					}
				}
				if(GO.countOfGetLatestBlockNoPeers==0){
					GO.noPeersAvailable();
				}
			}
			notifyAll();
		}
	}

	public void sendLatestBlockNo(String exactNode, int port, int latestBlockNo) {
		synchronized (this) {
			if (exactNode.length() > 0) {
				SendExecuter.SENDEXECUTER.submit(new THREADSendLatestBlockNo(exactNode, port, latestBlockNo));
			} else {
				for (Node node : NodeRegister.getInstance().getNodes()) {
					SendExecuter.SENDEXECUTER.submit(new THREADSendLatestBlockNo(node.getIP(), node.getPort(), latestBlockNo));
				}
			}
			notifyAll();
		}
	}
	
	void sendGetNodes(String exactNode, int port) {
		synchronized (this) {
			for (Node node : NodeRegister.getInstance().getNodes()) {
				if (node.hostAvailabilityCheck()) {
					SendExecuter.SENDEXECUTER.submit(new THREADSendGetNodes(node.getIP(), node.getPort()));
				}
			}
			notifyAll();
		}
	}
	
	public void sendNodes(String exactNode, int port) {
		synchronized (this) {
			
			for (Node node : NodeRegister.getInstance().getNodes()) {
				if(node.hostAvailabilityCheck()){
					SendExecuter.SENDEXECUTER.submit(new THREADSendNodes(exactNode, port, node));
				}
			}
			
			notifyAll();
		}
	}

}
