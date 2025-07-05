package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskAssignmentEmail(String toEmail, String workerName, String taskTitle, String description, String date) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Task Assigned: " + taskTitle);
        message.setText("Hello " + workerName + ",\n\n" +
                "You have been assigned a new task:\n" +
                "Title: " + taskTitle + "\n" +
                "Description: " + description + "\n" +
                "Scheduled Date: " + date + "\n\n" +
                "Please log in to your account to view more details.\n\n" +
                "Regards,\nWDTMS System");
        mailSender.send(message);
    }
}
