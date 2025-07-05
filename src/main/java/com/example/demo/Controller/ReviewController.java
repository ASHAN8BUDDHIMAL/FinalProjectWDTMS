package com.example.demo.Controller;

import com.example.demo.DTO.ReviewResponseDTO;
import com.example.demo.Service.ReviewService;
import com.example.demo.model.Review;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/task/{taskId}/worker/{workerId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long taskId,
            @PathVariable Long workerId,
            @RequestParam(required = false) Integer rating, // ✅ now optional
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "message", "User not logged in"
            ));
        }

        try {
            Review review = reviewService.createReview(taskId, workerId, userId, rating, text, image, session);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "reviewId", review.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/worker/reviews")
    public ResponseEntity<?> getReviewsForLoggedInWorker(HttpSession session) {
        Long workerId = (Long) session.getAttribute("loggedInUserId");

        if (workerId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "message", "Worker not logged in"
            ));
        }

        try {
            List<ReviewResponseDTO> reviews = reviewService.getReviewsForWorker(workerId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "reviews", reviews
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    }




