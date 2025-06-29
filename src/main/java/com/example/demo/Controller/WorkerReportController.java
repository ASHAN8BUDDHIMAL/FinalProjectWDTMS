package com.example.demo.Controller;

import com.example.demo.DTO.WorkerReportDTO;
import com.example.demo.Service.PdfService;
import com.example.demo.Service.WorkerReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@CrossOrigin
public class WorkerReportController {

    @Autowired
    private WorkerReportService reportService;

    @Autowired
    private PdfService pdfService;


    @GetMapping("/worker")
    public ResponseEntity<WorkerReportDTO> getWorkerReport(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        WorkerReportDTO report = reportService.generateReportForWorker(userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/worker/pdf")
    public ResponseEntity<byte[]> downloadWorkerReportPdf(@RequestParam(required = false) String month, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        WorkerReportDTO report = reportService.generateReportForWorker(userId);

        byte[] pdf = pdfService.generatePdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename("worker_report_" + LocalDate.now() + ".pdf")
                .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}

