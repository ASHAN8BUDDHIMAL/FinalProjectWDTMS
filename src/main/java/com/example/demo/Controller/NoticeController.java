package com.example.demo.Controller;

import com.example.demo.Service.NoticeService;
import com.example.demo.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // Create a new notice
    @PostMapping("/create")
    public Notice createNotice(@RequestBody Notice notice) {
        return noticeService.createNotice(notice);
    }

    // Get all notices
    @GetMapping("/Show")
    public List<Notice> getAllNotices() {
        return noticeService.getAllNotices();
    }

    // Get a single notice by ID
    @GetMapping("/{id}")
    public Notice getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    // Edit (update) a notice
    @PutMapping("/{id}")
    public Notice updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        return noticeService.updateNotice(id, notice);
    }

    @DeleteMapping("/{id}")
    public void deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
    }
}
