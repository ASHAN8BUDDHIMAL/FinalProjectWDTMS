package com.example.demo.Service;

import com.example.demo.model.Notice;
import com.example.demo.repository.NoticeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepo noticeRepo;

    public Notice createNotice(Notice notice) {
        return noticeRepo.save(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepo.findAll();
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepo.findById(id);
    }

    public Notice updateNotice(Long id, Notice updatedNotice) {
        return noticeRepo.findById(id)
                .map(notice -> {
                    notice.setTitle(updatedNotice.getTitle());
                    notice.setMessage(updatedNotice.getMessage());
                    return noticeRepo.save(notice);
                })
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public void deleteNotice(Long id) {
        noticeRepo.deleteById(id);
    }
}
