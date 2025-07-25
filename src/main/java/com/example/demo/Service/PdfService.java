package com.example.demo.Service;

import com.example.demo.DTO.AdminReportDTO;
import com.example.demo.DTO.WorkerReportDTO;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generatePdf(WorkerReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String html = generateHtml(report);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private String generateHtml(WorkerReportDTO report) {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; padding: 20px; }")
                .append("h1 { color: #007acc; text-align: center; margin-bottom: 0; }")
                .append("h2 { color: #333; margin-top: 10px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }")
                .append("th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }")
                .append("th { background-color: #007acc; color: white; }")
                .append("tbody tr:nth-child(even) { background-color: #f9f9f9; }")
                .append("footer { margin-top: 40px; text-align: center; color: gray; font-size: 12px; }")
                .append("</style></head><body>");

        html.append("<h1>FindWorker</h1>");
        html.append("<h2>Worker Monthly Report</h2>");

        // Summary table
        html.append("<table>");
        html.append("<thead><tr>")
                .append("<th>Worker Name</th>")
                .append("<th>Completed Tasks</th>")
                .append("<th>Average Rating</th>")
                .append("<th>Total Monthly Income (Rs.)</th>")
                .append("<th>Report Generated On</th>")
                .append("</tr></thead><tbody>");
        html.append("<tr>")
                .append("<td>").append(report.getWorkerName()).append("</td>")
                .append("<td>").append(report.getCompletedTasks()).append("</td>")
                .append("<td>").append(String.format("%.1f ★", report.getAverageRating())).append("</td>")
                .append("<td>").append(String.format("%.2f", report.getTotalMonthlyIncome())).append("</td>")
                .append("<td>").append(LocalDate.now()).append("</td>")
                .append("</tr>");
        html.append("</tbody></table>");

        // Monthly income breakdown table
        html.append("<h2>Monthly Income Breakdown</h2>");
        html.append("<table><thead><tr><th>Month</th><th>Income (Rs.)</th></tr></thead><tbody>");
        for (Map.Entry<String, Double> entry : report.getMonthlyIncome().entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>")
                    .append(String.format("%.2f", entry.getValue())).append("</td></tr>");
        }
        html.append("</tbody></table>");

        html.append("<footer>End of Report</footer>");
        html.append("</body></html>");

        return html.toString();
    }


    // Your existing admin report PDF generator
    public byte[] generateAdminReportPdf(AdminReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String html = generateAdminHtml(report);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Admin PDF generation failed", e);
        }
    }

    private String generateAdminHtml(AdminReportDTO report) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; padding: 20px; }")
                .append("h1 { color: #007acc; text-align: center; }")
                .append("h2 { color: #333; margin-top: 30px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 13px; }")
                .append("th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }")
                .append("th { background-color: #007acc; color: white; }")
                .append("tbody tr:nth-child(even) { background-color: #f9f9f9; }")
                .append("footer { margin-top: 40px; text-align: center; font-size: 12px; color: gray; }")
                .append("</style></head><body>");

        html.append("<h1>WDTMS Admin Report</h1>");
        html.append("<p><b>Year:</b> ").append(report.getYear()).append(" | <b>Generated on:</b> ").append(java.time.LocalDate.now()).append("</p>");

        // Section 1: Clients & Workers
        html.append("<h2>Clients and Workers per Month</h2>");
        html.append("<table><thead><tr><th>Month</th><th>Clients</th><th>Workers</th></tr></thead><tbody>");
        for (int i = 0; i < 12; i++) {
            html.append("<tr><td>").append(java.time.Month.of(i + 1).name()).append("</td>")
                    .append("<td>").append(report.getUserStats().get("clients")[i]).append("</td>")
                    .append("<td>").append(report.getUserStats().get("workers")[i]).append("</td></tr>");
        }
        html.append("</tbody></table>");

        // Section 2: Completed Tasks
        html.append("<h2>Completed Tasks per Month</h2>");
        html.append("<table><thead><tr><th>Month</th><th>Completed Tasks</th></tr></thead><tbody>");
        for (int i = 0; i < 12; i++) {
            html.append("<tr><td>").append(java.time.Month.of(i + 1).name()).append("</td>")
                    .append("<td>").append(report.getCompletedTasks()[i]).append("</td></tr>");
        }
        html.append("</tbody></table>");

        // Section 3: City Distribution
        html.append("<h2>User Distribution by City</h2>");
        html.append("<table><thead><tr><th>City</th><th>Total Users</th></tr></thead><tbody>");
        for (Map.Entry<String, Integer> entry : report.getCityDistribution().entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }
        html.append("</tbody></table>");

        html.append("<footer>Generated by WDTMS Admin Dashboard | All rights reserved.</footer>");
        html.append("</body></html>");

        return html.toString();
    }


}
