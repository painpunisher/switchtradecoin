package app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import app.gui.msg.WarningError;
import app.log.OUT;

public class KeyLoader {

	public static KeyPair LoadKeyPair(String privateKeyPath, String publicKeyPath, String algorithm,
			boolean loadInAppDir) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		if (loadInAppDir) {
			privateKeyPath = privateKeyPath + "private.key";
			publicKeyPath = publicKeyPath + "public.key";
		}

		// Read Public Key.
		File filePublicKey = null;
		try {
			filePublicKey = new File(publicKeyPath);
		} catch (Exception e) {
			WarningError.showError("Cannot load Public Key!");
			OUT.ERROR("", e);
		}
		FileInputStream fis = new FileInputStream(publicKeyPath);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		// Read Private Key.
		File filePrivateKey = null;
		try {
			filePrivateKey = new File(privateKeyPath);
		} catch (Exception e) {
			WarningError.showError("Cannot load Private Key!");
		}
		fis = new FileInputStream(privateKeyPath);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}
	
	public static KeyPair LoadKeyPairViaPrivateKey(String privateKeyPath, String algorithm,
			boolean loadInAppDir) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		if (loadInAppDir) {
			privateKeyPath = privateKeyPath + "private.key";
//			publicKeyPath = publicKeyPath + "public.key";
		}

		// Read Public Key.
//		File filePublicKey = null;
//		try {
//			filePublicKey = new File(publicKeyPath);
//		} catch (Exception e) {
//			WarningError.showError("Cannot load Public Key!");
//		}
		FileInputStream fis; //= new FileInputStream(publicKeyPath);
//		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
//		fis.read(encodedPublicKey);
//		fis.close();

		// Read Private Key.
		File filePrivateKey = null;
		try {
			filePrivateKey = new File(privateKeyPath);
		} catch (Exception e) {
			WarningError.showError("Cannot load Private Key!");
		}
		fis = new FileInputStream(privateKeyPath);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
//		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
//		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		
		PublicKey publicKey = null;
		try {
			publicKey = StringUtil.getPublicKeyFromPrivateKey(privateKey);
		} catch (NoSuchProviderException e) {
			OUT.ERROR("", e);
		}
		
		return new KeyPair(publicKey, privateKey);
	}

}
