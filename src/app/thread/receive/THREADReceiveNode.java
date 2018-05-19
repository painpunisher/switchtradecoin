package app.thread.receive;

import app.config.Config;
import app.log.OUT;
import app.net.Node;
import app.net.NodeDiscovery;

public class THREADReceiveNode implements Runnable {

	private Node mNode;

	public THREADReceiveNode(Object obj) {
		mNode = (Node) obj;
	}

	@Override
	public void run() {
		addNewArrivedNode();
	}

	private void addNewArrivedNode() {
		OUT.DEBUG("NODE RECEIVED: " + mNode.getIP() + ":" + mNode.getPort());

		if (NodeDiscovery.nodeExists(mNode)) {
			OUT.DEBUG("RECEIVED NODE EXISTS! DISCARDED!");
			return;
		}

		if (NodeDiscovery.addNodeToFreeSpace(mNode)) {
			OUT.DEBUG("ADDING NODE TO NODELIST");
		} else {
			OUT.DEBUG("ADDING NODE TO NODELIST");
		}

		Config.saveProperties();
	}
}
