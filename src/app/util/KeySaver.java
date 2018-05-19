package app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import app.config.FilePathCollection;

public class KeySaver {

    public static void SaveKeyPair(String path, KeyPair keyPair, boolean saveInAppDir) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
 
        String targetPath = path + File.separator;
        if(saveInAppDir){
        	targetPath = FilePathCollection.DATA_PATH;
        } 
        
        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(targetPath + "public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();
 
        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(targetPath +"private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }
	
}
