package com.wastonix.server;

import com.wastonix.config.HibernateUtil;
import com.wastonix.model.User;
import com.wastonix.model.UserProfile;
import com.wastonix.service.AgriStockServiceImpl;
import com.wastonix.service.IAgriStockService;
import org.apache.activemq.broker.BrokerService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RmiServerBootstrap {

    
    private static final String ADMIN_EMAIL = "wastonorganisation@gmail.com";
    private static final String ADMIN_NAME  = "Waston Administrator";
    private static final String ADMIN_PHONE = "+250790734995";
    

    public static void main(String[] args) {
        try {
            System.setProperty("activemq.enabled", "true");
            System.setProperty("activemq.brokerUrl", "tcp://localhost:61616");
            startEmbeddedBroker();

            System.out.println("🚀 Starting AgriStock & AgriTrack Server...");

            LocateRegistry.createRegistry(5000);
            System.out.println("✅ RMI Registry created on port 5000");

            IAgriStockService service = new AgriStockServiceImpl();
            Naming.rebind("//localhost:5000/AgriStockService", service);
            System.out.println("✅ AgriStockService bound and ready.");

            seedAdminUser();

            System.out.println("📡 Waiting for requests...");

        } catch (Exception e) {
            System.err.println("❌ Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startEmbeddedBroker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("AgriStockBroker");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();
        System.out.println("✅ Embedded ActiveMQ broker started at tcp://localhost:61616");
    }

    private static void seedAdminUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            
            User existing = session.createQuery("FROM User WHERE email = :e", User.class)
                .setParameter("e", ADMIN_EMAIL).uniqueResult();

            if (existing != null) {
                System.out.println("👤 Admin user already exists: " + ADMIN_EMAIL);
                return;
            }

            Transaction tx = session.beginTransaction();

            User admin = new User();
            admin.setEmail(ADMIN_EMAIL);
            admin.setPhone(ADMIN_PHONE);
            admin.setFullName(ADMIN_NAME);
            admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
            admin.setActive(true);
            session.persist(admin);
            session.flush(); 

            UserProfile profile = new UserProfile();
            profile.setUser(admin);
            profile.setFullName(ADMIN_NAME);
            profile.setPhone(ADMIN_PHONE);
            profile.setRole("ADMIN");
            session.persist(profile);

            tx.commit();
            System.out.println("✅ Admin user seeded successfully!");
            System.out.println("   📧 Email : " + ADMIN_EMAIL);
            System.out.println("   🔑 Login : Request OTP on the login screen");

        } catch (Exception e) {
            System.err.println("⚠️  Admin seed failed: " + e.getMessage());
        }
    }
}