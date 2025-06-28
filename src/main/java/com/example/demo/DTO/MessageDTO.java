package com.example.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class MessageDTO {
    private Long id;
    private Long senderFromId;
    private String senderFromName;
    private Long senderToId;
    private String senderToName;
    private String content;
    private List<String> attachments;
    private LocalDateTime sentAt;
    private boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderFromId() {
        return senderFromId;
    }

    public void setSenderFromId(Long senderFromId) {
        this.senderFromId = senderFromId;
    }

    public String getSenderFromName() {
        return senderFromName;
    }

    public void setSenderFromName(String senderFromName) {
        this.senderFromName = senderFromName;
    }

    public Long getSenderToId() {
        return senderToId;
    }

    public void setSenderToId(Long senderToId) {
        this.senderToId = senderToId;
    }

    public String getSenderToName() {
        return senderToName;
    }

    public void setSenderToName(String senderToName) {
        this.senderToName = senderToName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
