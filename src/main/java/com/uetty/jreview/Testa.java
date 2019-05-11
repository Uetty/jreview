package com.uetty.jreview;

public class Testa {

	
	static {
		System.out.println("static block a");
	}
	
	{
		System.out.println("block a");
	}
	
	public Testa() {
		System.out.println("a");
	}
}
