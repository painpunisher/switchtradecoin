package testchain2;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.junit.Test;

import app.config.Config;
import app.database.DBAccount_Valid;
import app.database.DBEnum;
import app.log.OUT;
import app.model.account.AccountState;

public class DBConnectionTesting {

	@Test
	public void accvalid(){
		Config.init();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup
		// Bouncey
		// castle
		// as
		// a
		// Security
		// Provider
		KeyPair kp1 = null;
		KeyPair kp2 = null;
		try {
			kp1 = app.util.KeyGenerator.getNewGeneratedKeyPair();
			kp2 = app.util.KeyGenerator.getNewGeneratedKeyPair();
			System.out.println(kp2);
		} catch (NoSuchAlgorithmException e) {
			OUT.ERROR("", e);
		} catch (NoSuchProviderException e) {
			OUT.ERROR("", e);
		} catch (InvalidAlgorithmParameterException e) {
			OUT.ERROR("", e);
		}
		
		
		
		
		AccountState acc = new AccountState();
		acc.balance = new BigInteger("500");
		acc.nonce = new BigInteger("12");
		try {
			DBAccount_Valid.destroy(DBEnum.ACCOUNT_VALID);
//			DBAccount_Valid.insertAccount(kp1.getPublic(), acc);
//			DBAccount_Valid.destroy(DBEnum.ACCOUNT_VALID);
//			DBAccount_Valid.insertAccount(kp1.getPublic(), acc);
//			DBAccount_Valid.destroy(DBEnum.ACCOUNT_VALID);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		AccountState nu = DBAccount_Valid.getAccountState(kp1.getPublic());
		
		
		assertTrue(acc.equals(nu));
		
	}
	
	
}
