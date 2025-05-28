package com.example.demo.repository;

import com.example.demo.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepo extends JpaRepository<Worker,Long> {
    Optional<Worker> findByUserId(Long userId);



    @Query("SELECT w.userId FROM Worker w " +
            "WHERE LOWER(w.skills) LIKE LOWER(CONCAT('%', :skill, '%')) " +
            "AND LOWER(w.workCity) = LOWER(:location) " +
            "AND w.rating >= :minRating")
    List<Long> findMatchingRegUserIds(@Param("skill") String skill,
                                      @Param("location") String location,
                                      @Param("minRating") double minRating);
    }


