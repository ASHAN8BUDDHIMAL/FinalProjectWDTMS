package com.example.demo.Service;

import com.example.demo.DTO.MatchedWorkerDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerMatchingService {

    private final WorkerRepo workerRepo;
    private final RegUser regUser;
    private final CreateTaskRepo taskRepo;
    private final TaskLocationRepo taskLocationRepo;
    private final LocationRepo locationRepo;

    private static final double MAX_DISTANCE_KM = 30.0;

    public List<MatchedWorkerDTO> findMatchedWorkers(Long taskId) {
        CreateTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskLocation taskLocation = taskLocationRepo.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task location not found"));

        return workerRepo.findAll().stream()
                .filter(worker -> isSkillMatch(worker.getSkills(), task.getRequiredSkills()))
                .filter(worker -> isWithinDistance(worker.getUserId(), taskLocation))
                .map(worker -> {
                    Optional<UserRegistration> userOpt = regUser.findById(worker.getUserId());
                    if (userOpt.isEmpty()) return null;

                    UserRegistration user = userOpt.get();
                    MatchedWorkerDTO dto = new MatchedWorkerDTO();
                    dto.setUserId(worker.getUserId());
                    dto.setFullName(user.getFirstName() + " " + user.getLastName());
                    dto.setSkills(worker.getSkills());
//                    dto.setRating(worker.getRating());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean isSkillMatch(String workerSkills, String requiredSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()) return true;
        if (workerSkills == null || workerSkills.isBlank()) return false;

        return Arrays.stream(requiredSkills.toLowerCase().split(","))
                .anyMatch(skill -> workerSkills.toLowerCase().contains(skill.trim()));
    }

    private boolean isWithinDistance(Long workerUserId, TaskLocation taskLocation) {
        Optional<Location> optional = locationRepo.findByUserId(workerUserId);
        if (optional.isEmpty()) return false;

        Location loc = optional.get();
        double distance = calculateDistance(taskLocation.getLatitude(), taskLocation.getLongitude(),
                loc.getLatitude(), loc.getLongitude());

        return distance <= MAX_DISTANCE_KM;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
