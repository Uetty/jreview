package com.uetty.jreview.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTesst {

	public static class Data {
		public int round;
	}
	
	public static class Producer implements Runnable {
		
		ThreadPoolExecutor executor;
		List<Runnable> runables = new ArrayList<Runnable>();
		
		public Producer(ThreadPoolExecutor executor) {
			this.executor = executor;
		}
		
		@Override
		public void run() {
			if (runables.size() == 0) {
				for (int i = 0; i < 100; i++) {
					Comsumer comsumer = new Comsumer();
					Data d = new Data();
					d.round = (int)(Math.random() * 30) + 30;
					comsumer.setData(d);
					runables.add(comsumer);
				}
			}
			for (Runnable r : runables) executor.execute(r);
			executor.execute(this);
		}
	}
	
	public static class Comsumer implements Runnable {

		static ReentrantLock lock = new ReentrantLock(false);
		
		private Data data;
		
		public Data getData() {
			return data;
		}

		public void setData(Data rounds) {
			this.data = rounds;
		}

		public int revoke(int i) {
			
			lock.lock();
			String name = Thread.currentThread().getName();
			System.out.println("enter " + name + " revoke" + "-" + i);
			
			Integer rtn = null;
			if (i <= 0) rtn = 0;;
			if (i == 1) rtn = 1;
			try {
				Thread.sleep(64l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (rtn == null)
				i = i + revoke(i - 1);
			lock.unlock();
			if (rtn != null) return rtn; 
			return i;
		}
		
		@Override
		public void run() {
			revoke(data.round);
		}
		
	}
	
	static class MyThreadFactory implements ThreadFactory {
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private String NAME_PREFIX = "threadpool-thread";
		private ThreadGroup group;
		
		public MyThreadFactory() {
			SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
		}
		
		@Override
		public Thread newThread(Runnable r) {
			int i = threadNumber.getAndIncrement();
			System.out.println("new thread " + i);
			Thread thread = new Thread(r, NAME_PREFIX + i);
			if (thread.isDaemon())
				thread.setDaemon(false);
			if (thread.getPriority() != Thread.NORM_PRIORITY) 
				thread.setPriority(Thread.NORM_PRIORITY);
			return thread;
		}
	};
	
	public static void main(String[] args) {
		LinkedBlockingQueue<Runnable> blockQueue = new LinkedBlockingQueue<>(100_0000);
		
		MyThreadFactory tf = new MyThreadFactory();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 8, 20, TimeUnit.SECONDS, blockQueue, tf);
		Producer producer = new Producer(executor);
		executor.execute(producer);
	}
}
