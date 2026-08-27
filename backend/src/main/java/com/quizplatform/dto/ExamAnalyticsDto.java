package com.quizplatform.dto;

/**
 * DTO for exam-level analytics data.
 */
public class ExamAnalyticsDto {
    private Long examId;
    private String examTitle;
    private Long totalAttempts;
    private Long completedAttempts;
    private Long passedAttempts;
    private Long failedAttempts;
    private Double averageScore;
    private Double averagePercentage;
    private Double highestScore;
    private Double lowestScore;
    private Double passRate;

    public ExamAnalyticsDto() {}

    // Getters and Setters
    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
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
}
