package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByWorkerId(Long workerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.workerId = :workerId")
    Double calculateAverageRatingByWorkerId(@Param("workerId") Long workerId);
}
