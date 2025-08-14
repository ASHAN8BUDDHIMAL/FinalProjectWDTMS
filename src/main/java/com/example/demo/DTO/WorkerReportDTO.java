package com.example.demo.DTO;

import lombok.Data;

import java.util.Map;
@Data
public class WorkerReportDTO {
    private Long workerId;
    private String workerName;
    private double totalMonthlyIncome;
    private Map<String, Double> monthlyIncome; // e.g., "JANUARY" -> 12000.0
    private int completedTasks;
    private int incompleteTasks;
    private double averageRating;
    private double avarageIncome;


    public double getAvarageIncome() {
        return avarageIncome;
    }

    public void setAvarageIncome(double avarageIncome) {
        this.avarageIncome = avarageIncome;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public double getTotalMonthlyIncome() {
        return totalMonthlyIncome;
    }

    public void setTotalMonthlyIncome(double totalMonthlyIncome) {
        this.totalMonthlyIncome = totalMonthlyIncome;
    }

    public Map<String, Double> getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Map<String, Double> monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getIncompleteTasks() {
        return incompleteTasks;
    }

    public void setIncompleteTasks(int incompleteTasks) {
        this.incompleteTasks = incompleteTasks;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
