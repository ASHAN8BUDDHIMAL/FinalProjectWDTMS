package com.example.demo.Service;


import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;

    public Review saveReview(Review review) {
        Review savedReview = reviewRepo.save(review);
        updateAverageRating(savedReview.getWorkerId());
        return savedReview;
    }

    public List<Review> getReviewsByWorkerId(Long workerId) {
        return reviewRepo.findByWorkerId(workerId);
    }

    private void updateAverageRating(Long workerId) {
        Double avg = reviewRepo.calculateAverageRatingByWorkerId(workerId);
        if (avg != null) {
            List<Review> reviews = reviewRepo.findByWorkerId(workerId);
            for (Review r : reviews) {
                r.setAverageRating(avg);
            }
            reviewRepo.saveAll(reviews);
        }
    }
}
