package com.quizplatform.service;

import com.quizplatform.dto.ResultDto;
import com.quizplatform.entity.Answer;
import com.quizplatform.entity.Exam;
import com.quizplatform.entity.ExamAttempt;
import com.quizplatform.entity.QuestionOption;
import com.quizplatform.entity.User;
import com.quizplatform.enums.AttemptStatus;
import com.quizplatform.enums.ResultStatus;
import com.quizplatform.enums.UserRole;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.exception.UnauthorizedException;
import com.quizplatform.repository.AnswerRepository;
import com.quizplatform.repository.ExamAttemptRepository;
import com.quizplatform.repository.ExamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;

    public ResultService(ExamAttemptRepository examAttemptRepository,
                         ExamRepository examRepository,
                         AnswerRepository answerRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Get result by attempt ID (for students - own results only)
     */
    @Transactional(readOnly = true)
    public ResultDto getResultByAttemptId(Long attemptId) {
        User currentUser = getCurrentUser();
        
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        // Students can only view their own results
        if (currentUser.getRole() == UserRole.STUDENT && !attempt.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only view your own results");
        }

        if (attempt.getStatus() != AttemptStatus.SUBMITTED) {
            throw new BadRequestException("Result not yet generated for this attempt");
        }

        return mapToResultDto(attempt);
    }

    /**
     * Get all results for the current user (students)
     */
    @Transactional(readOnly = true)
    public Page<ResultDto> getUserResults(Pageable pageable) {
        User currentUser = getCurrentUser();
        
        Page<ExamAttempt> attempts = examAttemptRepository
                .findByUserIdAndStatus(currentUser.getId(), "SUBMITTED", pageable);
        
        return attempts.map(this::mapToResultDto);
    }

    /**
     * Get all results in the system (admin only)
     */
    @Transactional(readOnly = true)
    public Page<ResultDto> getAllResults(Pageable pageable) {
        User currentUser = getCurrentUser();
        
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can view all results");
        }
        
        Page<ExamAttempt> attempts = examAttemptRepository.findByStatus("SUBMITTED", pageable);
        return attempts.map(this::mapToResultDto);
    }

    /**
     * Get results by exam ID (admin only)
     */
    @Transactional(readOnly = true)
    public Page<ResultDto> getResultsByExamId(Long examId, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can view results by exam");
        }
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        
        Page<ExamAttempt> attempts = examAttemptRepository.findByExamIdAndStatus(examId, "SUBMITTED", pageable);
        return attempts.map(this::mapToResultDto);
    }

    /**
     * Map ExamAttempt to ResultDto
     */
    private ResultDto mapToResultDto(ExamAttempt attempt) {
        Exam exam = attempt.getExam();
        User student = attempt.getUser();
        
        // Count answers
        List<Answer> answers = answerRepository.findByExamAttemptId(attempt.getId());
        
        int totalQuestions = exam.getExamQuestions().size();
        int answeredQuestions = answers.size();
        int unansweredQuestions = totalQuestions - answeredQuestions;
        
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        
        for (Answer answer : answers) {
            QuestionOption selectedOption = answer.getSelectedOption();
            if (selectedOption != null && selectedOption.isCorrect()) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
            }
        }
        
        // Calculate scores
        double maxScore = exam.getTotalMarks() != null ? exam.getTotalMarks() : totalQuestions;
        double earnedScore = correctAnswers; // Each correct answer = 1 point by default
        double percentage = maxScore > 0 ? (earnedScore / maxScore) * 100.0 : 0.0;
        
        // Determine result status based on passing score
        ResultStatus resultStatus = ResultStatus.PENDING;
        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            Double passingScore = exam.getPassingScore();
            if (passingScore != null && percentage >= passingScore) {
                resultStatus = ResultStatus.PASS;
            } else if (passingScore != null) {
                resultStatus = ResultStatus.FAIL;
            }
        }
        
        String studentName = student.getFirstName() + " " + student.getLastName();
        
        return ResultDto.builder()
                .id(attempt.getId())
                .examAttemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .userId(student.getId())
                .studentName(studentName)
                .studentEmail(student.getEmail())
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(incorrectAnswers)
                .unansweredQuestions(unansweredQuestions)
                .earnedScore(earnedScore)
                .maxScore(maxScore)
                .percentage(percentage)
                .status(resultStatus)
                .evaluatedAt(attempt.getSubmittedAt())
                .createdAt(attempt.getCreatedAt())
                .build();
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal() == "anonymousUser") {
            throw new UnauthorizedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }
}
