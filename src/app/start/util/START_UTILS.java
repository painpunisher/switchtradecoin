package app.start.util;

import java.security.Security;

import app.log.OUT;

class START_UTILS {

	static void initBouncyCastle() {
		OUT.println("###INIT BOUNCY CASTLE###");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

}
