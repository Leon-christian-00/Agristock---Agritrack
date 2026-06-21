package com.wastonix.client.report;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.model.Farmer;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;

public class ReportExportFrame extends JFrame {
    private final IAgriStockService service;
    private JComboBox<FarmerItem> farmerCombo;
    private JRadioButton allFarmersRadio, specificFarmerRadio;
    private JRadioButton harvestRadio, salesRadio, bothRadio;

    public ReportExportFrame() {
        super("Generate Reports");
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
        farmerCombo         = new JComboBox<>(); farmerCombo.setFont(UITheme.FONT_BODY);
        allFarmersRadio     = radio("All Farmers", true);
        specificFarmerRadio = radio("Specific Farmer", false);
        harvestRadio        = radio("Harvest Only", false);
        salesRadio          = radio("Sales Only", false);
        bothRadio           = radio("Both Harvest & Sales", true);

        ButtonGroup sg = new ButtonGroup(); sg.add(allFarmersRadio); sg.add(specificFarmerRadio);
        ButtonGroup tg = new ButtonGroup(); tg.add(harvestRadio); tg.add(salesRadio); tg.add(bothRadio);

        specificFarmerRadio.addActionListener(e -> farmerCombo.setEnabled(true));
        allFarmersRadio.addActionListener(e -> farmerCombo.setEnabled(false));
    }

    private JRadioButton radio(String text, boolean selected) {
        JRadioButton r = new JRadioButton(text, selected);
        r.setFont(UITheme.FONT_BODY); r.setForeground(UITheme.TEXT_MAIN);
        r.setBackground(UITheme.CARD_BG); r.setFocusPainted(false);
        return r;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.topBar("R", "Report Generation", "Export comprehensive CSV and PDF analytics"), BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(new EmptyBorder(32, 36, 32, 36));
        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        
        JPanel scopeBox = sectionBox("Report Scope");
        scopeBox.add(allFarmersRadio);
        scopeBox.add(Box.createVerticalStrut(10));
        JPanel specRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        specRow.setBackground(UITheme.CARD_BG);
        specRow.add(specificFarmerRadio); specRow.add(farmerCombo);
        farmerCombo.setEnabled(false);
        scopeBox.add(specRow);

        
        JPanel typeBox = sectionBox("Report Type");
        typeBox.add(harvestRadio);
        typeBox.add(Box.createVerticalStrut(8));
        typeBox.add(salesRadio);
        typeBox.add(Box.createVerticalStrut(8));
        typeBox.add(bothRadio);

        JButton csvBtn = UITheme.primaryBtn("Export to CSV");
        JButton pdfBtn = UITheme.accentBtn("Generate PDF Report");
        csvBtn.setAlignmentX(CENTER_ALIGNMENT);
        pdfBtn.setAlignmentX(CENTER_ALIGNMENT);
        csvBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        pdfBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        csvBtn.addActionListener(e -> exportCSV());
        pdfBtn.addActionListener(e -> exportPDF());

        card.add(scopeBox);
        card.add(Box.createVerticalStrut(20));
        card.add(typeBox);
        card.add(Box.createVerticalStrut(28));
        card.add(csvBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(pdfBtn);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);
        center.add(card);

        root.add(center, BorderLayout.CENTER);
        root.add(UITheme.statusBar("Connected  |  Select options and export"), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel sectionBox(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            UITheme.sectionBorder(title), new EmptyBorder(10, 8, 10, 8)));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        return p;
    }

    private void loadFarmers() {
        try {
            farmerCombo.removeAllItems();
            farmerCombo.addItem(new FarmerItem(0, "-- Select Farmer --", ""));
            for (Farmer f : service.findAllFarmers())
                farmerCombo.addItem(new FarmerItem(f.getId(), f.getFullName(), f.getPhone()));
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void exportCSV() {
        int fid = getFarmerId(); if (fid == -1) return;
        String farmerName = getSelectedFarmerName();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String suggestedName = farmerName.isEmpty()
            ? "agristock-agritrack-all-farmers-report-" + date
            : sanitize(farmerName) + "-harvest-report-" + date;

        try {
            String serverPath = service.exportHarvestsToCSV(fid);
            JOptionPane.showMessageDialog(owner(),
                "CSV exported!\nFile: " + suggestedName + ".csv\nServer path:\n" + serverPath,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { showError(e.getMessage()); }
        refresh();
    }

    private void exportPDF() {
        int fid = getFarmerId(); if (fid == -1) return;
        String farmerName = getSelectedFarmerName();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String suggestedName = farmerName.isEmpty()
            ? "agristock-agritrack-all-farmers-report-" + date
            : sanitize(farmerName) + "-harvest-report-" + date;

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save PDF Report");
        fc.setSelectedFile(new File(suggestedName + ".pdf"));
        if (fc.showSaveDialog(owner()) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".pdf")) path += ".pdf";
                Files.write(Paths.get(path), service.generateHarvestReportPDF(fid));
                JOptionPane.showMessageDialog(owner(),
                    "PDF generated!\nSaved to:\n" + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { showError(e.getMessage()); }
            refresh();
        }
    }

    private int getFarmerId() {
        if (specificFarmerRadio.isSelected()) {
            FarmerItem sel = (FarmerItem) farmerCombo.getSelectedItem();
            if (sel == null || sel.id == 0) { warn("Please select a farmer"); return -1; }
            return sel.id;
        }
        return 0;
    }

    private String getSelectedFarmerName() {
        if (specificFarmerRadio.isSelected()) {
            FarmerItem sel = (FarmerItem) farmerCombo.getSelectedItem();
            if (sel != null && sel.id != 0) return sel.rawName;
        }
        return "";
    }

    
    private String sanitize(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    private Window owner()           { Window w = SwingUtilities.getWindowAncestor(farmerCombo); return w != null ? w : this; }
    private void refresh() { getContentPane().revalidate(); getContentPane().repaint(); }
    private void warn(String m)      { JOptionPane.showMessageDialog(owner(), m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(owner(), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE); }

    private record FarmerItem(int id, String rawName, String phone) {
        @Override public String toString() {
            return id == 0 ? rawName : "FAR" + String.format("%03d", id) + " — " + rawName + " (" + phone + ")";
        }
    }
}
