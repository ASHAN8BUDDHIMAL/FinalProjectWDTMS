package com.example.demo.repository;

import com.example.demo.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    Optional<Location> findByUserId(Long userId);
}
