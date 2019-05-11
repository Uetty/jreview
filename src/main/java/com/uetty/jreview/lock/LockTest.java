package com.uetty.jreview.lock;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

	public static void main(String[] args) {
		ReentrantLock lock = new ReentrantLock();
		
		ReentrantLock fairLock = new ReentrantLock(true);
		
	}
}
