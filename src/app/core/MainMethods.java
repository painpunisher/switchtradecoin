package app.core;

import java.math.BigInteger;
import java.security.PublicKey;

import app.database.DBAccount;
import app.util.StringUtil;

public class MainMethods {
	/**
	 * Returns Biginteger balance of the given publickey.
	 * 
	 * @param publickey
	 * @return biginteger balance
	 */
	public static BigInteger getBalance(String publickey) {
		return DBAccount.getAccountState(StringUtil.getKey(publickey)).balance;
	}
	
	/**
	 * Returns Biginteger balance of the given publickey.
	 * 
	 * @param publickey
	 * @return biginteger balance
	 */
	public static BigInteger getBalance(PublicKey publickey) {
		return DBAccount.getAccountState(publickey).balance;
	}
	

}
