package com.uetty.jreview.rabbit.queue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitQueueConsumer {

	private final static String QUEUE_NAME = "multiple";
	
	private static ConnectionFactory factory;
	private static Connection conn;
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	
	private static synchronized Channel createConnectChannel() {
		if (factory == null) {
			factory = new ConnectionFactory();
			factory.setHost("118.25.54.197");
			factory.setVirtualHost("test");
			factory.setUsername(System.getProperty("username"));
			factory.setPassword(System.getProperty("password"));
		}
		if (conn == null) {
			try {
				conn = factory.newConnection();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Channel channel = conn.createChannel();
			return channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("create failed");
	}
	
	private static void closeChannel(Channel channel) {
		if (channel == null || !channel.isOpen()) return;
		synchronized (channel) {
			if (channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static synchronized void closeConnection(Channel channel) {
		closeChannel(channel);
		if (conn != null && conn.isOpen()) {
			synchronized (conn) {
				if (conn != null && conn.isOpen()) {
					try {
						conn.close();
						conn = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static void doWork(String task) {
		for (char ch : task.toCharArray()) {
			if (ch == '.') {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			} 
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Channel channel = createConnectChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		
		Thread.sleep(4000l);
		
		channel.basicQos(1);
		CountDownLatch cdl = new CountDownLatch(1);
		
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            
            System.out.println(" [!] Consumer TAG '" + consumerTag + "'");
            long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            System.out.println(" [!] Delivery TAG '" + deliveryTag + "'");
            System.out.println(" [x] Received '" + message + "'");
            
            try {
            	doWork(message);
            	
            } catch (Exception e) {
            	System.out.println(" [x] Done");
            	channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            }
            
            boolean ack = Math.random() > 0.5;
            System.out.println(" [a] ACK '" + ack + "' " + sdf.format(new Date()));
            if (ack) {
            	channel.basicAck(deliveryTag, false);
            } else {
            	channel.basicNack(deliveryTag, false, true);
            }
            cdl.countDown();
        };
        
        
        System.out.println(" [*] Waiting for messages  " + sdf.format(new Date()));
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        	System.out.println(" [x] Cancel '" + consumerTag + "'");
        });
        cdl.await();
        
        closeConnection(channel);
        
	}
}
