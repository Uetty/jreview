package com.uetty.jreview;

public class Testb extends Testa {

	static {
		System.out.println("static block b");
	}
	
	{
		System.out.println("block b");
	}
	
	public Testb() {
		System.out.println("b");
	}
}
