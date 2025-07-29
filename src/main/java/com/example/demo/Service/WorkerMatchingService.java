package com.example.demo.Service;

import com.example.demo.DTO.MatchedWorkerDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    private final BusySlotRepo busySlotRepo;

    private static final double MAX_DISTANCE_KM = 30.0;

    public List<MatchedWorkerDTO> findMatchedWorkers(Long taskId) {
        CreateTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskLocation taskLocation = taskLocationRepo.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task location not found"));

        LocalDateTime scheduledDateTime = task.getScheduledDate();
        LocalDate taskDate = scheduledDateTime.toLocalDate();
        LocalTime taskStart = scheduledDateTime.toLocalTime();
        int durationHours = task.getAllocatedTime();
        LocalTime taskEnd = taskStart.plusHours(durationHours);

        return workerRepo.findAll().stream()
                .map(worker -> {
                    Optional<UserRegistration> userOpt = regUser.findById(worker.getUserId());
                    if (userOpt.isEmpty()) return null;

                    UserRegistration user = userOpt.get();

                    // Skill match filter (using existing isSkillMatch method)
                    if (!isSkillMatch(worker.getSkills(), task.getRequiredSkills())) {
                        return null;
                    }

                    // Calculate skill match strength (1.0 if any match, 0.6 if basic match, etc.)
                    double skillScore = calculateSkillStrength(worker.getSkills(), task.getRequiredSkills());

                    // Distance calculation (using existing getDistanceToTask method)
                    double distance = getDistanceToTask(worker.getUserId(), taskLocation);
                    if (distance < 0 || distance > MAX_DISTANCE_KM) return null;
                    double distanceScore = 1.0 - (distance / MAX_DISTANCE_KM);

                    // Availability check (using existing isWorkerAvailable method)
                    boolean available = isWorkerAvailable(worker.getId(), taskDate, taskStart, taskEnd);
                    double availabilityScore = available ? 1.0 : 0.0;

                    // New weighted scoring (60% skills, 30% distance, 10% availability)
                    double finalScore = (skillScore * 0.6) + (distanceScore * 0.3) + (availabilityScore * 0.1);

                    MatchedWorkerDTO dto = new MatchedWorkerDTO();
                    dto.setUserId(worker.getUserId());
                    dto.setFullName(user.getFirstName() + " " + user.getLastName());
                    dto.setSkills(worker.getSkills());
                    dto.setEmail(user.getEmail());
                    dto.setAvailable(available);
                    dto.setScore(finalScore);

                    return dto;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(MatchedWorkerDTO::getScore).reversed())
                .collect(Collectors.toList());
    }

    // New helper method to calculate skill strength
    private double calculateSkillStrength(String workerSkills, String requiredSkills) {
        // Count matching skills
        Set<String> workerSkillSet = Arrays.stream(workerSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        long matchingSkills = Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(workerSkillSet::contains)
                .count();

        // Return 1.0 if any match (maintaining existing filter behavior)
        return matchingSkills > 0 ? 1.0 : 0.0;
    }


    private boolean isSkillMatch(String workerSkills, String requiredSkills) {
        Set<String> workerSkillSet = Arrays.stream(workerSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .anyMatch(workerSkillSet::contains);
    }

    private double getDistanceToTask(Long workerUserId, TaskLocation taskLocation) {
        Optional<Location> optional = locationRepo.findByUserId(workerUserId);
        if (optional.isEmpty()) return -1;

        Location loc = optional.get();
        return calculateDistance(taskLocation.getLatitude(), taskLocation.getLongitude(),
                loc.getLatitude(), loc.getLongitude());
    }

    // Add this new method for pure schedule printing
//    public void printScheduleComparison(Long workerId, LocalDate taskDate, LocalTime taskStart, LocalTime taskEnd) {
//        // Input validation
//        if (workerId == null || taskDate == null || taskStart == null || taskEnd == null) {
//            throw new IllegalArgumentException("Parameters must not be null");
//        }
//        if (!taskStart.isBefore(taskEnd)) {
//            throw new IllegalArgumentException("Task start time must be before end time");
//        }
//
//        // Print task schedule
//        System.out.println("\n=== TASK SCHEDULE ===");
//        System.out.println("Date: " + taskDate);
//        System.out.println("Time: " + taskStart + " to " + taskEnd);
//        System.out.println("Duration: " + taskStart.until(taskEnd, ChronoUnit.MINUTES) + " minutes");
//
//        // Get worker's busy slots
//        List<BusySlot> busySlots = busySlotRepo.findByWorkerIdAndDate(workerId, taskDate);
//
//        System.out.println("\n=== WORKER BUSY SCHEDULE ===");
//        System.out.println("Worker ID: " + workerId);
//
//        if (busySlots.isEmpty()) {
//            System.out.println("No busy slots found for this date");
//            return;
//        }
//
//        // Print all busy slots
//        System.out.println("Busy slots on " + taskDate + ":");
//        System.out.println("----------------------------------");
//        System.out.println("| ID  | Start Time | End Time   | Duration | Status      |");
//        System.out.println("----------------------------------");
//
//        for (BusySlot slot : busySlots) {
//            LocalTime busyStart = slot.getStartTime();
//            LocalTime busyEnd = slot.getEndTime();
//
//            String start = busyStart != null ? busyStart.toString() : "NULL";
//            String end = busyEnd != null ? busyEnd.toString() : "NULL";
//            String duration = "N/A";
//            String status = "VALID";
//
//            if (busyStart == null || busyEnd == null) {
//                status = "NULL VALUES";
//            } else if (!busyStart.isBefore(busyEnd)) {
//                status = "INVALID RANGE";
//            } else {
//                duration = busyStart.until(busyEnd, ChronoUnit.MINUTES) + " mins";
//            }
//
//            System.out.printf("| %-4d| %-11s| %-11s| %-9s| %-12s|\n",
//                    slot.getId(), start, end, duration, status);
//        }
//        System.out.println("----------------------------------");
//    }

    private boolean isWorkerAvailable(Long workerId, LocalDate taskDate, LocalTime taskStart, LocalTime taskEnd) {
        // Input validation
        if (workerId == null || taskDate == null || taskStart == null || taskEnd == null) {
            throw new IllegalArgumentException("Worker ID, task date, start time, and end time must not be null.");
        }
        if (!taskStart.isBefore(taskEnd)) {
            throw new IllegalArgumentException("Task start time must be before end time.");
        }

        // Print task schedule
        System.out.println("\n==========================================");
        System.out.println("               SCHEDULE COMPARISON         ");
        System.out.println("==========================================");
        System.out.println("TASK SCHEDULE:");
        System.out.println("------------------------------------------");
        System.out.printf("  • Date:       %s\n", taskDate);
        System.out.printf("  • Time:       %s to %s\n", taskStart, taskEnd);
        System.out.printf("  • Duration:   %d minutes\n", taskStart.until(taskEnd, ChronoUnit.MINUTES));
        System.out.println("------------------------------------------");

        // Get all busy slots for this worker (not just the task date)
        List<BusySlot> allBusySlots = busySlotRepo.findByWorkerId(workerId);

        System.out.println("\nWORKER'S COMPLETE BUSY SCHEDULE:");
        System.out.println("Worker ID: " + workerId);

        if (allBusySlots.isEmpty()) {
            System.out.println("No busy slots found for this worker");
            System.out.println("==========================================\n");
            return true;
        }

        // Print all busy slots grouped by date
        Map<LocalDate, List<BusySlot>> slotsByDate = allBusySlots.stream()
                .collect(Collectors.groupingBy(BusySlot::getDate, TreeMap::new, Collectors.toList()));

        slotsByDate.forEach((date, slots) -> {
            System.out.println("\nDate: " + date);
            System.out.println("----------------------------------");
            System.out.println("| ID  | Start Time | End Time   | Duration | Status      |");
            System.out.println("----------------------------------");

            for (BusySlot slot : slots) {
                LocalTime busyStart = slot.getStartTime();
                LocalTime busyEnd = slot.getEndTime();

                String start = busyStart != null ? busyStart.toString() : "NULL";
                String end = busyEnd != null ? busyEnd.toString() : "NULL";
                String duration = "N/A";
                String status = "VALID";

                if (busyStart == null || busyEnd == null) {
                    status = "NULL VALUES";
                } else if (!busyStart.isBefore(busyEnd)) {
                    status = "INVALID RANGE";
                } else {
                    duration = busyStart.until(busyEnd, ChronoUnit.MINUTES) + " mins";
                }

                System.out.printf("| %-4d| %-11s| %-11s| %-9s| %-12s|\n",
                        slot.getId(), start, end, duration, status);
            }
            System.out.println("----------------------------------");
        });

        // Now check availability for the specific task date
        List<BusySlot> taskDateSlots = busySlotRepo.findByWorkerIdAndDate(workerId, taskDate);
        boolean isAvailable = true;

        System.out.println("\nAVAILABILITY CHECK FOR TASK DATE: " + taskDate);
        if (taskDateSlots.isEmpty()) {
            System.out.println("No conflicts found - worker is available");
        } else {
            for (BusySlot slot : taskDateSlots) {
                LocalTime busyStart = slot.getStartTime();
                LocalTime busyEnd = slot.getEndTime();

                if (busyStart == null || busyEnd == null || !busyStart.isBefore(busyEnd)) {
                    continue;
                }

                if (timesOverlap(taskStart, taskEnd, busyStart, busyEnd)) {
                    System.out.printf("CONFLICT FOUND: %s to %s overlaps with task\n",
                            busyStart, busyEnd);
                    isAvailable = false;
                }
            }
        }

        System.out.println("==========================================\n");
        return isAvailable;
    }

    // Keep the original timesOverlap method
    private boolean timesOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
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
