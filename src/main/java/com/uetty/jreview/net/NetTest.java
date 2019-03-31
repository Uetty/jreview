package com.uetty.jreview.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetTest {

	public static void main(String[] args) throws UnknownHostException {
		testInetAddress();
	}
	
	public static void testInetAddress() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
		InetAddress byAddress = InetAddress.getByAddress(new byte[] {(byte) 192, (byte) 169, 1, 12});
		InetAddress byName = InetAddress.getByName("194.111.12.11");
		System.out.println(localHost);
		System.out.println(loopbackAddress);
		System.out.println(byAddress);
		System.out.println(byName);
		
	}
}
