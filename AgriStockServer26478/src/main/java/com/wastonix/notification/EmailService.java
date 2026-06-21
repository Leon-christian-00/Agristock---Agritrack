package com.wastonix.notification;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    
    private static final String SMTP_HOST    = "smtp.gmail.com";
    private static final int    SMTP_PORT    = 587;
    private static final String SENDER_EMAIL = "abumuchristian@gmail.com";
    private static final String SENDER_NAME  = "AgriStock & AgriTrack";
    private static final String SMTP_LOGIN   = "abumuchristian@gmail.com";
    private static final String SMTP_KEY     = "kfxf tvzc itfn ivrj";
    

    private static Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_LOGIN, SMTP_KEY);
            }
        });
    }

    public static void sendOTP(String toEmail, String otp) {
        String subject = "🌾 AgriStock & AgriTrack — Your Login OTP";
        String body = """
            <div style="font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;border:1px solid #d4edda;border-radius:12px;overflow:hidden">
              <div style="background:#22751c;padding:24px;text-align:center">
                <h2 style="color:#fff;margin:0">🌾 AgriStock & AgriTrack</h2>
                <p style="color:#b8f0b8;margin:4px 0 0">Agricultural Stock Management System</p>
              </div>
              <div style="padding:32px">
                <p style="color:#333;font-size:15px">Your one-time login code is:</p>
                <div style="background:#f0faf0;border:2px dashed #22751c;border-radius:10px;padding:20px;text-align:center;margin:20px 0">
                  <span style="font-size:36px;font-weight:bold;letter-spacing:10px;color:#22751c">""" + otp + """
                  </span>
                </div>
                <p style="color:#666;font-size:13px">⏱ This code expires in <strong>5 minutes</strong>.</p>
                <p style="color:#666;font-size:13px">If you did not request this, please ignore this email.</p>
              </div>
              <div style="background:#f5faf5;padding:14px;text-align:center;border-top:1px solid #d4edda">
                <p style="color:#999;font-size:11px;margin:0">AgriStock RW &bull; Secure Agricultural Management</p>
              </div>
            </div>
            """;
        send(toEmail, subject, body);
    }

    public static void sendApprovalNotification(String toEmail, String fullName) {
        String subject = "✅ AgriStock & AgriTrack — Account Approved!";
        String body = """
            <div style="font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;border:1px solid #d4edda;border-radius:12px;overflow:hidden">
              <div style="background:#22751c;padding:24px;text-align:center">
                <h2 style="color:#fff;margin:0">🌾 AgriStock & AgriTrack</h2>
              </div>
              <div style="padding:32px">
                <h3 style="color:#22751c">Welcome, """ + fullName + """
                !</h3>
                <p style="color:#333">Your account has been <strong style="color:#22751c">approved</strong> by the administrator.</p>
                <p style="color:#333">You can now log in to AgriStock & AgriTrack using your registered email address.</p>
              </div>
              <div style="background:#f5faf5;padding:14px;text-align:center;border-top:1px solid #d4edda">
                <p style="color:#999;font-size:11px;margin:0">AgriStock & AgriTrack &bull; Secure Agricultural Management</p>
              </div>
            </div>
            """;
        send(toEmail, subject, body);
    }

    public static void sendRejectionNotification(String toEmail, String fullName) {
        String subject = "❌ AgriStock & AgriTrack — Registration Update";
        String body = """
            <div style="font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;border:1px solid #f5c6cb;border-radius:12px;overflow:hidden">
              <div style="background:#c82333;padding:24px;text-align:center">
                <h2 style="color:#fff;margin:0">🌾 AgriStock & AgriTrack</h2>
              </div>
              <div style="padding:32px">
                <p style="color:#333">Dear """ + fullName + """
                ,</p>
                <p style="color:#333">Unfortunately, your registration request has not been approved at this time.</p>
                <p style="color:#333">Please contact your cooperative administrator for more information.</p>
              </div>
            </div>
            """;
        send(toEmail, subject, body);
    }

    public static void sendAdminNewRegistrationAlert(String adminEmail, String newUserName, String newUserEmail) {
        String subject = "🔔 AgriStock & AgriTrack — New Registration Pending Approval";
        String body = """
            <div style="font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;border:1px solid #d4edda;border-radius:12px;overflow:hidden">
              <div style="background:#22751c;padding:24px;text-align:center">
                <h2 style="color:#fff;margin:0">🌾 AgriStock & AgriTrack — Admin Alert</h2>
              </div>
              <div style="padding:32px">
                <p style="color:#333">A new user has registered and is awaiting your approval:</p>
                <table style="width:100%;border-collapse:collapse;margin:16px 0">
                  <tr><td style="padding:8px;color:#666;font-weight:bold">Name:</td><td style="padding:8px;color:#333">""" + newUserName + """
                  </td></tr>
                  <tr style="background:#f5faf5"><td style="padding:8px;color:#666;font-weight:bold">Email:</td><td style="padding:8px;color:#333">""" + newUserEmail + """
                  </td></tr>
                </table>
                <p style="color:#333">Please log in to AgriStock & AgriTrack to approve or reject this registration.</p>
              </div>
            </div>
            """;
        send(adminEmail, subject, body);
    }

    private static void send(String to, String subject, String htmlBody) {
        try {
            Message msg = new MimeMessage(buildSession());
            msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(msg);
            System.out.println("📧 Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("⚠️ Email send failed to " + to + ": " + e.getMessage());
        }
    }
}
