package com.example.demo.Controller;

import com.example.demo.DTO.AdminReportDTO;
import com.example.demo.Service.AdminReportService;
import com.example.demo.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("api/report")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/data")
    public ResponseEntity<AdminReportDTO> getReportData(@RequestBody Map<String, Integer> request) {
        int year = request.get("year");
        AdminReportDTO report = adminReportService.generateReport(year);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, Integer> request) {
        int year = request.get("year");
        AdminReportDTO report = adminReportService.generateReport(year);
        byte[] pdfBytes = pdfService.generateAdminReportPdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("admin_report_" + year + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


}
