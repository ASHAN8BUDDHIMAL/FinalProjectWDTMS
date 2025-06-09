package com.example.demo.Service;

import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.CreateTask;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.TaskStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepo taskStatusRepo;

    @Autowired
    private CreateTaskRepo createTaskRepo;

    @Autowired
    private RegUser regUser;

    // Save task status (ASSIGNED/ACCEPTED/REJECTED)
    public TaskStatus updateStatus(TaskStatusRequest request) {
        TaskStatus status = new TaskStatus();
        status.setUserId(request.getUserId());
        status.setTaskId(request.getTaskId());
        status.setWorkerId(request.getWorkerId());
        status.setStatus(request.getStatus());
        return taskStatusRepo.save(status);
    }

    // Get all tasks assigned to a specific worker with task + user details
    public List<ShowStatusDTO> getTasksForWorker(Long workerId) {
        return taskStatusRepo.findByWorkerId(workerId)
                .stream()
                .map(status -> {
                    CreateTask task = createTaskRepo.findById(status.getTaskId()).orElse(null);
                    if (task == null) return null;

                    UserRegistration user = regUser.findById(task.getUserId()).orElse(null);
                    if (user == null) return null;

                    ShowStatusDTO dto = new ShowStatusDTO();
                    dto.setTaskId(task.getId());
                    dto.setTitle(task.getTitle());
                    dto.setDescription(task.getDescription());
                    dto.setRequiredSkills(task.getRequiredSkills());
                    dto.setMinRating(task.getMinRating());
                    dto.setScheduledDate(task.getScheduledDate());
                    dto.setAllocatedAmount(task.getAllocatedAmount());
                    dto.setStatus(status.getStatus());

                    dto.setUserId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());

                    return dto;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
