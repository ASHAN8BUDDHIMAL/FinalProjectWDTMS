package com.example.demo.repository;

import com.example.demo.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepo extends JpaRepository<Location, Long> {

    // For worker location (1 per worker)
    Optional<Location> findByUserId(Long userId);

    // Optional: all locations of a user if needed
    List<Location> findAllByUserId(Long userId);
}
