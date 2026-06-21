package com.wastonix.client.sale;

import com.wastonix.client.util.RmiClientUtil;
import com.wastonix.client.util.UITheme;
import com.wastonix.model.Farmer;
import com.wastonix.model.Harvest;
import com.wastonix.model.Sale;
import com.wastonix.service.IAgriStockService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.print.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

public class SaleRecordFrame extends JFrame {
    private final IAgriStockService service;
    private JComboBox<FarmerItem> farmerCombo;
    private JComboBox<String> cropCombo;
    private JTextField buyerField, quantityField, priceField, searchField;
    private JLabel revenueLabel, stockLabel;
    private JTable saleTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private Map<String, Double> cropStockMap = new HashMap<>();

    public SaleRecordFrame() {
        super("Record Sale");
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
        loadSales();
    }

    private void initComponents() {
        farmerCombo   = new JComboBox<>(); farmerCombo.setFont(UITheme.FONT_BODY);
        cropCombo     = new JComboBox<>(); cropCombo.setFont(UITheme.FONT_BODY); cropCombo.setEnabled(false);
        buyerField    = UITheme.field();
        quantityField = UITheme.field();
        priceField    = UITheme.field();
        searchField   = UITheme.field();
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID, farmer, crop, buyer...");

        revenueLabel = new JLabel("0 RWF", JLabel.CENTER);
        revenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        revenueLabel.setForeground(UITheme.GREEN_800);

        stockLabel = new JLabel("— kg available", JLabel.CENTER);
        stockLabel.setFont(UITheme.FONT_SMALL);
        stockLabel.setForeground(UITheme.BLUE_600);

        farmerCombo.addActionListener(e -> loadFarmerCrops());
        cropCombo.addActionListener(e -> updateStock());

        DocumentListener dl = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { calcRevenue(); }
            public void removeUpdate(DocumentEvent e)  { calcRevenue(); }
            public void insertUpdate(DocumentEvent e)  { calcRevenue(); }
        };
        quantityField.getDocument().addDocumentListener(dl);
        priceField.getDocument().addDocumentListener(dl);

        String[] cols = {"ID", "Farmer", "Crop", "Buyer", "Qty (kg)", "Unit Price", "Revenue (RWF)", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        saleTable = new JTable(tableModel);
        UITheme.styleTable(saleTable);

        sorter = new TableRowSorter<>(tableModel);
        saleTable.setRowSorter(sorter);

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
        root.add(UITheme.topBar("S", "Record Sale", "Track crop sales and revenue per farmer"), BorderLayout.NORTH);

        
        JPanel revBadge = new JPanel(new GridLayout(3, 1, 0, 4));
        revBadge.setBackground(UITheme.GREEN_50);
        revBadge.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.GREEN_400, 1, true), new EmptyBorder(16, 24, 16, 24)));
        revBadge.add(UITheme.muted("Estimated Revenue"));
        revBadge.add(revenueLabel);
        revBadge.add(stockLabel);

        
        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setBorder(UITheme.sectionBorder("Sale Details"));

        JPanel fields = new JPanel(new GridLayout(5, 2, 14, 12));
        fields.setBackground(UITheme.CARD_BG);
        fields.setBorder(new EmptyBorder(12, 14, 4, 14));
        for (JTextField f : new JTextField[]{buyerField, quantityField, priceField})
            f.setPreferredSize(new Dimension(0, 40));
        fields.add(UITheme.label("Select Farmer"));      fields.add(farmerCombo);
        fields.add(UITheme.label("Select Crop"));        fields.add(cropCombo);
        fields.add(UITheme.label("Buyer Name"));         fields.add(buyerField);
        fields.add(UITheme.label("Quantity Sold (kg)")); fields.add(quantityField);
        fields.add(UITheme.label("Unit Price (RWF)"));   fields.add(priceField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        btnRow.setBackground(UITheme.CARD_BG);
        JButton saveBtn    = UITheme.accentBtn("Record Sale");
        JButton refreshBtn = UITheme.ghostBtn("Refresh");
        saveBtn.addActionListener(e -> recordSale());
        refreshBtn.addActionListener(e -> { loadSales(); refresh(); });
        btnRow.add(saveBtn); btnRow.add(refreshBtn);

        JPanel formTop = new JPanel(new BorderLayout(16, 0));
        formTop.setBackground(UITheme.CARD_BG);
        formTop.add(fields, BorderLayout.CENTER);
        formTop.add(revBadge, BorderLayout.EAST);

        formCard.add(formTop, BorderLayout.CENTER);
        formCard.add(btnRow, BorderLayout.SOUTH);

        
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(UITheme.BG);
        searchBar.setBorder(new EmptyBorder(0, 0, 8, 0));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchBar.add(UITheme.label("Search:"), BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.setBorder(UITheme.sectionBorder("Sales Records  (click column header to sort)"));
        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(UITheme.scrollPane(saleTable), BorderLayout.CENTER);

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
                farmerCombo.addItem(new FarmerItem(f.getId(), f.getFullName() + " (" + f.getPhone() + ")"));
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void loadFarmerCrops() {
        cropCombo.removeAllItems(); cropStockMap.clear(); stockLabel.setText("— kg available");
        FarmerItem sel = (FarmerItem) farmerCombo.getSelectedItem();
        if (sel == null) return;
        try {
            List<Harvest> harvests = service.findHarvestsByFarmerId(sel.id);
            List<Sale> sales = service.findSalesByFarmerId(sel.id);
            Map<String, Double> totalH = new HashMap<>(), totalS = new HashMap<>();
            for (Harvest h : harvests) totalH.merge(h.getCropName(), h.getQuantityKg(), Double::sum);
            for (Sale s : sales) totalS.merge(s.getCropName(), s.getQuantitySold(), Double::sum);
            for (Map.Entry<String, Double> e : totalH.entrySet()) {
                double avail = e.getValue() - totalS.getOrDefault(e.getKey(), 0.0);
                if (avail > 0) {
                    cropCombo.addItem(e.getKey() + " (" + String.format("%.1f", avail) + " kg)");
                    cropStockMap.put(e.getKey(), avail);
                }
            }
            cropCombo.setEnabled(cropCombo.getItemCount() > 0);
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void updateStock() {
        String txt = (String) cropCombo.getSelectedItem();
        if (txt == null) { stockLabel.setText("— kg available"); return; }
        Double avail = cropStockMap.get(txt.split(" \\(")[0].trim());
        stockLabel.setText(avail != null ? String.format("%.1f kg available", avail) : "— kg available");
    }

    private void loadSales() {
        try {
            tableModel.setRowCount(0);
            for (Sale s : service.findAllSales())
                tableModel.addRow(new Object[]{
                    "SAL" + String.format("%03d", s.getId()),
                    s.getFarmer().getFullName(),
                    s.getCropName(),
                    s.getBuyerName(),
                    s.getQuantitySold(),
                    s.getUnitPrice(),
                    String.format("%,.0f", s.getTotalRevenue()),
                    s.getSaleDate()
                });
        } catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void calcRevenue() {
        try {
            double qty   = quantityField.getText().trim().isEmpty() ? 0 : Double.parseDouble(quantityField.getText().trim());
            double price = priceField.getText().trim().isEmpty() ? 0 : Double.parseDouble(priceField.getText().trim());
            revenueLabel.setText(String.format("%,.0f RWF", qty * price));
        } catch (NumberFormatException e) { revenueLabel.setText("0 RWF"); }
    }

    private void recordSale() {
        if (farmerCombo.getSelectedItem() == null) { warn("Please select a farmer"); return; }
        if (cropCombo.getSelectedItem() == null)   { warn("Please select a crop"); return; }
        if (buyerField.getText().trim().isEmpty() || quantityField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
            warn("All fields are required"); return;
        }
        try {
            FarmerItem sel = (FarmerItem) farmerCombo.getSelectedItem();
            String cropName = ((String) cropCombo.getSelectedItem()).split(" \\(")[0].trim();
            double qty   = Double.parseDouble(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            if (qty <= 0 || price <= 0) { warn("Quantity and price must be greater than 0"); return; }
            Double avail = cropStockMap.get(cropName);
            if (avail == null || qty > avail) {
                warn("Insufficient stock! Available: " + String.format("%.1f", avail != null ? avail : 0) + " kg"); return;
            }
            Sale sale = new Sale();
            Farmer f = new Farmer(); f.setId(sel.id);
            sale.setFarmer(f); sale.setCropName(cropName); sale.setBuyerName(buyerField.getText().trim());
            sale.setQuantitySold(qty); sale.setUnitPrice(price);
            sale.setTotalRevenue(qty * price); sale.setSaleDate(LocalDate.now());
            Sale saved = service.saveSale(sale);

            
            showReceipt(saved, sel.name, avail - qty);

            buyerField.setText(""); quantityField.setText(""); priceField.setText(""); revenueLabel.setText("0 RWF");
            loadSales(); loadFarmerCrops(); refresh();
        } catch (NumberFormatException e) { warn("Quantity and price must be valid numbers"); }
        catch (RemoteException e) { showError(e.getMessage()); }
    }

    private void showReceipt(Sale sale, String farmerName, double remainingStock) {
        String saleId = "SAL" + String.format("%03d", sale.getId());
        String date   = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        
        String receipt =
            "╔══════════════════════════════════════╗\n" +
            "║     AGRISTOCK & AGRITRACK — RECEIPT  ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "  Receipt No : " + saleId + "\n" +
            "  Date       : " + date + "\n" +
            "╠══════════════════════════════════════╣\n" +
            "  Farmer     : " + farmerName + "\n" +
            "  Buyer      : " + sale.getBuyerName() + "\n" +
            "  Crop       : " + sale.getCropName() + "\n" +
            "╠══════════════════════════════════════╣\n" +
            "  Quantity   : " + String.format("%.1f kg", sale.getQuantitySold()) + "\n" +
            "  Unit Price : " + String.format("%,.0f RWF/kg", sale.getUnitPrice()) + "\n" +
            "  ─────────────────────────────────── \n" +
            "  TOTAL      : " + String.format("%,.0f RWF", sale.getTotalRevenue()) + "\n" +
            "  Remaining  : " + String.format("%.1f kg", remainingStock) + "\n" +
            "╠══════════════════════════════════════╣\n" +
            "  Thank you for using AgriStock & AgriTrack\n" +
            "╚══════════════════════════════════════╝";

        JTextArea textArea = new JTextArea(receipt);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(250, 255, 250));
        textArea.setForeground(UITheme.SLATE_900);
        textArea.setBorder(new EmptyBorder(12, 16, 12, 16));

        JButton printBtn = UITheme.primaryBtn("Print Receipt");
        String printJobName = sanitize(farmerName) + "-" + sanitize(sale.getCropName())
            + "-receipt-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        printBtn.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName(printJobName);
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                    graphics.setFont(new Font("Consolas", Font.PLAIN, 11));
                    graphics.setColor(Color.BLACK);
                    String[] lines = receipt.split("\n");
                    int x = (int) pageFormat.getImageableX() + 10;
                    int y = (int) pageFormat.getImageableY() + 20;
                    for (String line : lines) { graphics.drawString(line, x, y); y += 16; }
                    return Printable.PAGE_EXISTS;
                });
                if (job.printDialog()) job.print();
            } catch (PrinterException ex) { JOptionPane.showMessageDialog(owner(), "Print failed: " + ex.getMessage()); }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(printBtn, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(420, 380));

        JOptionPane.showMessageDialog(owner(), panel, "Sale Receipt — " + saleId, JOptionPane.PLAIN_MESSAGE);
    }

    private String sanitize(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    private Window owner()           { Window w = SwingUtilities.getWindowAncestor(buyerField); return w != null ? w : this; }
    private void warn(String m)      { JOptionPane.showMessageDialog(owner(), m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(owner(), UITheme.cleanErrorMessage(m), "Error", JOptionPane.ERROR_MESSAGE); }
    private void refresh()           { getContentPane().revalidate(); getContentPane().repaint(); }

    private record FarmerItem(int id, String name) {
        @Override public String toString() { return name; }
    }
}
