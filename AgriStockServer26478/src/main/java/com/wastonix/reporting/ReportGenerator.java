package com.wastonix.reporting;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import com.wastonix.config.HibernateUtil;
import com.wastonix.model.Harvest;
import com.wastonix.model.Sale;
import org.hibernate.Session;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.util.List;

public class ReportGenerator {

    public static String exportHarvestsToCSV(Integer farmerId) {
        String filePath = "harvest_report_" + (farmerId > 0 ? farmerId : "all") + ".csv";
        CSVFormat csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .setHeader("ID", "Farmer Name", "Phone", "Crop", "Quantity(kg)", "Grade", "Harvest Date")
                .build();

        try (Session session = HibernateUtil.getSessionFactory().openSession();
             CSVPrinter printer = new CSVPrinter(new FileWriter(filePath), csvFormat)) {

            List<Harvest> list;
            if (farmerId > 0) {
                list = session.createQuery("SELECT h FROM Harvest h JOIN FETCH h.farmer WHERE h.farmer.id=:fid", Harvest.class)
                        .setParameter("fid", farmerId).getResultList();
            } else {
                list = session.createQuery("SELECT h FROM Harvest h JOIN FETCH h.farmer ORDER BY h.farmer.fullName", Harvest.class)
                        .getResultList();
            }

            for (Harvest h : list) {
                printer.printRecord("HAR" + String.format("%03d", h.getId()), h.getFarmer().getFullName(), h.getFarmer().getPhone(),
                        h.getCropName(), h.getQuantityKg(), h.getQualityGrade(), h.getHarvestDate());
            }
            return filePath;
        } catch (Exception e) {
            System.err.println("CSV Export Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public static byte[] generateHarvestReportPDF(Integer farmerId) {
        Document document = null;
        ByteArrayOutputStream outputStream = null;

        try {
            outputStream = new ByteArrayOutputStream();
            document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, outputStream);

            
            document.open();

            
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
            PdfPCell titleCell = new PdfPCell(new Phrase("AgriStock & AgriTrack Report", titleFont));
            titleCell.setBackgroundColor(new Color(0, 102, 51));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setPadding(15);
            titleCell.setBorder(Rectangle.NO_BORDER);

            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.addCell(titleCell);
            document.add(titleTable);

            
            document.add(Chunk.NEWLINE);
            Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            document.add(new Paragraph("Generated: " + java.time.LocalDate.now().toString(), infoFont));
            document.add(new Paragraph("Scope: " + (farmerId > 0 ? "Farmer ID: FAR" + String.format("%03d", farmerId) : "All Farmers"), infoFont));
            document.add(Chunk.NEWLINE);

            
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
            PdfPCell harvestHeader = new PdfPCell(new Phrase("HARVEST RECORDS", sectionFont));
            harvestHeader.setBackgroundColor(new Color(0, 120, 215));
            harvestHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            harvestHeader.setPadding(10);
            harvestHeader.setBorder(Rectangle.NO_BORDER);

            PdfPTable harvestHeaderTable = new PdfPTable(1);
            harvestHeaderTable.setWidthPercentage(100);
            harvestHeaderTable.setSpacingBefore(10);
            harvestHeaderTable.addCell(harvestHeader);
            document.add(harvestHeaderTable);

            
            PdfPTable harvestTable = new PdfPTable(6);
            harvestTable.setWidthPercentage(100);
            harvestTable.setWidths(new float[]{1, 3, 2, 2, 2, 2});
            harvestTable.setSpacingAfter(10);

            
            String[] headers = {"ID", "Farmer", "Crop", "Qty (kg)", "Grade", "Date"};
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                harvestTable.addCell(cell);
            }

            
            Session session = HibernateUtil.getSessionFactory().openSession();
            List<Harvest> harvests;
            if (farmerId > 0) {
                harvests = session.createQuery(
                        "SELECT h FROM Harvest h JOIN FETCH h.farmer WHERE h.farmer.id=:fid",
                        Harvest.class).setParameter("fid", farmerId).getResultList();
            } else {
                harvests = session.createQuery(
                        "SELECT h FROM Harvest h JOIN FETCH h.farmer ORDER BY h.farmer.fullName",
                        Harvest.class).getResultList();
            }

            double totalQty = 0;
            boolean alternate = false;
            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
            for (Harvest h : harvests) {
                alternate = !alternate;
                Color bgColor = alternate ? Color.WHITE : new Color(245, 245, 245);

                harvestTable.addCell(createCell("HAR" + String.format("%03d", h.getId()), bgColor, Element.ALIGN_CENTER, cellFont));
                harvestTable.addCell(createCell(h.getFarmer().getFullName(), bgColor, Element.ALIGN_LEFT, cellFont));
                harvestTable.addCell(createCell(h.getCropName(), bgColor, Element.ALIGN_LEFT, cellFont));
                harvestTable.addCell(createCell(String.format("%.2f", h.getQuantityKg()), bgColor, Element.ALIGN_RIGHT, cellFont));
                harvestTable.addCell(createCell(h.getQualityGrade(), bgColor, Element.ALIGN_CENTER, cellFont));
                harvestTable.addCell(createCell(h.getHarvestDate().toString(), bgColor, Element.ALIGN_CENTER, cellFont));

                totalQty += h.getQuantityKg();
            }
            session.close();
            document.add(harvestTable);

            
            document.add(Chunk.NEWLINE);
            Font summaryFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0, 102, 51));
            document.add(new Paragraph("Total Harvest: " + String.format("%.2f kg", totalQty), summaryFont));

            
            document.add(Chunk.NEWLINE);
            PdfPCell salesHeader = new PdfPCell(new Phrase("SALES RECORDS", sectionFont));
            salesHeader.setBackgroundColor(new Color(0, 120, 215));
            salesHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesHeader.setPadding(10);
            salesHeader.setBorder(Rectangle.NO_BORDER);

            PdfPTable salesHeaderTable = new PdfPTable(1);
            salesHeaderTable.setWidthPercentage(100);
            salesHeaderTable.setSpacingBefore(10);
            salesHeaderTable.addCell(salesHeader);
            document.add(salesHeaderTable);

            
            PdfPTable salesTable = new PdfPTable(7);
            salesTable.setWidthPercentage(100);
            salesTable.setWidths(new float[]{1, 2, 2, 2, 2, 2, 2});
            salesTable.setSpacingAfter(10);

            String[] salesHeaders = {"ID", "Farmer", "Crop", "Buyer", "Qty", "Price", "Revenue"};
            for (String header : salesHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                salesTable.addCell(cell);
            }

            session = HibernateUtil.getSessionFactory().openSession();
            List<Sale> sales;
            if (farmerId > 0) {
                sales = session.createQuery(
                        "SELECT s FROM Sale s JOIN FETCH s.farmer WHERE s.farmer.id=:fid",
                        Sale.class).setParameter("fid", farmerId).getResultList();
            } else {
                sales = session.createQuery(
                        "SELECT s FROM Sale s JOIN FETCH s.farmer ORDER BY s.farmer.fullName",
                        Sale.class).getResultList();
            }

            double totalRevenue = 0;
            alternate = false;
            for (Sale s : sales) {
                alternate = !alternate;
                Color bgColor = alternate ? Color.WHITE : new Color(245, 245, 245);

                salesTable.addCell(createCell("SAL" + String.format("%03d", s.getId()), bgColor, Element.ALIGN_CENTER, cellFont));
                salesTable.addCell(createCell(s.getFarmer().getFullName(), bgColor, Element.ALIGN_LEFT, cellFont));
                salesTable.addCell(createCell(s.getCropName(), bgColor, Element.ALIGN_LEFT, cellFont));
                salesTable.addCell(createCell(s.getBuyerName(), bgColor, Element.ALIGN_LEFT, cellFont));
                salesTable.addCell(createCell(String.format("%.2f", s.getQuantitySold()), bgColor, Element.ALIGN_RIGHT, cellFont));
                salesTable.addCell(createCell(String.format("%,d", s.getUnitPrice().intValue()), bgColor, Element.ALIGN_RIGHT, cellFont));
                salesTable.addCell(createCell(String.format("%,d RWF", s.getTotalRevenue().intValue()),
                        new Color(220, 255, 220), Element.ALIGN_RIGHT, new Font(Font.HELVETICA, 9, Font.BOLD, new Color(0, 80, 0))));

                totalRevenue += s.getTotalRevenue();
            }
            session.close();
            document.add(salesTable);

            
            document.add(Chunk.NEWLINE);
            Font revenueFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 102, 51));
            document.add(new Paragraph("Total Revenue: " + String.format("%,d RWF", (long)totalRevenue), revenueFont));

            
            document.close();

            
            byte[] pdfBytes = outputStream.toByteArray();
            System.out.println("✅ PDF Generated: " + pdfBytes.length + " bytes");
            return pdfBytes;

        } catch (Exception e) {
            System.err.println("❌ PDF Generation Error: " + e.getMessage());
            e.printStackTrace();
            return new byte[0];
        } finally {
            
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    private static PdfPCell createCell(String text, Color bgColor, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}