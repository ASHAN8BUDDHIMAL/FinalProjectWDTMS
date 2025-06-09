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
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/task-status")
public class TaskStatusController {
    @Autowired
    private final TaskStatusService svc;

    public TaskStatusController(TaskStatusService svc) {
        this.svc = svc;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TaskStatusRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        req.setUserId(userId); // ✅ set it into the DTO
        TaskStatus saved = svc.updateStatus(req);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{workerId}")
    public ResponseEntity<List<ShowStatusDTO>> getTasksForWorker(@PathVariable Long workerId) {
        List<ShowStatusDTO> tasks = svc.getTasksForWorker(workerId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/auth/session")
    public ResponseEntity<Map<String, Object>> getSessionUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        }
        return ResponseEntity.ok(Map.of("userId", userId));
    }


}
