package com.example.demo.repository;

import com.example.demo.model.CreateTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreateTaskRepo extends JpaRepository<CreateTask, Long> {
    List<CreateTask> findByUserId(Long userId);
}



