package com.example.demo.repository;

import com.example.demo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TaskStatusRepo extends JpaRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByTaskIdAndWorkerId(Long taskId, Long workerId);
    List<TaskStatus> findByWorkerId(Long workerId);

    // Find all TaskStatus with status 'ACCEPTED' for a list of tasks (client's tasks)
    List<TaskStatus> findByTaskIdInAndStatusIn(List<Long> taskIds, List<String> statuses);

    // Find task status by taskId (used for confirmation)
    List<TaskStatus> findByTaskIdIn(List<Long> taskIds);

    Optional<TaskStatus> findByTaskIdAndWorkerIdAndStatus(Long taskId, Long workerId, String status);

    @Query("SELECT DISTINCT ts.taskId FROM TaskStatus ts WHERE ts.status = 'COMPLETED'")
    List<Long> findTaskIdsWithCompletedStatus();

    @Query("SELECT MONTH(t.updatedAt), COUNT(t) FROM TaskStatus t WHERE t.status = 'Completed' AND YEAR(t.updatedAt) = :year GROUP BY MONTH(t.updatedAt)")
    List<Object[]> countCompletedTasksByMonth(int year);

    List<TaskStatus> findByStatus(String status);  // Gets all records with given status
}
