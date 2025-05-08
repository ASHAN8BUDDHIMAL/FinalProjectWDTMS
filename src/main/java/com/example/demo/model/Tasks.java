package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    private String title;

    private String description;

    private String location;

    private String status; // e.g., "Pending", "Accepted", "Completed"

    private LocalDateTime createdAt;

    private LocalDateTime scheduledTime;

}

