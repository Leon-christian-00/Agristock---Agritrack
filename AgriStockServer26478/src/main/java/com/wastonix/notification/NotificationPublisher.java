package com.wastonix.notification;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class NotificationPublisher {
    
    private static final boolean ACTIVEMQ_ENABLED = Boolean.parseBoolean(System.getProperty("activemq.enabled", "false"));
    private static final String BROKER_URL = System.getProperty("activemq.brokerUrl", "tcp://localhost:61616");

    public void publish(String eventType, String message) {
        if (!ACTIVEMQ_ENABLED) {
            
            System.out.println("📢 Notification (local): [" + eventType + "] " + message);
            return;
        }

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection conn = factory.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = session.createQueue("agristock.notifications");
            MessageProducer producer = session.createProducer(dest);
            TextMessage msg = session.createTextMessage("[" + eventType + "] " + message);
            producer.send(msg);
            System.out.println("📢 Notification Sent: " + msg.getText());
            session.close(); conn.close();
        } catch (Exception e) {
            System.err.println("⚠️ ActiveMQ Error: " + e.getMessage());
        }
    }
}