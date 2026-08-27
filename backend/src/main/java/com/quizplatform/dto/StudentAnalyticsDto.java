package com.quizplatform.dto;

import java.util.List;

/**
 * DTO for student-specific analytics data.
 */
public class StudentAnalyticsDto {
    private Long totalAttempts;
    private Long completedAttempts;
    private Long passedAttempts;
    private Long failedAttempts;
    private Double averageScore;
    private Double averagePercentage;
    private Double highestScore;
    private Double lowestScore;
    private Double passRate;
    private List<PerformanceHistoryItem> performanceHistory;

    public StudentAnalyticsDto() {}

    // Getters and Setters
    public Long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Long getCompletedAttempts() {
        return completedAttempts;
    }

    public void setCompletedAttempts(Long completedAttempts) {
        this.completedAttempts = completedAttempts;
    }

    public Long getPassedAttempts() {
        return passedAttempts;
    }

    public void setPassedAttempts(Long passedAttempts) {
        this.passedAttempts = passedAttempts;
    }

    public Long getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Long failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(Double averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public Double getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Double lowestScore) {
        this.lowestScore = lowestScore;
    }

    public Double getPassRate() {
        return passRate;
    }

    public void setPassRate(Double passRate) {
        this.passRate = passRate;
    }

    public List<PerformanceHistoryItem> getPerformanceHistory() {
        return performanceHistory;
    }

    public void setPerformanceHistory(List<PerformanceHistoryItem> performanceHistory) {
        this.performanceHistory = performanceHistory;
    }

    /**
     * Inner class for performance history items.
     */
    public static class PerformanceHistoryItem {
        private String examTitle;
        private Double score;
        private Double percentage;
        private String status;
        private String submittedAt;

        public PerformanceHistoryItem() {}

        public PerformanceHistoryItem(String examTitle, Double score, Double percentage, String status, String submittedAt) {
            this.examTitle = examTitle;
            this.score = score;
            this.percentage = percentage;
            this.status = status;
            this.submittedAt = submittedAt;
        }

        // Getters and Setters
        public String getExamTitle() {
            return examTitle;
        }

        public void setExamTitle(String examTitle) {
            this.examTitle = examTitle;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubmittedAt() {
            return submittedAt;
        }

        public void setSubmittedAt(String submittedAt) {
            this.submittedAt = submittedAt;
        }
    }
}
