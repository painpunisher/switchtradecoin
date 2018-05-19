package app.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import app.log.OUT;

public class NetUtil {
	public static List<String> getHostAddresses() {
		LinkedList<String> addresses = new LinkedList<>();
		
		
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<InetAddress> ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
//			        InetAddress i = (InetAddress) ee.nextElement();
			        addresses.add(e.toString());
			    }
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return addresses;
	}
	
	
	public static boolean isReachableByPing() {
		String host = "www.google.com";
	     try{
	                String cmd = "";
	                if(System.getProperty("os.name").startsWith("Windows")) {   
	                        // For Windows
	                        cmd = "ping -n 1 " + host;
	                } else {
	                        // For Linux and OSX
	                        cmd = "ping -c 1 " + host;
	                }

	                Process myProcess = Runtime.getRuntime().exec(cmd);
	                myProcess.waitFor();

	                if(myProcess.exitValue() == 0) {

	                        return true;
	                } else {

	                        return false;
	                }

	        } catch( Exception e ) {

	                OUT.ERROR("", e);
	                return false;
	        }
	}
	
}
