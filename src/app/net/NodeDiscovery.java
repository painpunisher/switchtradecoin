package app.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import app.config.Config;
import app.config.FilePathCollection;
import app.log.OUT;
import app.start.util.GO;

/**
 * Node Discovery class and other methods.
 *
 */
public class NodeDiscovery {
	private static int limitReadingTimesDeadNodes = 5;

	/**
	 * Starting node discovery process.
	 */
	public static void startRefreshNodes() {
		checkCurrentConfigNodesForDuplicate();

		LinkedList<Node> checkLimitReading = readDeadNodesFile(true);
		checkDeadNodesReadedMoreThenHaveTo(checkLimitReading);

		// if(getAmountFreeNodeConfig()==0){//check if free spaces in config.
		// this lines adds deadnodes if exist
		checkForDeadNodes(); // check if current nodeslist contains dead nodes.
		// }
		LinkedList<Node> deadNodes = readDeadNodesFile(false);

		if (getAmountFreeNodeConfig() > 0) {// check if free spaces in config.
			// this line removes deadnodes if they are active
			boolean success = checkIfDeadNodesActive(deadNodes);
			if(success){
				deadNodes = readDeadNodesFile(false);
			}
		}

		writeDeadNodesFile(deadNodes);

		if (getAmountFreeNodeConfig() > 0) {// check if free spaces in config.
			GO.sender.sendGetNodes("", 0); // add from network
		}
		Config.saveProperties();
		Config.loadProperties();
	}

	private static boolean checkDeadNodesReadedMoreThenHaveTo(LinkedList<Node> dn) {
		boolean deleted = false;
		boolean real = false;
		for (Node node : dn) {
			if (node.getReadedFromDeadNodes() > limitReadingTimesDeadNodes) {
				deleted = deleteNodeFromDeadNodeFile(node, false);
				if (deleted) {
					real = true;
				}
			}
		}
		return real;
	}

	private static boolean checkCurrentConfigNodesForDuplicate() {
		boolean success = true;
		for (int x = 1; x <= 8; x++) {
			Node nodeX = new Node(Config.INSTANCE.getProperty("peer" + x),
					Integer.parseInt(Config.INSTANCE.getProperty("port" + x)));
			int existsCount = 0;
			for (int y = 1; y <= 8; y++) {
				Node nodeY = new Node(Config.INSTANCE.getProperty("peer" + y),
						Integer.parseInt(Config.INSTANCE.getProperty("port" + y)));

				if (nodeX.isSameAs(nodeY)) {
					existsCount++;
					if (existsCount > 1) {
						Config.INSTANCE.setProperty("peer" + y, "0");
						Config.INSTANCE.setProperty("port" + y, "0");
						existsCount--;
					}
				}

			}
		}
		Config.saveProperties();
		return success;
	}

	/**
	 * Checks if node exists.
	 * 
	 * @param exists
	 * @return
	 */
	public static boolean nodeExists(Node exists) {
		for (Node node : NodeRegister.getInstance().getNodes()) {
			if (node.isSameAs(exists)) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkForDeadNodes() {
		boolean success = false;
		for (int i = 1; i <= 8; i++) {
			Node tryNode = new Node(Config.INSTANCE.getProperty("peer" + i),
					Integer.parseInt(Config.INSTANCE.getProperty("port" + i)));
			if (tryNode.getIP().length() > 7) {
				if (!tryNode.hostAvailabilityCheck()) {
					success = writeDeadNodesInSeperateFile(tryNode, i);
				}
			}
		}
		return success;
	}

	private static int getFreeNodeConfig() {
		for (int i = 1; i <= 8; i++) {
			Node tryNode = new Node(Config.INSTANCE.getProperty("peer" + i),
					Integer.parseInt(Config.INSTANCE.getProperty("port" + i)));
			if (tryNode.getIP().equals("0")) {
				return i;
			}
		}
		return 0;
	}

	private static int getAmountFreeNodeConfig() {
		int count = 0;
		for (int i = 1; i <= 8; i++) {
			Node tryNode = new Node(Config.INSTANCE.getProperty("peer" + i),
					Integer.parseInt(Config.INSTANCE.getProperty("port" + i)));
			if (tryNode.getIP().equals("0")) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks if free space for node available. Yes adds the node - No return
	 * false.
	 * 
	 * @param n
	 * @return
	 */
	public static boolean addNodeToFreeSpace(Node n) {
		OUT.DEBUG("BEFORE GETFREENODECONFIG");
		int freeSpaceConfig = getFreeNodeConfig();
		OUT.DEBUG("AFTER GETFREENODECONFIG");
		if (freeSpaceConfig > 0) {
			OUT.DEBUG("ADDING REAL NODE BEFORE");
			Config.INSTANCE.setProperty("peer" + freeSpaceConfig, n.getIP());
			Config.INSTANCE.setProperty("port" + freeSpaceConfig, n.getPort() + "");
			OUT.DEBUG("ADDING REAL NODE AFTER");
			return true;
		}
		return false;
	}

	private static boolean writeDeadNodesInSeperateFile(Node n, int deleteNodeNo) {
		if (writeDeadNodeFile(n)) {
			Config.INSTANCE.setProperty("peer" + deleteNodeNo, "0");
			Config.INSTANCE.setProperty("port" + deleteNodeNo, "0");
			return true;
		}
		return false;
	}

	private static LinkedList<Node> readDeadNodesFile(boolean count) {
		LinkedList<Node> deadNodes = new LinkedList<Node>();
		File f = new File(FilePathCollection.NODE_DISCOVERY_FILE);
		// do something
		try {
			if (f.exists() && !f.isDirectory()) {
				File file = new File(FilePathCollection.NODE_DISCOVERY_FILE);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				StringBuffer stringBuffer = new StringBuffer();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					String[] node = line.split("#");
					if (count) {
						deadNodes.add(new Node(node[0], Integer.parseInt(node[1]), Integer.parseInt(node[2]) + 1));
					} else {
						deadNodes.add(new Node(node[0], Integer.parseInt(node[1]), Integer.parseInt(node[2])));
					}
					stringBuffer.append(line);
					stringBuffer.append("\n");
				}
				fileReader.close();
				OUT.DEBUG("Contents of file:");
				OUT.DEBUG(stringBuffer.toString());
			}
		} catch (IOException e) {
			OUT.ERROR("", e);
			return null;
		}
		return deadNodes;
	}

	private static boolean writeDeadNodesFile(LinkedList<Node> dn) {
		for (Node node : dn) {
			writeDeadNodeFile(node);
		}
		return true;
	}

	private static boolean writeDeadNodeFile(Node n) {
		LinkedList<Node> deadnodes = readDeadNodesFile(false);
		for (Node dn : deadnodes) {// avoid duplicates here.
			if (dn.isSameAs(n)) {
				// node already exists in deadnodesfile return.
				deleteNodeFromDeadNodeFile(dn, false);
				return true;
			}
		}
		try {
			(new File(FilePathCollection.NODE_DISCOVERY_PATH)).mkdirs();
			File fout = new File(FilePathCollection.NODE_DISCOVERY_FILE);
			FileOutputStream fos = new FileOutputStream(fout, true);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write("" + n.getIP() + "#" + n.getPort() + "#" + n.getReadedFromDeadNodes());
			bw.newLine();

			bw.close();
			fos.close();
		} catch (IOException e) {
			OUT.ERROR("Writing Dead Node Failed: " + n.getIP() + ":" + n.getPort(), e);
			OUT.ERROR("", e);
			return false;
		}
		return true;
	}

	private static boolean deleteNodeFromDeadNodeFile(final Node n, final boolean withoutCheck) {
		File inputFile = null;
		File tempFile = null;
		try {

			inputFile = new File(FilePathCollection.NODE_DISCOVERY_FILE);
			tempFile = new File(FilePathCollection.NODE_DISCOVERY_FILE + "_new");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String lineToRemove = n.getIP() + "#" + n.getPort() + "#" + n.getReadedFromDeadNodes();
			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();

				String[] nodestr = trimmedLine.split("#");
				Node fromFile = new Node(nodestr[0], Integer.parseInt(nodestr[1]), Integer.parseInt(nodestr[2]));
				if (withoutCheck) {
					if (trimmedLine.equals(lineToRemove))
						continue;
				} else {
					if (trimmedLine.equals(lineToRemove)
							&& fromFile.getReadedFromDeadNodes() > limitReadingTimesDeadNodes)
						continue;
				}

				String[] writeNodeInNewFile = trimmedLine.split("#");
				Node nn = new Node(writeNodeInNewFile[0], Integer.parseInt(writeNodeInNewFile[1]),
						Integer.parseInt(writeNodeInNewFile[2]) + 1);
				writer.write(nn.getIP() + "#" + nn.getPort() + "#" + nn.getReadedFromDeadNodes()
						+ System.getProperty("line.separator"));
			}
			writer.close();
			reader.close();
		} catch (Exception e) {
			OUT.ERROR("", e);
		}
		// inputFile.delete();

		inputFile.renameTo(new File(FilePathCollection.NODE_DISCOVERY_PATH + "old"));
		boolean successful = tempFile.renameTo(new File(FilePathCollection.NODE_DISCOVERY_FILE));

		inputFile = new File(FilePathCollection.NODE_DISCOVERY_PATH + "old");
		// delete files section
		inputFile.delete();

		return successful;
	}

	private static boolean checkIfDeadNodesActive(LinkedList<Node> deadNodes) {
		boolean active = false;
		for (Node n : deadNodes) {
			if (n.hostAvailabilityCheck()) {
				addNodeToFreeSpace(n);
				deleteNodeFromDeadNodeFile(n, true);
				active = true;
			}
		}
		return active;
	}
}
