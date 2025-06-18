package com.example.demo.Service;

import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.model.CreateTask;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.TaskStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepo taskStatusRepo;

    @Autowired
    private CreateTaskRepo createTaskRepo;

    @Autowired
    private RegUser regUser;

    public TaskStatus updateStatus(TaskStatusRequest request) {
        Optional<TaskStatus> optionalStatus = taskStatusRepo.findByTaskIdAndWorkerId(
                request.getTaskId(), request.getWorkerId());

        TaskStatus status;

        if (optionalStatus.isPresent()) {
            // Update existing status
            status = optionalStatus.get();
            status.setStatus(request.getStatus());
            status.setUserId(request.getUserId()); // Set session userId in update
        } else {
            // Create new TaskStatus if not found
            status = new TaskStatus();
            status.setUserId(request.getUserId()); // Set session userId in create
            status.setTaskId(request.getTaskId());
            status.setWorkerId(request.getWorkerId());
            status.setStatus(request.getStatus());
        }

        return taskStatusRepo.save(status);

    }

    /**
     * Retrieve task details and client info for all statuses assigned to a worker.
     */
    public List<ShowStatusDTO> getTasksForWorker(Long workerId) {
        List<TaskStatus> taskStatuses = taskStatusRepo.findByWorkerId(workerId);

        return taskStatuses.stream()
                .map(status -> {
                    Optional<CreateTask> taskOpt = createTaskRepo.findById(status.getTaskId());
                    if (taskOpt.isEmpty()) {
                        return null;
                    }
                    CreateTask task = taskOpt.get();

                    Optional<UserRegistration> userOpt = regUser.findById(task.getUserId());
                    if (userOpt.isEmpty()) {
                        return null;
                    }
                    UserRegistration user = userOpt.get();

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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public TaskStatus updateTaskStatus(Long taskId, Long workerId, String newStatus) {
        // Find task by taskId and workerId
        Optional<TaskStatus> optionalStatus = taskStatusRepo.findByTaskIdAndWorkerId(taskId, workerId);

        if (optionalStatus.isPresent()) {
            TaskStatus status = optionalStatus.get();
            status.setStatus(newStatus);  // Change to ACCEPTED or REJECTED
            return taskStatusRepo.save(status);
        } else {
            throw new RuntimeException("Task not found for this worker");
        }
    }

    public List<ShowStatusDTO> getAcceptedTasksForClient(Long clientId) {
        // Get all tasks created by this client
        List<CreateTask> clientTasks = createTaskRepo.findByUserId(clientId);
        List<Long> clientTaskIds = clientTasks.stream().map(CreateTask::getId).collect(Collectors.toList());

        if (clientTaskIds.isEmpty()) {
            return List.of();
        }

        // Find all ACCEPTED statuses for this client’s tasks
        List<TaskStatus> acceptedStatuses = taskStatusRepo.findByTaskIdInAndStatus(clientTaskIds, "ACCEPTED");

        return acceptedStatuses.stream()
                .map(this::buildShowStatusDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // Client confirms the accepted task
    public TaskStatus confirmAcceptedTask(Long taskId, Long clientId) {
        Optional<TaskStatus> optionalStatus = taskStatusRepo.findByTaskId(taskId);

        if (optionalStatus.isPresent()) {
            TaskStatus status = optionalStatus.get();

            CreateTask task = createTaskRepo.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));

            if (!task.getUserId().equals(clientId)) {
                throw new RuntimeException("Unauthorized: This task does not belong to this client.");
            }

            if (!status.getStatus().equals("ACCEPTED")) {
                throw new RuntimeException("Task must be in ACCEPTED status to confirm.");
            }

            status.setStatus("CONFIRMED");
            return taskStatusRepo.save(status);
        } else {
            throw new RuntimeException("Task status not found.");
        }
    }

    // Build DTO from TaskStatus
    private ShowStatusDTO buildShowStatusDTO(TaskStatus status) {
        Optional<CreateTask> taskOpt = createTaskRepo.findById(status.getTaskId());
        if (taskOpt.isEmpty()) return null;

        CreateTask task = taskOpt.get();

        Optional<UserRegistration> userOpt = regUser.findById(task.getUserId());
        if (userOpt.isEmpty()) return null;

        UserRegistration user = userOpt.get();

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
    }


}
