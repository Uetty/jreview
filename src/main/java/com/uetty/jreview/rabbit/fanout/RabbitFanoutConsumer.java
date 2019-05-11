package com.uetty.jreview.rabbit.fanout;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitFanoutConsumer {
	private final static String EXCHANGE_NAME = "fanout";
	private final static String ROUTING_KEY = "";
	
	private static ConnectionFactory factory;
	private static Connection conn;
	
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
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Channel channel = createConnectChannel();
		
		
		Scanner sc = new Scanner(System.in);
		System.out.println(" [!] Enter Queue Name");
		String queueName = sc.nextLine();
		sc.close();
		
		channel.queueDeclare(queueName, false, false, false, null);
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
		channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
		
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		CountDownLatch cdl = new CountDownLatch(3);
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            
            System.out.println(" [x] Received '" + message + "'");
            cdl.countDown();
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        
        cdl.await();
        closeConnection(channel);
	}
}
