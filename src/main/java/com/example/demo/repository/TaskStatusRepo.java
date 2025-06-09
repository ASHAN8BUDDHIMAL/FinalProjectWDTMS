package com.example.demo.repository;

import com.example.demo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TaskStatusRepo extends JpaRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByTaskIdAndWorkerId(Long taskId, Long workerId);
    List<TaskStatus> findByWorkerId(Long workerId);
}
