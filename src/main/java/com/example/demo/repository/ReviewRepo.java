package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {

    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
    List<Review> findByWorkerId(Long workerId);
    List<Review> findByTaskId(Long taskId);
    Optional<Review> findByTaskIdAndWorkerIdAndUserId(Long taskId, Long workerId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.workerId = :workerId")
    Double calculateAverageRatingByWorkerId(@Param("workerId") Long workerId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.workerId = :workerId GROUP BY r.rating")
    List<Object[]> countReviewsByRatingForWorker(@Param("workerId") Long workerId);

    @Query("SELECT r FROM Review r WHERE r.workerId = :workerId ORDER BY r.createdAt DESC")
    List<Review> findLatestReviewsByWorkerId(@Param("workerId") Long workerId);
}