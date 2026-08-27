package com.quizplatform.dto;

/**
 * DTO for admin-level system-wide analytics.
 */
public class AdminAnalyticsDto {
    private Long totalStudents;
    private Long totalExams;
    private Long totalAttempts;
    private Long completedAttempts;
    private Long passedAttempts;
    private Long failedAttempts;
    private Double overallPassRate;
    private Double averageScore;
    private Double averagePercentage;

    public AdminAnalyticsDto() {}

    // Getters and Setters
    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(Long totalExams) {
        this.totalExams = totalExams;
    }

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

    public Double getOverallPassRate() {
        return overallPassRate;
    }

    public void setOverallPassRate(Double overallPassRate) {
        this.overallPassRate = overallPassRate;
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
}
