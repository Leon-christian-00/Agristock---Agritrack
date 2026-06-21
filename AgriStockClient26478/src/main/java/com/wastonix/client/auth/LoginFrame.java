package com.wastonix.client.auth;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.client.dashboard.MainDashboard;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    private IAgriStockService service;
    private JTextField    loginEmailField;
    private JPasswordField otpField;
    private JLabel        loginStatusLabel;
    private JButton       requestOtpBtn, verifyBtn;
    private JTextField    regNameField, regEmailField, regPhoneField;
    private JLabel        regStatusLabel;
    private JButton       loginTabBtn, registerTabBtn;
    private JPanel        cardContainer;
    private CardLayout    cardLayout;

    public LoginFrame() {
        super("AgriStock & AgriTrack");
        UITheme.applyGlobalDefaults();
        this.service = RmiClientUtil.getService();
        buildUI();
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        root.add(buildHero());
        root.add(buildFormSide());

        setContentPane(root);
    }

    
    private JPanel buildHero() {
        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                
                g2.setPaint(new GradientPaint(0, 0, UITheme.GREEN_900, getWidth(), getHeight(), new Color(5, 46, 22)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(getWidth() - 260, -100, 400, 400);

                
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(-120, getHeight() - 280, 380, 380);

                
                g2.setColor(UITheme.GREEN_600);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2.fillOval(getWidth() / 2 - 60, getHeight() / 2 - 60, 120, 120);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                
                g2.setColor(new Color(255, 255, 255, 15));
                for (int x = 40; x < getWidth(); x += 40)
                    for (int y = 40; y < getHeight(); y += 40)
                        g2.fillOval(x - 1, y - 1, 3, 3);

                g2.dispose();
            }
        };

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));


        JPanel logoMark = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.GREEN_600);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        logoMark.setOpaque(false);
        logoMark.setPreferredSize(new Dimension(72, 72));
        logoMark.setMaximumSize(new Dimension(72, 72));
        logoMark.setAlignmentX(CENTER_ALIGNMENT);
        JLabel logoText = new JLabel("AR", JLabel.CENTER);
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoText.setForeground(UITheme.WHITE);
        logoMark.add(logoText);

        JLabel appName = new JLabel("AgriStock & AgriTrack", JLabel.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appName.setForeground(UITheme.WHITE);
        appName.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Agricultural Stock & Tracking Management", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(167, 243, 208));
        tagline.setAlignmentX(CENTER_ALIGNMENT);

        
        JPanel divLine = new JPanel();
        divLine.setOpaque(false);
        divLine.setMaximumSize(new Dimension(60, 3));
        divLine.setPreferredSize(new Dimension(60, 3));
        divLine.setAlignmentX(CENTER_ALIGNMENT);
        divLine.setBorder(new MatteBorder(0, 0, 3, 0, UITheme.GREEN_400));


        String[][] features = {
            {"Farmer Management",   "Register and manage cooperative farmers"},
            {"Harvest Logging",     "Track crop yields and quality grades"},
            {"Sales & Revenue",     "Record sales and calculate earnings"},
            {"Reports & Export",    "Generate PDF and CSV analytics"}
        };

        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setOpaque(false);
        featurePanel.setAlignmentX(CENTER_ALIGNMENT);

        for (String[] feat : features) {
            JPanel row = new JPanel(new BorderLayout(14, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(360, 52));
            row.setBorder(new EmptyBorder(6, 0, 6, 0));

            
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.GREEN_400);
                    g2.fillOval(0, 4, 10, 10);
                    g2.dispose();
                }
            };
            dot.setOpaque(false); dot.setPreferredSize(new Dimension(10, 18));

            JPanel texts = new JPanel(new GridLayout(2, 1, 0, 1));
            texts.setOpaque(false);
            JLabel title = new JLabel(feat[0]);
            title.setFont(new Font("Segoe UI", Font.BOLD, 13));
            title.setForeground(UITheme.WHITE);
            JLabel desc = new JLabel(feat[1]);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            desc.setForeground(new Color(134, 239, 172));
            texts.add(title); texts.add(desc);

            row.add(dot, BorderLayout.WEST);
            row.add(texts, BorderLayout.CENTER);
            featurePanel.add(row);
        }

        JLabel version = new JLabel("v1.0  |  Secure RMI Architecture  |  Java 26", JLabel.CENTER);
        version.setFont(UITheme.FONT_CAPTION);
        version.setForeground(new Color(100, 180, 130));
        version.setAlignmentX(CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(logoMark);
        content.add(Box.createVerticalStrut(20));
        content.add(appName);
        content.add(Box.createVerticalStrut(8));
        content.add(tagline);
        content.add(Box.createVerticalStrut(20));
        content.add(divLine);
        content.add(Box.createVerticalStrut(32));
        content.add(featurePanel);
        content.add(Box.createVerticalStrut(40));
        content.add(version);
        content.add(Box.createVerticalGlue());

        hero.add(content);
        return hero;
    }

    
    private JPanel buildFormSide() {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(UITheme.SLATE_50);

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(44, 48, 44, 48)));
        card.setPreferredSize(new Dimension(480, 640));

        
        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(UITheme.SLATE_900);

        JLabel sub = new JLabel("Sign in to your account or create a new one");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        headerPanel.setOpaque(false);
        headerPanel.add(welcome);
        headerPanel.add(sub);

        
        loginTabBtn    = tabBtn("Login",    true);
        registerTabBtn = tabBtn("Register", false);
        loginTabBtn.addActionListener(e -> switchTab("LOGIN"));
        registerTabBtn.addActionListener(e -> switchTab("REGISTER"));

        JPanel tabBar = new JPanel(new GridLayout(1, 2, 0, 0));
        tabBar.setOpaque(false);
        tabBar.setBorder(new EmptyBorder(24, 0, 0, 0));
        tabBar.add(loginTabBtn);
        tabBar.add(registerTabBtn);

        
        JPanel tabIndicator = new JPanel(new GridLayout(1, 2, 0, 0));
        tabIndicator.setOpaque(false);
        tabIndicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        tabIndicator.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel activeUnderline = new JPanel();
        activeUnderline.setBackground(UITheme.PRIMARY);
        activeUnderline.setPreferredSize(new Dimension(0, 3));
        JPanel inactiveUnderline = new JPanel();
        inactiveUnderline.setBackground(UITheme.BORDER_COLOR);
        inactiveUnderline.setPreferredSize(new Dimension(0, 3));
        tabIndicator.add(activeUnderline);
        tabIndicator.add(inactiveUnderline);

        JPanel tabSection = new JPanel(new BorderLayout());
        tabSection.setOpaque(false);
        tabSection.add(tabBar, BorderLayout.CENTER);
        tabSection.add(tabIndicator, BorderLayout.SOUTH);

        
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);
        cardContainer.add(buildLoginCard(), "LOGIN");
        cardContainer.add(buildRegisterCard(), "REGISTER");

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(headerPanel, BorderLayout.NORTH);
        top.add(tabSection, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        card.add(cardContainer, BorderLayout.CENTER);

        side.add(card);
        return side;
    }

    private JButton tabBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(active ? UITheme.FONT_BOLD : UITheme.FONT_BODY);
        b.setForeground(active ? UITheme.PRIMARY_DARK : UITheme.TEXT_MUTED);
        b.setBackground(UITheme.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setContentAreaFilled(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 0, 10, 0));
        return b;
    }

    private void switchTab(String tab) {
        cardLayout.show(cardContainer, tab);
        boolean isLogin = tab.equals("LOGIN");
        loginTabBtn.setFont(isLogin ? UITheme.FONT_BOLD : UITheme.FONT_BODY);
        loginTabBtn.setForeground(isLogin ? UITheme.PRIMARY_DARK : UITheme.TEXT_MUTED);
        registerTabBtn.setFont(!isLogin ? UITheme.FONT_BOLD : UITheme.FONT_BODY);
        registerTabBtn.setForeground(!isLogin ? UITheme.PRIMARY_DARK : UITheme.TEXT_MUTED);
    }

    
    private JPanel buildLoginCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(24, 0, 0, 0));

        loginEmailField = UITheme.field();
        loginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginEmailField.setAlignmentX(LEFT_ALIGNMENT);
        loginEmailField.putClientProperty("JTextField.placeholderText", "you@example.com");

        otpField = UITheme.passwordField();
        otpField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        otpField.setAlignmentX(LEFT_ALIGNMENT);

        loginStatusLabel = new JLabel(" ", JLabel.CENTER);
        loginStatusLabel.setFont(UITheme.FONT_SMALL);
        loginStatusLabel.setForeground(UITheme.TEXT_MUTED);
        loginStatusLabel.setAlignmentX(CENTER_ALIGNMENT);
        loginStatusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        requestOtpBtn = UITheme.secondaryBtn("Send OTP to Email");
        verifyBtn     = UITheme.primaryBtn("Verify & Sign In");
        requestOtpBtn.setAlignmentX(CENTER_ALIGNMENT);
        verifyBtn.setAlignmentX(CENTER_ALIGNMENT);
        requestOtpBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        verifyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        requestOtpBtn.addActionListener(e -> requestOTP());
        verifyBtn.addActionListener(e -> verifyOTP());
        loginEmailField.addKeyListener(enter(this::requestOTP));
        otpField.addKeyListener(enter(this::verifyOTP));

        p.add(fieldGroup("Email address", loginEmailField));
        p.add(Box.createVerticalStrut(16));
        p.add(fieldGroup("One-time password", otpField));
        p.add(Box.createVerticalStrut(12));
        p.add(loginStatusLabel);
        p.add(Box.createVerticalStrut(20));
        p.add(requestOtpBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(verifyBtn);

        
        JLabel note = new JLabel("<html><center>Enter your email, request an OTP, then enter it above to sign in.</center></html>", JLabel.CENTER);
        note.setFont(UITheme.FONT_CAPTION);
        note.setForeground(UITheme.TEXT_MUTED);
        note.setAlignmentX(CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(20));
        p.add(note);
        return p;
    }

    
    private JPanel buildRegisterCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(24, 0, 0, 0));

        regNameField  = UITheme.field();
        regEmailField = UITheme.field();
        regPhoneField = UITheme.field();
        regPhoneField.setText("+2507");

        for (JTextField f : new JTextField[]{regNameField, regEmailField, regPhoneField}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            f.setAlignmentX(LEFT_ALIGNMENT);
        }

        regStatusLabel = new JLabel(" ", JLabel.CENTER);
        regStatusLabel.setFont(UITheme.FONT_SMALL);
        regStatusLabel.setForeground(UITheme.TEXT_MUTED);
        regStatusLabel.setAlignmentX(CENTER_ALIGNMENT);
        regStatusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JButton regBtn = UITheme.primaryBtn("Submit Registration Request");
        regBtn.setAlignmentX(CENTER_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        regBtn.addActionListener(e -> registerUser());

        
        JPanel infoBox = new JPanel(new BorderLayout(10, 0));
        infoBox.setBackground(UITheme.AMBER_100);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(253, 230, 138), 1, true),
            new EmptyBorder(10, 14, 10, 14)));
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        infoBox.setAlignmentX(LEFT_ALIGNMENT);
        JLabel infoText = new JLabel("<html>After submitting, an administrator will review your request. You will receive an email notification once approved.</html>");
        infoText.setFont(UITheme.FONT_CAPTION);
        infoText.setForeground(new Color(120, 80, 0));
        infoBox.add(infoText, BorderLayout.CENTER);

        p.add(fieldGroup("Full name", regNameField));
        p.add(Box.createVerticalStrut(14));
        p.add(fieldGroup("Email address", regEmailField));
        p.add(Box.createVerticalStrut(14));
        p.add(fieldGroup("Phone number (+2507XXXXXXXX)", regPhoneField));
        p.add(Box.createVerticalStrut(10));
        p.add(regStatusLabel);
        p.add(Box.createVerticalStrut(16));
        p.add(regBtn);
        p.add(Box.createVerticalStrut(16));
        p.add(infoBox);
        return p;
    }

    private JPanel fieldGroup(String labelText, JComponent field) {
        JPanel g = new JPanel(new BorderLayout(0, 7));
        g.setOpaque(false);
        g.setAlignmentX(LEFT_ALIGNMENT);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel lbl = UITheme.label(labelText);
        g.add(lbl, BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    
    private void requestOTP() {
        String email = loginEmailField.getText().trim();
        if (email.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter your email address.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        requestOtpBtn.setEnabled(false); requestOtpBtn.setText("Sending...");
        new SwingWorker<Void, Void>() {
            String err;
            @Override protected Void doInBackground() {
                try { service.requestOTP(email); } catch (Exception e) { err = extractMsg(e); }
                return null;
            }
            @Override protected void done() {
                requestOtpBtn.setEnabled(true); requestOtpBtn.setText("Send OTP to Email");
                if (err != null) JOptionPane.showMessageDialog(LoginFrame.this, err, "Access Denied", JOptionPane.ERROR_MESSAGE);
                else { loginStatusLabel.setText("OTP sent to " + email + " — check your inbox"); loginStatusLabel.setForeground(UITheme.PRIMARY); otpField.setText(""); otpField.requestFocus(); }
            }
        }.execute();
    }

    private void verifyOTP() {
        String email = loginEmailField.getText().trim();
        String otp   = new String(otpField.getPassword()).trim();
        if (email.isEmpty() || otp.isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        verifyBtn.setEnabled(false); verifyBtn.setText("Verifying...");
        new SwingWorker<Boolean, Void>() {
            String err, role = "OFFICER";
            @Override protected Boolean doInBackground() {
                try { boolean ok = service.verifyOTP(email, otp); if (ok) role = service.getUserRole(email); return ok; }
                catch (Exception e) { err = extractMsg(e); return false; }
            }
            @Override protected void done() {
                verifyBtn.setEnabled(true); verifyBtn.setText("Verify & Sign In");
                try {
                    if (get()) { dispose(); new MainDashboard(email, role); }
                    else if (err != null) { JOptionPane.showMessageDialog(LoginFrame.this, err, "Login Failed", JOptionPane.ERROR_MESSAGE); otpField.setText(""); }
                    else { JOptionPane.showMessageDialog(LoginFrame.this, "Invalid OTP. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE); otpField.setText(""); }
                } catch (Exception ex) { JOptionPane.showMessageDialog(LoginFrame.this, extractMsg(ex), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void registerUser() {
        String name = regNameField.getText().trim(), email = regEmailField.getText().trim(), phone = regPhoneField.getText().trim();
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) { JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        new SwingWorker<Void, Void>() {
            String err;
            @Override protected Void doInBackground() {
                try { service.registerUser(name, email, phone); } catch (Exception e) { err = extractMsg(e); }
                return null;
            }
            @Override protected void done() {
                if (err != null) JOptionPane.showMessageDialog(LoginFrame.this, err, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                else { JOptionPane.showMessageDialog(LoginFrame.this, "Registration submitted!\nAwait admin approval via email.", "Success", JOptionPane.INFORMATION_MESSAGE); regNameField.setText(""); regEmailField.setText(""); regPhoneField.setText("+2507"); }
            }
        }.execute();
    }

    private static String extractMsg(Throwable t) {
        String msg = t.getMessage();
        if (msg == null) return "An unexpected error occurred.";
        
        
        if (msg.contains("already exists") || msg.contains("duplicate")) {
            if (msg.contains("phone")) return "This phone number is already registered.";
            if (msg.contains("email")) return "This email address is already registered.";
            return "This information is already registered.";
        }
        if (msg.contains("not found")) return "User not found.";
        if (msg.contains("invalid") && msg.contains("otp")) return "Invalid OTP code.";
        if (msg.contains("expired")) return "OTP has expired. Please request a new one.";
        
        
        int nl = msg.lastIndexOf('\n');
        if (nl >= 0) msg = msg.substring(nl + 1).trim();
        
        
        if (msg.contains("nested exception is:")) {
            int idx = msg.indexOf("nested exception is:");
            msg = msg.substring(idx + 21).trim();
        }
        
        
        String[] prefixes = {"java.rmi.ServerException:", "java.lang.RuntimeException:", "java.lang.Exception:"};
        for (String prefix : prefixes) {
            if (msg.startsWith(prefix)) {
                msg = msg.substring(prefix.length()).trim();
                break;
            }
        }
        
        
        int semi = msg.indexOf(';');
        if (semi > 0) msg = msg.substring(0, semi).trim();
        
        
        int colon = msg.lastIndexOf(':');
        if (colon >= 0 && colon < msg.length() - 2) msg = msg.substring(colon + 1).trim();
        
        return msg.isEmpty() ? "An unexpected error occurred." : msg;
    }
    private KeyAdapter enter(Runnable r) {
        return new KeyAdapter() { @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) r.run(); } };
    }

    static void main(String[] args) { SwingUtilities.invokeLater(LoginFrame::new); }
}
