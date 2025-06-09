package com.example.demo.repository;

import com.example.demo.model.TaskLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaskLocationRepo extends JpaRepository<TaskLocation, Long> {
    Optional<TaskLocation> findByTaskId(Long taskId);
}

