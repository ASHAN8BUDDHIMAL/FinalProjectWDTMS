package com.example.demo.Service;

import com.example.demo.DTO.AdminReportDTO;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.TaskStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminReportService {

    @Autowired
    private RegUser userRepo;

    @Autowired
    private TaskStatusRepo taskRepo;

    public AdminReportDTO generateReport(int year) {
        Integer[] clients = new Integer[12];
        Integer[] workers = new Integer[12];
        Arrays.fill(clients, 0);
        Arrays.fill(workers, 0);

        for (int month = 1; month <= 12; month++) {
            clients[month - 1] = userRepo.countByUserTypeAndCreatedAtMonth("customer", year, month);
            workers[month - 1] = userRepo.countByUserTypeAndCreatedAtMonth("worker", year, month);
        }

        Map<String, Integer[]> userStats = new HashMap<>();
        userStats.put("clients", clients);
        userStats.put("workers", workers);

        Integer[] completedTasks = new Integer[12];
        Arrays.fill(completedTasks, 0);
        for (Object[] row : taskRepo.countCompletedTasksByMonth(year)) {
            int month = (int) row[0];
            int count = ((Number) row[1]).intValue();
            completedTasks[month - 1] = count;
        }

        Map<String, Integer> cityDistribution = new HashMap<>();
        for (Object[] row : userRepo.countUsersByCity()) {
            String city = (String) row[0];
            int count = ((Number) row[1]).intValue();
            cityDistribution.put(city, count);
        }

        AdminReportDTO report = new AdminReportDTO();
        report.setYear(year);
        report.setUserStats(userStats);
        report.setCompletedTasks(completedTasks);
        report.setCityDistribution(cityDistribution);
        return report;
    }


}
