package com.example.demo.Controller;

import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.Service.TaskStatusService;
import com.example.demo.model.TaskStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/task-status")
public class TaskStatusController {

    private final TaskStatusService svc;

    @Autowired
    public TaskStatusController(TaskStatusService svc) {
        this.svc = svc;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TaskStatusRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        req.setUserId(userId); // Pass the session userId to the request

        TaskStatus updatedStatus = svc.updateStatus(req); // Update or create status

        return ResponseEntity.ok(updatedStatus);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ShowStatusDTO>> getTasksForLoggedInWorker(HttpSession session) {
        Long workerId = (Long) session.getAttribute("loggedInUserId");
        if (workerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ShowStatusDTO> tasks = svc.getTasksForWorker(workerId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/worker-update")
    public ResponseEntity<?> workerUpdate(@RequestBody TaskStatusRequest req, HttpSession session) {
        // Get workerId from session (logged-in user)
        Long workerId = (Long) session.getAttribute("loggedInUserId");

        if (workerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Worker not logged in");
        }

        // Only use workerId from session
        TaskStatus updatedStatus = svc.updateTaskStatus(req.getTaskId(), workerId, req.getStatus());

        return ResponseEntity.ok(updatedStatus);
    }

    // View all accepted tasks that need client confirmation
    @GetMapping("/accepted")
    public ResponseEntity<List<ShowStatusDTO>> getAcceptedTasksForClient(HttpSession session) {
        Long clientId = (Long) session.getAttribute("loggedInUserId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ShowStatusDTO> acceptedTasks = svc.getAcceptedTasksForClient(clientId);
        return ResponseEntity.ok(acceptedTasks);
    }

    // Client confirms an accepted task
    @PutMapping("/client-confirm")
    public ResponseEntity<?> confirmTask(@RequestBody TaskStatusRequest req, HttpSession session) {
        Long clientId = (Long) session.getAttribute("loggedInUserId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not logged in");
        }

        try {
            TaskStatus confirmedTask = svc.confirmAcceptedTask(req.getTaskId(), clientId);
            return ResponseEntity.ok(confirmedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}


