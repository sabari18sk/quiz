package com.quizplatform.service;

import com.quizplatform.dto.AdminAnalyticsDto;
import com.quizplatform.dto.ExamAnalyticsDto;
import com.quizplatform.dto.StudentAnalyticsDto;
import com.quizplatform.entity.Answer;
import com.quizplatform.entity.Exam;
import com.quizplatform.entity.ExamAttempt;
import com.quizplatform.entity.User;
import com.quizplatform.enums.AttemptStatus;
import com.quizplatform.enums.ResultStatus;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.exception.UnauthorizedException;
import com.quizplatform.repository.AnswerRepository;
import com.quizplatform.repository.ExamAttemptRepository;
import com.quizplatform.repository.ExamRepository;
import com.quizplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    /**
     * Get analytics for the current student (by user ID).
     */
    public StudentAnalyticsDto getStudentAnalytics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<ExamAttempt> attempts = examAttemptRepository.findByUserId(userId);

        if (attempts.isEmpty()) {
            return createEmptyStudentAnalytics();
        }

        List<ExamAttempt> completedAttempts = attempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED)
                .collect(Collectors.toList());

        long passedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.PASS)
                .count();

        long failedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.FAIL)
                .count();

        // Calculate scores
        List<Double> scores = completedAttempts.stream()
                .map(ExamAttempt::getScoreObtained)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averageScore = scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double highestScore = scores.isEmpty() ? 0.0 : Collections.max(scores);
        double lowestScore = scores.isEmpty() ? 0.0 : Collections.min(scores);

        // Calculate percentages
        List<Double> percentages = completedAttempts.stream()
                .map(ExamAttempt::getPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averagePercentage = percentages.isEmpty() ? 0.0 : percentages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Pass rate
        double passRate = completedAttempts.isEmpty() ? 0.0 : (passedCount * 100.0) / completedAttempts.size();

        // Performance history
        List<StudentAnalyticsDto.PerformanceHistoryItem> history = completedAttempts.stream()
                .sorted(Comparator.comparing(ExamAttempt::getSubmittedAt).reversed())
                .limit(10)
                .map(attempt -> {
                    Exam exam = attempt.getExam();
                    return new StudentAnalyticsDto.PerformanceHistoryItem(
                            exam != null ? exam.getTitle() : "Unknown Exam",
                            attempt.getScoreObtained(),
                            attempt.getPercentage(),
                            attempt.getResultStatus() != null ? attempt.getResultStatus().name() : "PENDING",
                            attempt.getSubmittedAt() != null ? attempt.getSubmittedAt().toString() : null
                    );
                })
                .collect(Collectors.toList());

        StudentAnalyticsDto dto = new StudentAnalyticsDto();
        dto.setTotalAttempts((long) attempts.size());
        dto.setCompletedAttempts((long) completedAttempts.size());
        dto.setPassedAttempts(passedCount);
        dto.setFailedAttempts(failedCount);
        dto.setAverageScore(averageScore);
        dto.setAveragePercentage(averagePercentage);
        dto.setHighestScore(highestScore);
        dto.setLowestScore(lowestScore);
        dto.setPassRate(passRate);
        dto.setPerformanceHistory(history);

        return dto;
    }

    /**
     * Get analytics for a specific exam (admin/instructor access).
     */
    public ExamAnalyticsDto getExamAnalytics(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        List<ExamAttempt> attempts = examAttemptRepository.findByExamId(examId);

        if (attempts.isEmpty()) {
            return createEmptyExamAnalytics(examId, exam.getTitle());
        }

        List<ExamAttempt> completedAttempts = attempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED)
                .collect(Collectors.toList());

        long passedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.PASS)
                .count();

        long failedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.FAIL)
                .count();

        List<Double> scores = completedAttempts.stream()
                .map(ExamAttempt::getScoreObtained)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averageScore = scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double highestScore = scores.isEmpty() ? 0.0 : Collections.max(scores);
        double lowestScore = scores.isEmpty() ? 0.0 : Collections.min(scores);

        List<Double> percentages = completedAttempts.stream()
                .map(ExamAttempt::getPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averagePercentage = percentages.isEmpty() ? 0.0 : percentages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double passRate = completedAttempts.isEmpty() ? 0.0 : (passedCount * 100.0) / completedAttempts.size();

        ExamAnalyticsDto dto = new ExamAnalyticsDto();
        dto.setExamId(examId);
        dto.setExamTitle(exam.getTitle());
        dto.setTotalAttempts((long) attempts.size());
        dto.setCompletedAttempts((long) completedAttempts.size());
        dto.setPassedAttempts(passedCount);
        dto.setFailedAttempts(failedCount);
        dto.setAverageScore(averageScore);
        dto.setAveragePercentage(averagePercentage);
        dto.setHighestScore(highestScore);
        dto.setLowestScore(lowestScore);
        dto.setPassRate(passRate);

        return dto;
    }

    /**
     * Get system-wide analytics (admin only).
     */
    public AdminAnalyticsDto getAdminAnalytics() {
        long totalStudents = userRepository.countByRole(com.quizplatform.enums.UserRole.STUDENT);
        long totalExams = examRepository.count();
        List<ExamAttempt> allAttempts = examAttemptRepository.findAll();

        List<ExamAttempt> completedAttempts = allAttempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED)
                .collect(Collectors.toList());

        long passedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.PASS)
                .count();

        long failedCount = completedAttempts.stream()
                .filter(a -> a.getResultStatus() == ResultStatus.FAIL)
                .count();

        List<Double> scores = completedAttempts.stream()
                .map(ExamAttempt::getScoreObtained)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averageScore = scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        List<Double> percentages = completedAttempts.stream()
                .map(ExamAttempt::getPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        double averagePercentage = percentages.isEmpty() ? 0.0 : percentages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double overallPassRate = completedAttempts.isEmpty() ? 0.0 : (passedCount * 100.0) / completedAttempts.size();

        AdminAnalyticsDto dto = new AdminAnalyticsDto();
        dto.setTotalStudents(totalStudents);
        dto.setTotalExams(totalExams);
        dto.setTotalAttempts((long) allAttempts.size());
        dto.setCompletedAttempts((long) completedAttempts.size());
        dto.setPassedAttempts(passedCount);
        dto.setFailedAttempts(failedCount);
        dto.setOverallPassRate(overallPassRate);
        dto.setAverageScore(averageScore);
        dto.setAveragePercentage(averagePercentage);

        return dto;
    }

    private StudentAnalyticsDto createEmptyStudentAnalytics() {
        StudentAnalyticsDto dto = new StudentAnalyticsDto();
        dto.setTotalAttempts(0L);
        dto.setCompletedAttempts(0L);
        dto.setPassedAttempts(0L);
        dto.setFailedAttempts(0L);
        dto.setAverageScore(0.0);
        dto.setAveragePercentage(0.0);
        dto.setHighestScore(0.0);
        dto.setLowestScore(0.0);
        dto.setPassRate(0.0);
        dto.setPerformanceHistory(new ArrayList<>());
        return dto;
    }

    private ExamAnalyticsDto createEmptyExamAnalytics(Long examId, String title) {
        ExamAnalyticsDto dto = new ExamAnalyticsDto();
        dto.setExamId(examId);
        dto.setExamTitle(title);
        dto.setTotalAttempts(0L);
        dto.setCompletedAttempts(0L);
        dto.setPassedAttempts(0L);
        dto.setFailedAttempts(0L);
        dto.setAverageScore(0.0);
        dto.setAveragePercentage(0.0);
        dto.setHighestScore(0.0);
        dto.setLowestScore(0.0);
        dto.setPassRate(0.0);
        return dto;
    }
}
