package com.example.demo.Service;

import com.example.demo.model.TaskLocation;
import com.example.demo.repository.TaskLocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TaskLocationService {

    @Autowired
    private TaskLocationRepo tasklocationRepo;

    // Save or update task location (1 per task)
    public TaskLocation saveOrUpdateTaskLocation(Long userId, Long taskId, Double lat, Double lon, String city) {
        Optional<TaskLocation> existing = tasklocationRepo.findByTaskId(taskId);
        TaskLocation tasklocation = existing.orElse(new TaskLocation());
        tasklocation.setUserId(userId);
        tasklocation.setTaskId(taskId); // Important
        tasklocation.setLatitude(lat);
        tasklocation.setLongitude(lon);
        tasklocation.setCity(city);
        return tasklocationRepo.save(tasklocation);
    }

    public Optional<TaskLocation> getTaskLocation(Long taskId) {
        return tasklocationRepo.findByTaskId(taskId);
    }
}
