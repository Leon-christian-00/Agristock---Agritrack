package com.wastonix.client.harvest;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.model.Farmer;
import com.wastonix.model.Harvest;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import javax.swing.SwingUtilities;

public class HarvestLogFrame extends JFrame {
    private final IAgriStockService service;
    private JComboBox<FarmerItem> farmerCombo;
    private JTextField cropField, quantityField, searchField;
    private JComboBox<String> qualityCombo;
    private JSpinner dateSpinner;
    private JTable harvestTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public HarvestLogFrame() {
        super("Harvest Logging");
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
        loadHarvests();
    }

    private void initComponents() {
        farmerCombo   = new JComboBox<>(); farmerCombo.setFont(UITheme.FONT_BODY);
        cropField     = UITheme.field();
        quantityField = UITheme.field();
        qualityCombo  = UITheme.combo("Grade A", "Grade B", "Standard");
        searchField   = UITheme.field();
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID, farmer, crop, grade...");

        SpinnerDateModel dm = new SpinnerDateModel();
        dateSpinner = new JSpinner(dm);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(UITheme.FONT_BODY);

        String[] cols = {"ID", "Farmer", "Crop", "Quantity (kg)", "Grade", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        harvestTable = new JTable(tableModel);
        UITheme.styleTable(harvestTable);

        sorter = new TableRowSorter<>(tableModel);
        harvestTable.setRowSorter(sorter);

        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { String t = searchField.getText().trim(); sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t)); }
            public void removeUpdate(DocumentEvent e) { String t = searchField.getText().trim(); sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t)); }
            public void changedUpdate(DocumentEvent e) {}
        };
        searchField.getDocument().addDocumentListener(searchListener);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.topBar("H", "Harvest Logging", "Record crop harvests for registered farmers"), BorderLayout.NORTH);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setBorder(UITheme.sectionBorder("Harvest Details"));

        JPanel fields = new JPanel(new GridLayout(5, 2, 14, 12));
        fields.setBackground(UITheme.CARD_BG);
        fields.setBorder(new EmptyBorder(12, 14, 4, 14));
        for (JTextField f : new JTextField[]{cropField, quantityField})
            f.setPreferredSize(new Dimension(0, 40));
        fields.add(UITheme.label("Select Farmer"));  fields.add(farmerCombo);
        fields.add(UITheme.label("Crop Name"));      fields.add(cropField);
        fields.add(UITheme.label("Quantity (kg)"));  fields.add(quantityField);
        fields.add(UITheme.label("Quality Grade"));  fields.add(qualityCombo);
        fields.add(UITheme.label("Harvest Date"));   fields.add(dateSpinner);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        btnRow.setBackground(UITheme.CARD_BG);
        JButton saveBtn    = UITheme.primaryBtn("Log Harvest");
        JButton refreshBtn = UITheme.ghostBtn("Refresh");
        saveBtn.addActionListener(e -> saveHarvest());
        refreshBtn.addActionListener(e -> { loadHarvests(); refresh(); });
        btnRow.add(saveBtn); btnRow.add(refreshBtn);

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
        tableCard.setBorder(UITheme.sectionBorder("Harvest Records  (click column header to sort)"));
        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(UITheme.scrollPane(harvestTable), BorderLayout.CENTER);

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
            farmerCombo.removeAllItems();
            for (Farmer f : service.findAllFarmers())
                farmerCombo.addItem(new FarmerItem(f.getId(), f.getFullName()));
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void loadHarvests() {
        try {
            tableModel.setRowCount(0);
            for (Harvest h : service.findAllHarvests())
                tableModel.addRow(new Object[]{
                    "HAR" + String.format("%03d", h.getId()),
                    h.getFarmer().getFullName(),
                    h.getCropName(),
                    h.getQuantityKg(),
                    h.getQualityGrade(),
                    h.getHarvestDate()
                });
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void saveHarvest() {
        if (farmerCombo.getSelectedItem() == null) { warn("Please select a farmer"); return; }
        if (cropField.getText().trim().isEmpty())  { warn("Crop name is required"); return; }
        if (quantityField.getText().trim().isEmpty()) { warn("Quantity is required"); return; }
        try {
            double qty = Double.parseDouble(quantityField.getText().trim());
            if (qty <= 0) { warn("Quantity must be greater than 0"); return; }
            FarmerItem sel = (FarmerItem) farmerCombo.getSelectedItem();
            Harvest h = new Harvest();
            Farmer f = new Farmer(); f.setId(sel.id);
            h.setFarmer(f); h.setCropName(cropField.getText().trim());
            h.setQuantityKg(qty); h.setQualityGrade((String) qualityCombo.getSelectedItem());
            h.setHarvestDate(LocalDate.now());
            Harvest saved = service.saveHarvest(h);
            JOptionPane.showMessageDialog(this,
                "Harvest logged!  ID: HAR" + String.format("%03d", saved.getId()),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            cropField.setText(""); quantityField.setText(""); loadHarvests(); refresh();
        } catch (NumberFormatException e) { warn("Quantity must be a valid number"); }
        catch (RemoteException e) { showError(e.getMessage()); }
    }

    private Window owner()           { Window w = SwingUtilities.getWindowAncestor(cropField); return w != null ? w : this; }
    private void warn(String m)      { JOptionPane.showMessageDialog(owner(), m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(owner(), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE); }
    private void refresh()           { getContentPane().revalidate(); getContentPane().repaint(); }

    private record FarmerItem(int id, String name) {
        @Override public String toString() { return name; }
    }
}
