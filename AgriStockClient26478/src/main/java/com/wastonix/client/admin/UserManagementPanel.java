package com.wastonix.client.admin;

import com.wastonix.client.util.UITheme;
import com.wastonix.model.User;
import com.wastonix.service.IAgriStockService;
import com.wastonix.client.util.RmiClientUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.rmi.RemoteException;

public class UserManagementPanel extends JPanel {
    private final IAgriStockService service;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField fullNameField, emailField, phoneField;
    private JComboBox<String> statusCombo;
    private User selectedUser;

    public UserManagementPanel() {
        this.service = RmiClientUtil.getService();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 0));
        setOpaque(false);
        
        initComponents();
        buildUI();
        loadUsers();
    }

    private void initComponents() {
        fullNameField = UITheme.field();
        emailField = UITheme.field();
        phoneField = UITheme.field();
        statusCombo = UITheme.combo("PENDING", "APPROVED", "REJECTED");

        String[] cols = {"ID", "Full Name", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(tableModel);
        UITheme.styleTable(userTable);
        userTable.setRowHeight(42);
        
        userTable.setDefaultRenderer(Object.class, new StatusCellRenderer());

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    try {
                        int userId = (int) tableModel.getValueAt(row, 0);
                        selectedUser = service.findUserById(userId);
                        populateForm(selectedUser);
                    } catch (RemoteException ex) {
                        showError(ex.getMessage());
                    }
                }
            }
        });
    }

    private void buildUI() {
        setBackground(UITheme.BG);

        
        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setBorder(UITheme.sectionBorder("User Details"));

        JPanel fields = new JPanel(new GridLayout(4, 2, 14, 12));
        fields.setBackground(UITheme.CARD_BG);
        fields.setBorder(new EmptyBorder(12, 14, 4, 14));

        for (JTextField f : new JTextField[]{fullNameField, emailField, phoneField})
            f.setPreferredSize(new Dimension(0, 40));

        fields.add(UITheme.label("Full Name")); fields.add(fullNameField);
        fields.add(UITheme.label("Email")); fields.add(emailField);
        fields.add(UITheme.label("Phone")); fields.add(phoneField);
        fields.add(UITheme.label("Status")); fields.add(statusCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        btnRow.setBackground(UITheme.CARD_BG);
        JButton saveBtn = UITheme.primaryBtn("Save");
        JButton updateBtn = UITheme.secondaryBtn("Update");
        JButton deleteBtn = UITheme.dangerBtn("Delete");
        JButton clearBtn = UITheme.ghostBtn("Clear");
        JButton refreshBtn = UITheme.ghostBtn("Refresh");
        
        saveBtn.addActionListener(e -> saveUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> loadUsers());
        
        btnRow.add(saveBtn); btnRow.add(updateBtn); btnRow.add(deleteBtn); btnRow.add(clearBtn); btnRow.add(refreshBtn);

        formCard.add(fields, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        
        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(UITheme.sectionBorder("Registered Users"));
        tableCard.add(UITheme.scrollPane(userTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(UITheme.BG);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));
        center.add(formCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(UITheme.statusBar("Connected  |  Select a row to edit or delete"), BorderLayout.SOUTH);
    }

    private void loadUsers() {
        try {
            tableModel.setRowCount(0);
            for (User u : service.findAllUsers()) {
                tableModel.addRow(new Object[]{
                    u.getId(), 
                    u.getFullName() != null ? u.getFullName() : "—", 
                    u.getEmail(), 
                    u.getPhone() != null ? u.getPhone() : "—", 
                    u.getApprovalStatus().name()
                });
            }
        } catch (RemoteException e) {
            showError(e.getMessage());
        }
    }

    private void saveUser() {
        if (!validateForm()) return;
        if (!emailField.getText().trim().contains("@")) {
            warn("Valid email is required");
            return;
        }
        
        try {
            User u = new User();
            u.setFullName(fullNameField.getText().trim());
            u.setEmail(emailField.getText().trim());
            u.setPhone(phoneField.getText().trim());
            
            String status = (String) statusCombo.getSelectedItem();
            if ("APPROVED".equals(status)) {
                u.setApprovalStatus(User.ApprovalStatus.APPROVED);
            } else if ("REJECTED".equals(status)) {
                u.setApprovalStatus(User.ApprovalStatus.REJECTED);
            } else {
                u.setApprovalStatus(User.ApprovalStatus.PENDING);
            }
            
            User saved = service.saveUser(u);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "User saved! ID: " + saved.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadUsers(); refresh();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void updateUser() {
        if (selectedUser == null) {
            warn("Select a user first");
            return;
        }
        if (!validateForm()) return;
        
        try {
            selectedUser.setFullName(fullNameField.getText().trim());
            selectedUser.setEmail(emailField.getText().trim());
            selectedUser.setPhone(phoneField.getText().trim());
            
            String status = (String) statusCombo.getSelectedItem();
            if ("APPROVED".equals(status)) {
                selectedUser.setApprovalStatus(User.ApprovalStatus.APPROVED);
            } else if ("REJECTED".equals(status)) {
                selectedUser.setApprovalStatus(User.ApprovalStatus.REJECTED);
            } else {
                selectedUser.setApprovalStatus(User.ApprovalStatus.PENDING);
            }
            
            service.updateUser(selectedUser);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "User updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadUsers(); refresh();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deleteUser() {
        if (selectedUser == null) {
            warn("Select a user first");
            return;
        }
        if (JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this), 
                "Delete " + selectedUser.getFullName() + "?", 
                "Confirm", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                service.deleteUser(selectedUser.getId());
                clearForm();
                loadUsers(); refresh();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            warn("Name is required");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            warn("Email is required");
            return false;
        }
        return true;
    }

    private void populateForm(User u) {
        fullNameField.setText(u.getFullName() == null ? "" : u.getFullName());
        emailField.setText(u.getEmail());
        phoneField.setText(u.getPhone() == null ? "" : u.getPhone());
        statusCombo.setSelectedItem(u.getApprovalStatus().name());
    }

    private void clearForm() {
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        statusCombo.setSelectedItem("PENDING");
        selectedUser = null;
        userTable.clearSelection();
    }

    private void warn(String m) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), m, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String m) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refresh() { revalidate(); repaint(); }
    
    private class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(UITheme.FONT_BOLD);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = value != null ? value.toString() : "";
            setText(status);
            
            switch (status) {
                case "PENDING":
                    setBackground(UITheme.AMBER_100);
                    setForeground(new Color(120, 80, 0));
                    break;
                case "APPROVED":
                    setBackground(UITheme.GREEN_100);
                    setForeground(UITheme.GREEN_900);
                    break;
                case "REJECTED":
                    setBackground(UITheme.RED_100);
                    setForeground(UITheme.RED_600);
                    break;
                default:
                    setBackground(UITheme.WHITE);
                    setForeground(UITheme.TEXT_MAIN);
                    break;
            }
            
            if (isSelected && !"PENDING".equals(status) && !"REJECTED".equals(status)) {
                setBackground(UITheme.GREEN_100);
                setForeground(UITheme.GREEN_900);
            }
            
            return this;
        }
    }
}