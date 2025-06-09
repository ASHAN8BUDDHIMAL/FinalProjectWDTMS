package com.example.demo.Controller;

import com.example.demo.Service.ReviewService;
import com.example.demo.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> submitReview(
            @RequestParam Long userId,
            @RequestParam Long workerId,
            @RequestParam Long taskId,
            @RequestParam int rating,
            @RequestParam String text,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Review review = Review.builder()
                .userId(userId)
                .workerId(workerId)
                .taskId(taskId)
                .rating(rating)
                .text(text)
                .image(image != null ? image.getBytes() : null)
                .build();

        Review saved = reviewService.saveReview(review);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{workerId}")
    public ResponseEntity<List<Review>> getWorkerReviews(@PathVariable Long workerId) {
        return ResponseEntity.ok(reviewService.getReviewsByWorkerId(workerId));
    }
}
