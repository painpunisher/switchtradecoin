package app.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import app.config.Cons;
import app.gui.msg.WarningError;
import app.log.OUT;
import app.model.transaction.Transaction;

public class StringUtil {
	// Applies Sha256 to a string and returns the result.
	public static String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// Applies sha256 to our input,
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer(); // This will contain
															// hash as
															// hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	// Verifies a String signature
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getStringFromKey(Key key) {
		return Base58.encode(key.getEncoded());
	}

	public static PublicKey getKey(String key) {
		try {
			byte[] byteKey = Base58.decode(key);
			X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
			KeyFactory kf = KeyFactory.getInstance(Cons.ALGORITHM);

			return kf.generatePublic(X509publicKey);
		} catch (Exception e) {
			WarningError.showError("Cannot initlize Public Key <" + key + "> for Receiver!");
		}

		return null;
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKeyFromPrivateKey(PrivateKey privateKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");

		ECPoint Q = ecSpec.getG().multiply(((ECPrivateKey) privateKey).getD());
		byte[] publicDerBytes = Q.getEncoded(false);

		ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
		ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
		ECPublicKey publicKeyGenerated = (ECPublicKey) keyFactory.generatePublic(pubSpec);
		return publicKeyGenerated;
	}

	// Tacks in array of transactions and returns a merkle root.
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();

		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		ArrayList<String> treeLayer = previousTreeLayer;

		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i += 2) {
				treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

	public static String getDificultyString(int difficulty) {
		String str = "";
		for (int i = 0; i < difficulty; i++) {
			str = str + "0";
		}
		return str;
	}

	public static PrivateKey getPrivateKeyFromByte(byte[] data) {
		try {

			byte[] encodedPrivateKey = new byte[(int)data.length];
			encodedPrivateKey = data.clone();
			KeyFactory keyFactory = KeyFactory.getInstance(Cons.ALGORITHM);

			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			return privateKey;// new KeyPair(publicKey, privateKey);
		} catch (Exception e) {
			OUT.ERROR("", e);
		}
		return null;
	}
}
