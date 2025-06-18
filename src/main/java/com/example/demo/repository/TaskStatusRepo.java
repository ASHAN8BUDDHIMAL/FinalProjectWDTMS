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

    // Find all TaskStatus by taskId and status (used internally)
    List<TaskStatus> findByTaskIdAndStatus(Long taskId, String status);

    // Find all TaskStatus with status 'ACCEPTED' for a list of tasks (client's tasks)
    List<TaskStatus> findByTaskIdInAndStatus(List<Long> taskIds, String status);

    // Find task status by taskId (used for confirmation)
    Optional<TaskStatus> findByTaskId(Long taskId);
}
