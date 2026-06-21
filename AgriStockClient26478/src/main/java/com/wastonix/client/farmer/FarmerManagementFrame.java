package com.wastonix.client.farmer;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.model.Farmer;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

public class FarmerManagementFrame extends JFrame {
    private final IAgriStockService service;
    private JTable farmerTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField nameField, phoneField, locationField, farmSizeField, searchField;
    private Farmer selectedFarmer;

    public FarmerManagementFrame() {
        super("Farmer Management");
        this.service = RmiClientUtil.getService();
        initComponents();
        buildUI();
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));
        setSize(1200, 1200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        loadFarmers();
    }

    private void initComponents() {
        nameField     = UITheme.field();
        phoneField    = UITheme.field();
        locationField = UITheme.field();
        farmSizeField = UITheme.field();
        searchField   = UITheme.field();
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID, name, phone, location...");

        String[] cols = {"ID", "Full Name", "Phone", "Location", "Farm Size (ha)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        farmerTable = new JTable(tableModel);
        UITheme.styleTable(farmerTable);

        sorter = new TableRowSorter<>(tableModel);
        farmerTable.setRowSorter(sorter);

        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        };
        searchField.getDocument().addDocumentListener(searchListener);

        farmerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = farmerTable.getSelectedRow();
                if (row >= 0) {
                    int modelRow = farmerTable.convertRowIndexToModel(row);
                    try {
                        int id = (int) tableModel.getValueAt(modelRow, 0);
                        selectedFarmer = service.findFarmerById(id);
                        populateForm(selectedFarmer);
                    } catch (RemoteException ex) { showError(ex.getMessage()); }
                }
            }
        });
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) { sorter.setRowFilter(null); return; }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.topBar("F", "Farmer Management", "Register and manage cooperative farmers"), BorderLayout.NORTH);

        
        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setBorder(UITheme.sectionBorder("Farmer Details"));

        JPanel fields = new JPanel(new GridLayout(4, 2, 14, 12));
        fields.setBackground(UITheme.CARD_BG);
        fields.setBorder(new EmptyBorder(12, 14, 4, 14));
        for (JTextField f : new JTextField[]{nameField, phoneField, locationField, farmSizeField})
            f.setPreferredSize(new Dimension(0, 40));
        fields.add(UITheme.label("Full Name"));            fields.add(nameField);
        fields.add(UITheme.label("Phone (+2507XXXXXXXX)")); fields.add(phoneField);
        fields.add(UITheme.label("Location"));             fields.add(locationField);
        fields.add(UITheme.label("Farm Size (hectares)"));  fields.add(farmSizeField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        btnRow.setBackground(UITheme.CARD_BG);
        JButton saveBtn    = UITheme.primaryBtn("Save");
        JButton updateBtn  = UITheme.secondaryBtn("Update");
        JButton deleteBtn  = UITheme.dangerBtn("Delete");
        JButton clearBtn   = UITheme.ghostBtn("Clear");
        JButton refreshBtn = UITheme.ghostBtn("Refresh");
        saveBtn.addActionListener(e -> saveFarmer());
        updateBtn.addActionListener(e -> updateFarmer());
        deleteBtn.addActionListener(e -> deleteFarmer());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> { loadFarmers(); refresh(); });
        btnRow.add(saveBtn); btnRow.add(updateBtn); btnRow.add(deleteBtn); btnRow.add(clearBtn); btnRow.add(refreshBtn);

        formCard.add(fields, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(UITheme.BG);
        searchBar.setBorder(new EmptyBorder(0, 0, 8, 0));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchBar.add(UITheme.label("Search:"), BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);

        
        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.setBorder(UITheme.sectionBorder("Registered Farmers  (click column header to sort)"));
        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(UITheme.scrollPane(farmerTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(UITheme.BG);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));
        center.add(formCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(UITheme.statusBar("Connected  |  Click column headers to sort  |  Type to search"), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void loadFarmers() {
        try {
            tableModel.setRowCount(0);
            for (Farmer f : service.findAllFarmers())
                tableModel.addRow(new Object[]{f.getId(), f.getFullName(), f.getPhone(), f.getLocation(), f.getFarmSizeHectares()});
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void saveFarmer() {
        if (!validateForm()) return;
        try {
            Farmer f = new Farmer();
            f.setFullName(nameField.getText().trim()); f.setPhone(phoneField.getText().trim());
            f.setLocation(locationField.getText().trim());
            String sz = farmSizeField.getText().trim();
            f.setFarmSizeHectares(sz.isEmpty() ? null : Double.parseDouble(sz));
            Farmer saved = service.saveFarmer(f);
            
            clearForm(); loadFarmers(); refresh();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateFarmer() {
        if (selectedFarmer == null) { warn("Select a farmer first"); return; }
        if (!validateForm()) return;
        try {
            selectedFarmer.setFullName(nameField.getText().trim()); selectedFarmer.setPhone(phoneField.getText().trim());
            selectedFarmer.setLocation(locationField.getText().trim());
            String sz = farmSizeField.getText().trim();
            selectedFarmer.setFarmSizeHectares(sz.isEmpty() ? null : Double.parseDouble(sz));
            service.updateFarmer(selectedFarmer);
            JOptionPane.showMessageDialog(this, "Farmer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadFarmers(); refresh();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteFarmer() {
        if (selectedFarmer == null) { warn("Select a farmer first"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete " + selectedFarmer.getFullName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { service.deleteFarmer(selectedFarmer.getId()); clearForm(); loadFarmers(); refresh(); }
            catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty())                    { warn("Name is required"); return false; }
        if (!phoneField.getText().trim().matches("^\\+2507\\d{8}$")) { warn("Phone: +2507XXXXXXXX"); return false; }
        if (locationField.getText().trim().isEmpty())                { warn("Location is required"); return false; }
        return true;
    }

    private void populateForm(Farmer f) {
        nameField.setText(f.getFullName()); phoneField.setText(f.getPhone());
        locationField.setText(f.getLocation());
        farmSizeField.setText(f.getFarmSizeHectares() != null ? f.getFarmSizeHectares().toString() : "");
    }

    private void clearForm() {
        nameField.setText(""); phoneField.setText(""); locationField.setText(""); farmSizeField.setText("");
        selectedFarmer = null; farmerTable.clearSelection();
    }

    private Window owner()           { Window w = SwingUtilities.getWindowAncestor(nameField); return w != null ? w : this; }
    private void warn(String m)      { JOptionPane.showMessageDialog(owner(), m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(owner(), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE); }
    private void refresh()           { getContentPane().revalidate(); getContentPane().repaint(); }
}
