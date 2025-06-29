package com.example.demo.Service;

import com.example.demo.DTO.WorkerReportDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;



@Service
public class PdfService {
    public byte[] generatePdf(WorkerReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            var titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            var normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            Paragraph title = new Paragraph("Worker Monthly Report")
                    .setFont(titleFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE)
                    .setMarginBottom(20);
            document.add(title);

            // Worker Info
            Table infoTable = new Table(2).useAllAvailableWidth();
            infoTable.addCell(new Cell().add("Worker Name").setBold());
            infoTable.addCell(report.getWorkerName());
            infoTable.addCell(new Cell().add("Worker ID").setBold());
            infoTable.addCell(String.valueOf(report.getWorkerId()));
            infoTable.addCell(new Cell().add("Generated Date").setBold());
            infoTable.addCell(LocalDate.now().toString());
            document.add(infoTable.setMarginBottom(20));

            // Metrics
            document.add(new Paragraph("Performance Summary")
                    .setFont(titleFont).setFontSize(14).setFontColor(ColorConstants.BLACK).setMarginBottom(10));

            Table metrics = new Table(2).useAllAvailableWidth();
            metrics.addCell(new Cell().add("Completed Tasks").setBold());
            metrics.addCell(String.valueOf(report.getCompletedTasks()));
            metrics.addCell(new Cell().add("Incomplete Tasks").setBold());
            metrics.addCell(String.valueOf(report.getIncompleteTasks()));
            metrics.addCell(new Cell().add("Average Rating").setBold());
            metrics.addCell(String.format("%.1f ★", report.getAverageRating()));
            metrics.addCell(new Cell().add("Total Monthly Income").setBold());
            metrics.addCell("Rs. " + String.format("%.2f", report.getTotalMonthlyIncome()));
            document.add(metrics.setMarginBottom(25));

            // Monthly Income Table
            document.add(new Paragraph("Monthly Income Breakdown")
                    .setFont(titleFont).setFontSize(14).setFontColor(ColorConstants.BLACK).setMarginBottom(10));

            Table incomeTable = new Table(new float[]{2, 2}).useAllAvailableWidth();
            incomeTable.addHeaderCell(new Cell().add("Month").setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            incomeTable.addHeaderCell(new Cell().add("Income (Rs.)").setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());

            for (Map.Entry<String, Double> entry : report.getMonthlyIncome().entrySet()) {
                incomeTable.addCell(new Cell().add(entry.getKey()));
                incomeTable.addCell(new Cell().add(String.format("%.2f", entry.getValue())));
            }
            document.add(incomeTable.setMarginBottom(30));

            document.add(new Paragraph("End of Report")
                    .setFont(normalFont)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(40));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
