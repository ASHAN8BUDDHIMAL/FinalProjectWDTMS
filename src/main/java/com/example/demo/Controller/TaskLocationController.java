package com.example.demo.Controller;
import com.example.demo.Service.TaskLocationService;
import com.example.demo.model.TaskLocation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TaskLocationController {

    @Autowired
    private TaskLocationService tasklocationService;

    @GetMapping("/location/task")
    public TaskLocation getTaskLocation(HttpSession session) {
        Long taskId = (Long) session.getAttribute("createdTaskId");
        if (taskId == null) throw new RuntimeException("Task not found in session");

        return tasklocationService.getTaskLocation(taskId).orElse(null);
    }

    @PutMapping("/location/task")
    public TaskLocation updateTaskLocation(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        Long taskId = (Long) session.getAttribute("createdTaskId");

        if (userId == null) throw new RuntimeException("User not logged in");
        if (taskId == null) throw new RuntimeException("Task not found in session");

        Double lat = ((Number) payload.get("latitude")).doubleValue();
        Double lon = ((Number) payload.get("longitude")).doubleValue();
        String city = (String) payload.get("city");

        return tasklocationService.saveOrUpdateTaskLocation(userId, taskId, lat, lon, city);
    }
}

