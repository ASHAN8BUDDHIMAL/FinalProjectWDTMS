package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "terms_conditions")
public class TermsAndConditions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private LocalDateTime lastUpdated;

    // ✅ No-arg constructor required by JPA
    public TermsAndConditions() {}

    // ✅ Constructor with content
    public TermsAndConditions(String content) {
        this.content = content;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
