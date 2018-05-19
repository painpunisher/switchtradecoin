package app.core.block;

import app.core.mining.MiningUnit;
import app.log.OUT;
import app.start.util.GO;
import app.util.StringUtil;

public class BLOCKMINING extends Thread {
	public static String hash;
	private static String previousHash;
	private static String merkleRoot;
	private static long timeStamp; // as number of milliseconds since 1/1/1970.
	public static int nonce;
	private static int difficulty;
	private static String target;

	public BLOCKMINING(String phash, String pprevHash, String pmerkleRoot, long ptimestamp, int pdifficutly,
			String ptarget) {
		hash = phash;
		previousHash = pprevHash;
		merkleRoot = pmerkleRoot;
		timeStamp = ptimestamp;
		difficulty = pdifficutly;
		target = ptarget;
	}

	@Override
	public void run() {
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
			if (GO.STOPMINING) {
				System.err.println("MINING STOPPED BECAUSE OF NEW BLOCK ARRIVED!");
				break;
			}
		}
		if (!GO.STOPMINING) {
			OUT.DEBUG("FOUND HASH: " + hash);
			OUT.DEBUG("Block Mined!!! HASH: " + hash);
			OUT.DEBUG("Block Mined!!! PREV.HASH: " + previousHash);
			MiningUnit.minedSuccessfuly = true;
		}
	}

	// Calculate new hash based on blocks contents
	private static String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}

}
