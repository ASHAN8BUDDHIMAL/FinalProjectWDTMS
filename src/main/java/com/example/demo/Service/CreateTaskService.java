package com.example.demo.Service;

import com.example.demo.model.CreateTask;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.TaskStatusRepo;
import com.example.demo.repository.WorkerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CreateTaskService {

    @Autowired
    private CreateTaskRepo createtaskRepo;

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private RegUser regUser;

    @Autowired
    private TaskStatusRepo taskStatusRepo;


    public CreateTask createTask(Long userId, CreateTask task) {
        task.setUserId(userId);
        task.setStatus("COMPILED");  // Set default status if needed
        return createtaskRepo.save(task);  // ✅ Return the saved task
    }

    public Optional<CreateTask> updateTask(Long userId, Long taskId, CreateTask updates) {
        return createtaskRepo.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .map(t -> {
                    t.setTitle(updates.getTitle());
                    t.setDescription(updates.getDescription());
                    t.setRequiredSkills(updates.getRequiredSkills());
                    t.setAllocatedTime(updates.getAllocatedTime());
                    t.setScheduledDate(updates.getScheduledDate());
                    t.setStatus(updates.getStatus());
                    t.setAllocatedAmount(updates.getAllocatedAmount());
                    t.setWorkerDone(updates.getWorkerDone());
                    return createtaskRepo.save(t); // ✅ use the instance, not a static class
                });
    }

//
//    public List<CreateTask> getTasks(Long userId) {
//        return createtaskRepo.findByUserId(userId);
//    }

    public boolean deleteTask(Long userId, Long taskId) {
        Optional<CreateTask> optionalTask = createtaskRepo.findById(taskId);
        if (optionalTask.isPresent() && optionalTask.get().getUserId().equals(userId)) {
            createtaskRepo.deleteById(taskId);
            return true;
        }
        return false;
    }
    public List<CreateTask> getTasks(Long userId) {
        List<CreateTask> allTasks = createtaskRepo.findByUserId(userId);

        // Get all task IDs that are marked COMPLETED for any worker
        List<Long> completedTaskIds = taskStatusRepo.findTaskIdsWithCompletedStatus();

        // Filter out tasks that are COMPLETED
        List<CreateTask> visibleTasks = new ArrayList<>();
        for (CreateTask task : allTasks) {
            if (!completedTaskIds.contains(task.getId())) {
                visibleTasks.add(task);
            }
        }

        return visibleTasks;
    }




}
