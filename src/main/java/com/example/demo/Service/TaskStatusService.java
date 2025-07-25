package com.example.demo.Service;

import com.example.demo.DTO.ShowStatusDTO;
import com.example.demo.DTO.TaskStatusRequest;
import com.example.demo.model.BusySlot;
import com.example.demo.model.CreateTask;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepo taskStatusRepo;

    @Autowired
    private CreateTaskRepo createTaskRepo;

    @Autowired
    private RegUser regUser;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskLocationRepo taskLocationRepo;

    @Autowired
    private BusySlotRepo busySlotRepo;


    public TaskStatus updateStatus(TaskStatusRequest request) {
        Optional<TaskStatus> optionalStatus = taskStatusRepo.findByTaskIdAndWorkerId(
                request.getTaskId(), request.getWorkerId());

        TaskStatus status;

        if (optionalStatus.isPresent()) {
            status = optionalStatus.get();
            status.setStatus(request.getStatus());
            status.setUserId(request.getUserId());
        } else {
            status = new TaskStatus();
            status.setUserId(request.getUserId());
            status.setTaskId(request.getTaskId());
            status.setWorkerId(request.getWorkerId());
            status.setStatus(request.getStatus());
        }

        TaskStatus savedStatus = taskStatusRepo.save(status);

        // ✅ Send email if status is "ASSIGNED"
        if ("ASSIGNED".equalsIgnoreCase(request.getStatus())) {
            Optional<UserRegistration> workerOpt = regUser.findById(request.getWorkerId());
            Optional<CreateTask> taskOpt = createTaskRepo.findById(request.getTaskId());

            if (workerOpt.isPresent() && taskOpt.isPresent()) {
                UserRegistration worker = workerOpt.get();
                CreateTask task = taskOpt.get();

                String workerName = worker.getFirstName() + " " + worker.getLastName();
                String email = worker.getEmail();
                String date = task.getScheduledDate() != null ? task.getScheduledDate().toString() : "N/A";

                emailService.sendTaskAssignmentEmail(email, workerName, task.getTitle(), task.getDescription(), date);
            }
        }

        return savedStatus;
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

    public List<ShowStatusDTO> getAllTasksForClient(Long clientId) {
        // 1. Get all tasks created by this client
        List<CreateTask> clientTasks = createTaskRepo.findByUserId(clientId);
        if (clientTasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Get all task IDs
        List<Long> taskIds = clientTasks.stream()
                .map(CreateTask::getId)
                .collect(Collectors.toList());

        // 3. Find ALL statuses for these tasks
        List<TaskStatus> allStatuses = taskStatusRepo.findByTaskIdIn(taskIds);

        // 4. Collect all user IDs (workers + client)
        Set<Long> userIds = allStatuses.stream()
                .map(TaskStatus::getWorkerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        userIds.add(clientId); // Ensure client is included

        // 5. Fetch all users in one query
        Map<Long, UserRegistration> usersMap = regUser.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(UserRegistration::getId, Function.identity()));

        // 6. Build the DTOs
        return allStatuses.stream()
                .map(status -> {
                    ShowStatusDTO dto = new ShowStatusDTO();
                    CreateTask task = clientTasks.stream()
                            .filter(t -> t.getId().equals(status.getTaskId()))
                            .findFirst()
                            .orElse(null);

                    // Set task details
                    if (task != null) {
                        dto.setTaskId(task.getId());
                        dto.setTitle(task.getTitle());
                        dto.setDescription(task.getDescription());
                        dto.setRequiredSkills(task.getRequiredSkills());
                        dto.setMinRating(task.getMinRating());
                        dto.setScheduledDate(task.getScheduledDate());
                        dto.setAllocatedAmount(task.getAllocatedAmount());
                        dto.setUserId(task.getUserId());
                    }

                    // Set status info
                    dto.setStatus(status.getStatus());
                    dto.setWorkerId(status.getWorkerId());

                    // Set worker details (if exists)
                    UserRegistration worker = usersMap.get(status.getWorkerId());
                    if (worker != null) {
                        dto.setWorkerFirstName(worker.getFirstName());
                        dto.setWorkerLastName(worker.getLastName());
                    }

                    // Set client details (NEW)
                    UserRegistration client = usersMap.get(clientId);
                    if (client != null) {
                        dto.setFirstName(client.getFirstName()); // Set client's first name
                        dto.setLastName(client.getLastName());   // Set client's last name
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Client confirms the accepted task
    public TaskStatus confirmTask(Long taskId, Long workerId) {
        // 1. Find the specific ACCEPTED status record
        TaskStatus status = taskStatusRepo
                .findByTaskIdAndWorkerIdAndStatus(taskId, workerId, "ACCEPTED")
                .orElseThrow(() -> new TaskConfirmationException(
                        "No acceptable task found for confirmation. " +
                                "Either: (1) Task doesn't exist, (2) Worker didn't accept it, or " +
                                "(3) It was already confirmed"
                ));

        // 2. Update status if needed (idempotent operation)
        if (!"CONFIRMED".equals(status.getStatus())) {
            status.setStatus("CONFIRMED");
            TaskStatus updatedStatus = taskStatusRepo.save(status);

            // 3. Save the corresponding busy slot now that task is confirmed
            saveBusySlotsFromConfirmedTasks();

            return updatedStatus;
        }

        return status;
    }

    // Custom exception for better error handling
    public static class TaskConfirmationException extends RuntimeException {
        public TaskConfirmationException(String message) {
            super(message);
        }
    }

    // Add these methods to your TaskStatusService class

    public TaskStatus completeTask(Long taskId, Long workerId) {
        // 1. Find the specific CONFIRMED status record
        TaskStatus status = taskStatusRepo
                .findByTaskIdAndWorkerIdAndStatus(taskId, workerId, "CONFIRMED")
                .orElseThrow(() -> new TaskStatusChangeException(
                        "No confirmed task found for completion. " +
                                "Either: (1) Task doesn't exist, (2) Worker didn't confirm it, or " +
                                "(3) It was already completed/incompleted"
                ));

        // 2. Update status
        status.setStatus("COMPLETED");
        return taskStatusRepo.save(status);
    }

    public TaskStatus incompleteTask(Long taskId, Long workerId) {
        // 1. Find the specific CONFIRMED status record
        TaskStatus status = taskStatusRepo
                .findByTaskIdAndWorkerIdAndStatus(taskId, workerId, "CONFIRMED")
                .orElseThrow(() -> new TaskStatusChangeException(
                        "No confirmed task found for marking incomplete. " +
                                "Either: (1) Task doesn't exist, (2) Worker didn't confirm it, or " +
                                "(3) It was already completed/incompleted"
                ));

        // 2. Update status
        status.setStatus("INCOMPLETED");
        return taskStatusRepo.save(status);
    }

    // Custom exception for status changes
    public static class TaskStatusChangeException extends RuntimeException {
        public TaskStatusChangeException(String message) {
            super(message);
        }
    }


    public void saveBusySlotsFromConfirmedTasks() {
        // 1. Get all confirmed assignments
        List<TaskStatus> confirmedStatuses = taskStatusRepo.findByStatus("CONFIRMED");

        for (TaskStatus status : confirmedStatuses) {
            Long taskId = status.getTaskId();
            Long workerId = status.getWorkerId();
            Long userId = status.getUserId(); // this is the client

            // 2. Get task by ID
            Optional<CreateTask> optionalTask = createTaskRepo.findById(taskId);

            if (optionalTask.isPresent()) {
                CreateTask task = optionalTask.get();

                BusySlot busySlot = new BusySlot();
                busySlot.setTaskId(task.getId());
                busySlot.setTitle(task.getTitle());
                busySlot.setDate(task.getScheduledDate().toLocalDate());
                busySlot.setStartTime(task.getScheduledDate().toLocalTime());
                busySlot.setEndTime(LocalTime.of(17, 0)); // Default end time
                busySlot.setClientId(userId);
                busySlot.setWorkerId(workerId);

                // 3. Load client name using client ID
                regUser.findById(userId).ifPresent(user -> {
                    busySlot.setClientFirstName(user.getFirstName());
                    busySlot.setClientLastName(user.getLastName());
                });

                // 4. Load task location if available
                taskLocationRepo.findByTaskId(taskId).ifPresent(loc -> {
                    busySlot.setTaskCity(loc.getCity());
//                    busySlot.setLatitude(loc.getLatitude());
//                    busySlot.setLongitude(loc.getLongitude());
                });

                // 5. Save to repository
                busySlotRepo.save(busySlot);
            } else {
                System.out.println("Task not found for ID: " + taskId);
            }
        }
    }


}
