package com.wastonix.client.admin;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.model.User;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.rmi.RemoteException;
import java.util.List;
import javax.swing.SwingUtilities;

public class AdminApprovalFrame extends JFrame {
    private final IAgriStockService service;
    private JTable pendingTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public AdminApprovalFrame() {
        super("User Approval Management");
        this.service = RmiClientUtil.getService();
        initComponents();
        buildUI();
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));
        setSize(1200, 1200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        loadPendingUsers();
    }

    private void initComponents() {
        String[] cols = {"ID", "Full Name", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        pendingTable = new JTable(tableModel);
        UITheme.styleTable(pendingTable);
        pendingTable.setRowHeight(42);

        pendingTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(v != null ? v.toString() : "", JLabel.CENTER);
                lbl.setFont(UITheme.FONT_BOLD); lbl.setOpaque(true);
                String s = v != null ? v.toString() : "";
                switch (s) {
                    case "PENDING"  -> { lbl.setBackground(UITheme.AMBER_100);  lbl.setForeground(new Color(120, 80, 0)); }
                    case "APPROVED" -> { lbl.setBackground(UITheme.GREEN_100);  lbl.setForeground(UITheme.GREEN_900); }
                    case "REJECTED" -> { lbl.setBackground(UITheme.RED_100);    lbl.setForeground(UITheme.RED_600); }
                    default         -> { lbl.setBackground(UITheme.WHITE);      lbl.setForeground(UITheme.TEXT_MAIN); }
                }
                if (sel) { lbl.setBackground(UITheme.GREEN_100); lbl.setForeground(UITheme.GREEN_900); }
                return lbl;
            }
        });

        countLabel = new JLabel("0 pending");
        countLabel.setFont(UITheme.FONT_BOLD);
        countLabel.setForeground(UITheme.PRIMARY_DARK);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.topBar("A", "User Approval Management", "Review and approve pending user registrations"), BorderLayout.NORTH);

        
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(UITheme.WHITE);
        actionBar.setBorder(new MatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        btnRow.setBackground(UITheme.WHITE);
        JButton approveBtn = UITheme.primaryBtn("Approve Selected");
        JButton rejectBtn  = UITheme.dangerBtn("Reject Selected");
        JButton refreshBtn = UITheme.ghostBtn("Refresh");
        approveBtn.addActionListener(e -> handleAction(true));
        rejectBtn.addActionListener(e -> handleAction(false));
        refreshBtn.addActionListener(e -> loadPendingUsers());
        btnRow.add(approveBtn); btnRow.add(rejectBtn); btnRow.add(refreshBtn);

        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        countRow.setBackground(UITheme.WHITE);
        countRow.add(countLabel);

        actionBar.add(btnRow, BorderLayout.WEST);
        actionBar.add(countRow, BorderLayout.EAST);

        
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        statsRow.setBackground(UITheme.BG);
        statsRow.setBorder(new EmptyBorder(20, 24, 12, 24));
        statsRow.add(statCard("Pending",  "Awaiting review",  UITheme.AMBER_500));
        statsRow.add(statCard("Approved", "Access granted",   UITheme.GREEN_600));
        statsRow.add(statCard("Rejected", "Access denied",    UITheme.RED_600));

        
        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(UITheme.sectionBorder("Registration Requests"));
        tableCard.add(actionBar, BorderLayout.NORTH);
        tableCard.add(UITheme.scrollPane(pendingTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(UITheme.BG);
        center.add(statsRow, BorderLayout.NORTH);
        JPanel tablePad = new JPanel(new BorderLayout());
        tablePad.setBackground(UITheme.BG);
        tablePad.setBorder(new EmptyBorder(0, 24, 20, 24));
        tablePad.add(tableCard);
        center.add(tablePad, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(UITheme.statusBar("Connected  |  Select a row then Approve or Reject"), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel statCard(String label, String sub, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight(), 5, 5));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(180, 64));
        card.setBorder(new EmptyBorder(12, 20, 12, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD); lbl.setForeground(UITheme.TEXT_MAIN);
        JLabel sublbl = new JLabel(sub);
        sublbl.setFont(UITheme.FONT_CAPTION); sublbl.setForeground(UITheme.TEXT_MUTED);

        card.add(lbl, BorderLayout.CENTER);
        card.add(sublbl, BorderLayout.SOUTH);
        return card;
    }

    private void loadPendingUsers() {
        try {
            List<User> users = service.getPendingUsers();
            tableModel.setRowCount(0);
            for (User u : users)
                tableModel.addRow(new Object[]{u.getId(), u.getFullName() != null ? u.getFullName() : "—", u.getEmail(), u.getPhone() != null ? u.getPhone() : "—", u.getApprovalStatus().name()});
            countLabel.setText(users.size() + " pending request" + (users.size() != 1 ? "s" : ""));
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void handleAction(boolean approve) {
        int row = pendingTable.getSelectedRow();
        if (row < 0) { warn("Please select a user from the table first"); return; }
        int userId = (Integer) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String email = (String) tableModel.getValueAt(row, 2);
        int c = JOptionPane.showConfirmDialog(owner(),
            (approve ? "Approve" : "Reject") + " registration for:\n\nName:  " + name + "\nEmail: " + email,
            "Confirm", JOptionPane.YES_NO_OPTION,
            approve ? JOptionPane.QUESTION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            if (approve) service.approveUser(userId); else service.rejectUser(userId);
            
            loadPendingUsers(); refresh();
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private Window owner()           { Window w = SwingUtilities.getWindowAncestor(pendingTable); return w != null ? w : this; }
    private void warn(String m)      { JOptionPane.showMessageDialog(owner(), m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(owner(), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE); }
    private void refresh() { getContentPane().revalidate(); getContentPane().repaint(); }
}
