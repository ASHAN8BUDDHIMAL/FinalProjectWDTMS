package com.example.demo.Controller;

import com.example.demo.model.CreateTask;
import com.example.demo.Service.CreateTaskService;
import com.example.demo.repository.RegUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/task")
public class CreateTaskController {

    @Autowired
    private CreateTaskService createtaskService;

    @PostMapping
    public ResponseEntity<CreateTask> createTask(@RequestBody CreateTask task, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        CreateTask createdTask = createtaskService.createTask(userId, task);
        session.setAttribute("createdTaskId", createdTask.getId());//

        return ResponseEntity.ok(createdTask);  // ✅ Return full task object, not just 1
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateTask> updateTask(@PathVariable Long id,
                                                 @RequestBody CreateTask updates,
                                                 HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return createtaskService.updateTask(userId, id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @GetMapping
    public ResponseEntity<List<CreateTask>> listTasks(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<CreateTask> tasks = createtaskService.getTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        boolean deleted = createtaskService.deleteTask(userId, id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }





}
