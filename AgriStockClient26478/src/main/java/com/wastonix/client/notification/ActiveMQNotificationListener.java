package com.wastonix.client.notification;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class ActiveMQNotificationListener {
    private static final String BROKER_URL = System.getProperty("activemq.brokerUrl", "tcp://localhost:61616");
    private static final String QUEUE_NAME = "agristock.notifications";
    private volatile boolean running = false;

    public void start() {
        if (running) return;
        running = true;
        Thread thread = new Thread(this::listenLoop, "ActiveMQ-Notification-Listener");
        thread.setDaemon(true);
        thread.start();
    }

    private void listenLoop() {
        while (running) {
            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
                try (Connection connection = connectionFactory.createConnection()) {
                    connection.start();
                    try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                        Destination destination = session.createQueue(QUEUE_NAME);
                        try (MessageConsumer consumer = session.createConsumer(destination)) {
                            while (running) {
                                Message message = consumer.receive(1000);
                                if (message instanceof TextMessage textMsg) {
                                    String text = textMsg.getText();
                                    System.out.println("📬 ActiveMQ notification received: " + text);
                                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, text, "ActiveMQ Notification", JOptionPane.INFORMATION_MESSAGE));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ ActiveMQ listener error: " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void stop() {
        running = false;
    }
}
