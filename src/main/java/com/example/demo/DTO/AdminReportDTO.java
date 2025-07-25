package com.example.demo.DTO;

import java.time.LocalDate;
import java.util.Map;

public class AdminReportDTO {

    private int year;
    private Map<String, Integer[]> userStats; // "clients" and "workers"
    private Integer[] completedTasks;
    private Map<String, Integer> cityDistribution;
    private LocalDate generatedDate = LocalDate.now();

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Map<String, Integer[]> getUserStats() {
        return userStats;
    }

    public void setUserStats(Map<String, Integer[]> userStats) {
        this.userStats = userStats;
    }

    public Integer[] getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer[] completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Map<String, Integer> getCityDistribution() {
        return cityDistribution;
    }

    public void setCityDistribution(Map<String, Integer> cityDistribution) {
        this.cityDistribution = cityDistribution;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }
}
