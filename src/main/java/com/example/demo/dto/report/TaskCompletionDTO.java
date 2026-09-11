package com.example.demo.dto.report;

import java.util.List;

public class TaskCompletionDTO {

    private int totalTasks;
    private int todoTasks;
    private int inProgressTasks;
    private int completedTasks;
    private int overdueTasks;
    private double overallCompletionRate;

    private int highPriorityCount;
    private int mediumPriorityCount;
    private int lowPriorityCount;

    private List<MonthlyTrendPoint> monthlyTrends;

    public TaskCompletionDTO() {
    }

    public static class MonthlyTrendPoint {
        private String month;
        private int completedCount;
        private int createdCount;

        public MonthlyTrendPoint() {
        }

        public MonthlyTrendPoint(String month, int completedCount, int createdCount) {
            this.month = month;
            this.completedCount = completedCount;
            this.createdCount = createdCount;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public int getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(int completedCount) {
            this.completedCount = completedCount;
        }

        public int getCreatedCount() {
            return createdCount;
        }

        public void setCreatedCount(int createdCount) {
            this.createdCount = createdCount;
        }
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(int todoTasks) {
        this.todoTasks = todoTasks;
    }

    public int getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(int inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(int overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public double getOverallCompletionRate() {
        return overallCompletionRate;
    }

    public void setOverallCompletionRate(double overallCompletionRate) {
        this.overallCompletionRate = overallCompletionRate;
    }

    public int getHighPriorityCount() {
        return highPriorityCount;
    }

    public void setHighPriorityCount(int highPriorityCount) {
        this.highPriorityCount = highPriorityCount;
    }

    public int getMediumPriorityCount() {
        return mediumPriorityCount;
    }

    public void setMediumPriorityCount(int mediumPriorityCount) {
        this.mediumPriorityCount = mediumPriorityCount;
    }

    public int getLowPriorityCount() {
        return lowPriorityCount;
    }

    public void setLowPriorityCount(int lowPriorityCount) {
        this.lowPriorityCount = lowPriorityCount;
    }

    public List<MonthlyTrendPoint> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<MonthlyTrendPoint> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }
}
