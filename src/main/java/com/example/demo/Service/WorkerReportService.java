package com.example.demo.Service;


import com.example.demo.DTO.WorkerReportDTO;
import com.example.demo.model.CreateTask;
import com.example.demo.model.Review;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.CreateTaskRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.ReviewRepo;
import com.example.demo.repository.TaskStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;


@Service
public class WorkerReportService {

    @Autowired
    private TaskStatusRepo taskStatusRepo;

    @Autowired
    private CreateTaskRepo createTaskRepo;

    @Autowired
    private RegUser regUser;

    @Autowired
    private ReviewRepo reviewRepo;

    public WorkerReportDTO generateReportForWorker(Long workerId) {
        WorkerReportDTO dto = new WorkerReportDTO();
        dto.setWorkerId(workerId);


        // 1. Get all task statuses for the worker
        List<TaskStatus> statuses = taskStatusRepo.findByWorkerId(workerId);
        List<Long> taskIds = statuses.stream().map(TaskStatus::getTaskId).distinct().toList();

        // 2. Fetch related tasks
        List<CreateTask> tasks = createTaskRepo.findAllById(taskIds);
        Map<Long, CreateTask> taskMap = tasks.stream()
                .collect(Collectors.toMap(CreateTask::getId, task -> task));

        // 3. Monthly Income Calculation
        Map<String, Double> monthlyIncome = new HashMap<>();
        double totalIncome = 0.0;
        int completed = 0;
        int incomplete = 0;
        double avarageIncome = 0.0;


        for (TaskStatus status : statuses) {
            if (taskMap.containsKey(status.getTaskId())) {
                CreateTask task = taskMap.get(status.getTaskId());

                if ("COMPLETED".equalsIgnoreCase(status.getStatus())) {
                    completed++;
                    String month = task.getScheduledDate().getMonth()
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    double amount = task.getAllocatedAmount();
                    monthlyIncome.put(month, monthlyIncome.getOrDefault(month, 0.0) + amount);
                    totalIncome += amount;
                    avarageIncome = totalIncome/completed;
                } else if ("INCOMPLETED".equalsIgnoreCase(status.getStatus())) {
                    incomplete++;
                }
            }
        }

        dto.setMonthlyIncome(monthlyIncome);
        dto.setTotalMonthlyIncome(totalIncome);
        dto.setCompletedTasks(completed);
        dto.setAvarageIncome(avarageIncome);
        dto.setIncompleteTasks(incomplete);

        // 4. Worker Name
        Optional<UserRegistration> workerOpt = regUser.findById(workerId);
        if (workerOpt.isPresent()) {
            UserRegistration user = workerOpt.get();
            dto.setWorkerName(user.getFirstName() + " " + user.getLastName());
        } else {
            dto.setWorkerName("Unknown");
        }

        // 5. Average Rating
        List<Review> reviews = reviewRepo.findByWorkerId(workerId);
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        dto.setAverageRating(averageRating);

        return dto;
    }
}
