package com.wastonix.client.dashboard;

import com.wastonix.client.admin.AdminApprovalFrame;
import com.wastonix.client.admin.UserManagementPanel;
import com.wastonix.client.farmer.FarmerManagementFrame;
import com.wastonix.client.harvest.HarvestLogFrame;
import com.wastonix.client.sale.SaleRecordFrame;
import com.wastonix.client.report.ReportExportFrame;
import com.wastonix.client.notification.ActiveMQNotificationListener;
import com.wastonix.client.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class MainDashboard extends JFrame {
    private final String currentUser;
    private final String role;

    public MainDashboard(String currentUser, String role) {
        super("AgriStock & AgriTrack");
        this.currentUser = currentUser;
        this.role = role != null ? role : "OFFICER";
        UITheme.applyGlobalDefaults();
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        new ActiveMQNotificationListener().start();
        setVisible(true);
    }

    private JPanel mainContentPanel;
    private JPanel topBarPanel;
    private JLabel pageTitle;


    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);

        
        topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(UITheme.WHITE);
        topBarPanel.setBorder(new MatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.SLATE_900);
        pageTitle.setBorder(new EmptyBorder(18, 28, 18, 0));

        JLabel greeting = new JLabel("Good day, " + currentUser.split("@")[0]);
        greeting.setFont(UITheme.FONT_SMALL);
        greeting.setForeground(UITheme.TEXT_MUTED);
        greeting.setBorder(new EmptyBorder(0, 0, 0, 28));

        topBarPanel.add(pageTitle, BorderLayout.WEST);
        topBarPanel.add(greeting, BorderLayout.EAST);

        
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(UITheme.BG);

        
        JPanel initialContent = buildInitialDashboardContent();
        mainContentPanel.add(initialContent, BorderLayout.CENTER);

        main.add(topBarPanel, BorderLayout.NORTH);
        main.add(mainContentPanel, BorderLayout.CENTER);
        main.add(UITheme.statusBar("Connected to AgriStock & AgriTrack RMI Server  |  Port 5000  |  " + currentUser + "  [" + role + "]"), BorderLayout.SOUTH);
        return main;
    }

    private JPanel buildInitialDashboardContent() {
        JPanel cardsArea = new JPanel(new GridBagLayout());
        cardsArea.setBackground(UITheme.BG);

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        JPanel grid = new JPanel(new GridLayout(isAdmin ? 3 : 2, 2, 20, 20));
        grid.setBackground(UITheme.BG);
        grid.setPreferredSize(new Dimension(isAdmin ? 860 : 860, isAdmin ? 620 : 420));

        grid.add(dashCard("Farmers",        "Register, update & view farmer profiles",   UITheme.GREEN_600,  UITheme.GREEN_100,  UITheme.GREEN_900,  () -> loadPanel("Farmer Management", new FarmerManagementFrame())));
        grid.add(dashCard("Harvest Log",    "Record crop harvests & quantities",          new Color(5,150,105), new Color(209,250,229), new Color(6,95,70), () -> loadPanel("Harvest Logging", new HarvestLogFrame())));
        grid.add(dashCard("Sales",          "Track sales & revenue per farmer",           UITheme.AMBER_500,  UITheme.AMBER_100,  new Color(120,53,15), () -> loadPanel("Sales Records", new SaleRecordFrame())));
        grid.add(dashCard("Reports",        "Export CSV & PDF analytics",                 UITheme.BLUE_600,   new Color(219,234,254), new Color(30,58,138), () -> loadPanel("Reports", new ReportExportFrame())));

        if (isAdmin) {
            grid.add(dashCard("User Approvals", "Review & approve pending registrations", UITheme.PURPLE_600, new Color(237,233,254), new Color(76,29,149), () -> loadPanel("User Approval Management", new AdminApprovalFrame())));
            JPanel empty = new JPanel(); empty.setBackground(UITheme.BG); grid.add(empty);
        }

        cardsArea.add(grid);
        return cardsArea;
    }

    
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, UITheme.GREEN_900, 0, getHeight(), new Color(5, 46, 22)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 0));

        
        JPanel logoArea = new JPanel(new GridBagLayout());
        logoArea.setOpaque(false);
        logoArea.setBorder(new EmptyBorder(32, 24, 24, 24));
        logoArea.setPreferredSize(new Dimension(260, 110));

        JPanel logoBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.GREEN_600);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        logoBox.setOpaque(false);
        logoBox.setPreferredSize(new Dimension(44, 44));
        JLabel logoLbl = new JLabel("AR");
        logoLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLbl.setForeground(UITheme.WHITE);
        logoBox.add(logoLbl);

        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 2));
        logoText.setOpaque(false);
        JLabel appName = new JLabel("AgriStock & AgriTrack");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appName.setForeground(UITheme.WHITE);
        JLabel appSub = new JLabel("Stock Management");
        appSub.setFont(UITheme.FONT_CAPTION);
        appSub.setForeground(new Color(134, 239, 172));
        logoText.add(appName); logoText.add(appSub);

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logoRow.setOpaque(false);
        logoRow.add(logoBox); logoRow.add(logoText);
        logoArea.add(logoRow);

        
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel navHeader = new JLabel("NAVIGATION");
        navHeader.setFont(UITheme.FONT_CAPTION);
        navHeader.setForeground(new Color(100, 180, 130));
        navHeader.setBorder(new EmptyBorder(0, 8, 10, 0));
        navHeader.setAlignmentX(LEFT_ALIGNMENT);
        nav.add(navHeader);

        nav.add(navItem("Farmers",       "Manage cooperative farmers",  UITheme.GREEN_400,  () -> loadPanel("Farmer Management", new FarmerManagementFrame())));
        nav.add(Box.createVerticalStrut(4));
        nav.add(navItem("Harvest",       "Log crop harvests",           UITheme.GREEN_400,  () -> loadPanel("Harvest Logging", new HarvestLogFrame())));
        nav.add(Box.createVerticalStrut(4));
        nav.add(navItem("Sales",         "Record sales & revenue",      UITheme.AMBER_500,  () -> loadPanel("Sales Records", new SaleRecordFrame())));
        nav.add(Box.createVerticalStrut(4));
        nav.add(navItem("Reports",       "Export PDF & CSV",            UITheme.BLUE_600,   () -> loadPanel("Reports", new ReportExportFrame())));

        if ("ADMIN".equalsIgnoreCase(role)) {
            nav.add(Box.createVerticalStrut(16));
            JLabel adminHeader = new JLabel("ADMINISTRATION");
            adminHeader.setFont(UITheme.FONT_CAPTION);
            adminHeader.setForeground(new Color(196, 181, 253));
            adminHeader.setBorder(new EmptyBorder(0, 8, 10, 0));
            adminHeader.setAlignmentX(LEFT_ALIGNMENT);
            nav.add(adminHeader);
            nav.add(navItem("User Approvals", "Review registrations", UITheme.PURPLE_600, () -> loadPanel("User Approval Management", new AdminApprovalFrame())));
            nav.add(Box.createVerticalStrut(4));
            nav.add(navItem("User Management", "Manage registered users", UITheme.PURPLE_600, () -> loadPanel("User Management", new UserManagementPanel())));
        }

        
        JPanel userCard = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        userCard.setOpaque(false);
        userCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.GREEN_600);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));
        String initials = currentUser.contains("@")
            ? String.valueOf(currentUser.charAt(0)).toUpperCase()
            : currentUser.length() >= 2 ? currentUser.substring(0, 2).toUpperCase() : currentUser.toUpperCase();
        JLabel avatarLbl = new JLabel(initials);
        avatarLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatarLbl.setForeground(UITheme.WHITE);
        avatar.add(avatarLbl);

        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        userInfo.setOpaque(false);
        JLabel userLbl = new JLabel(currentUser.length() > 22 ? currentUser.substring(0, 20) + ".." : currentUser);
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userLbl.setForeground(UITheme.WHITE);
        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(UITheme.FONT_CAPTION);
        roleLbl.setForeground("ADMIN".equalsIgnoreCase(role) ? new Color(196, 181, 253) : new Color(134, 239, 172));
        userInfo.add(userLbl); userInfo.add(roleLbl);

        JButton logoutBtn = new JButton("Exit") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, getModel().isRollover() ? 30 : 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(UITheme.FONT_CAPTION);
        logoutBtn.setForeground(new Color(252, 165, 165));
        logoutBtn.setFocusPainted(false); logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false); logoutBtn.setOpaque(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(4, 8, 4, 8));
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Sign out of AgriStock & AgriTrack?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { dispose(); new com.wastonix.client.auth.LoginFrame(); }
        });

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(userInfo, BorderLayout.CENTER);
        userCard.add(logoutBtn, BorderLayout.EAST);

        JPanel bottomPad = new JPanel(new BorderLayout());
        bottomPad.setOpaque(false);
        bottomPad.setBorder(new EmptyBorder(0, 16, 20, 16));
        bottomPad.add(userCard);

        sidebar.add(logoArea, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(bottomPad, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel navItem(String title, String desc, Color accent, Runnable action) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isOpaque()) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(10, 10, 10, 10));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        item.setAlignmentX(LEFT_ALIGNMENT);

        
        JPanel accentBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(4, 0));

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 2));
        texts.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.WHITE);
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UITheme.FONT_CAPTION);
        descLbl.setForeground(new Color(134, 239, 172));
        texts.add(titleLbl); texts.add(descLbl);

        item.add(accentBar, BorderLayout.WEST);
        item.add(texts, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { item.setOpaque(true); item.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { item.setOpaque(false); item.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return item;
    }


    private JPanel dashCard(String title, String desc, Color accent, Color bgLight, @SuppressWarnings("unused") Color textDark, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setBackground(UITheme.WHITE);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        
        JPanel strip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() * 2, 16, 16));
                g2.dispose();
            }
        };
        strip.setOpaque(false);
        strip.setPreferredSize(new Dimension(0, 5));

        
        JPanel iconBadge = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgLight);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(48, 48));
        JLabel iconLbl = new JLabel(String.valueOf(title.charAt(0)));
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconLbl.setForeground(accent);
        iconBadge.add(iconLbl);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_H2);
        titleLbl.setForeground(UITheme.SLATE_900);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel arrowLbl = new JLabel("  Open  >");
        arrowLbl.setFont(UITheme.FONT_CAPTION);
        arrowLbl.setForeground(accent);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLbl);
        textPanel.add(descLbl);
        textPanel.add(arrowLbl);

        body.add(iconBadge, BorderLayout.WEST);
        body.add(textPanel, BorderLayout.CENTER);

        card.add(strip, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(bgLight);
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(accent, 2, true), new EmptyBorder(22, 22, 22, 22)));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(UITheme.WHITE);
                card.setBorder(new EmptyBorder(24, 24, 24, 24));
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return card;
    }

    
    private void loadPanel(String title, JPanel panel) {
        
        pageTitle.setText(title);
        
        
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    
    private void loadPanel(String title, JFrame frame) {
        pageTitle.setText(title);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);

        Container content = frame.getContentPane();
        frame.setVisible(false);
        content.setPreferredSize(null);

        if (content.getParent() != null)
            ((Container) content.getParent()).remove(content);

        wrapper.add(content, BorderLayout.CENTER);

        mainContentPanel.removeAll();
        mainContentPanel.add(wrapper, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    void open(Runnable r) {
        try { r.run(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }
}
