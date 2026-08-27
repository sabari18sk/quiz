package com.quizplatform.service;

import com.quizplatform.dto.AnswerRequest;
import com.quizplatform.dto.AttemptResponse;
import com.quizplatform.dto.StartAttemptRequest;
import com.quizplatform.entity.*;
import com.quizplatform.enums.AttemptStatus;
import com.quizplatform.enums.ExamStatus;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.exception.UnauthorizedException;
import com.quizplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing exam attempts.
 */
@Service
@Transactional
public class ExamAttemptService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    /**
     * Start a new exam attempt for the current user.
     */
    public AttemptResponse startAttempt(Long userId, StartAttemptRequest request) {
        // Find and validate exam
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + request.getExamId()));

        // Check if exam is published
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new BadRequestException("Only published exams can be attempted");
        }

        // Check for existing active attempts
        Optional<ExamAttempt> existingAttempt = examAttemptRepository
                .findByUserIdAndExamIdAndStatus(userId, exam.getId(), AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            throw new BadRequestException("You already have an active attempt for this exam");
        }

        // Create new attempt
        ExamAttempt attempt = new ExamAttempt();
        User user = new User();
        user.setId(userId);
        attempt.setUser(user);
        attempt.setExam(exam);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());

        // Set expiration time if duration is specified
        if (exam.getDurationMinutes() != null && exam.getDurationMinutes() > 0) {
            attempt.setExpiresAt(attempt.getStartedAt().plusMinutes(exam.getDurationMinutes()));
        }

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);
        return AttemptResponse.fromEntity(savedAttempt);
    }

    /**
     * Get the current attempt for a user and exam.
     */
    @Transactional(readOnly = true)
    public AttemptResponse getCurrentAttempt(Long userId, Long examId) {
        ExamAttempt attempt = examAttemptRepository
                .findByUserIdAndExamIdAndStatus(userId, examId, AttemptStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active attempt found for this exam"));

        return AttemptResponse.fromEntity(attempt);
    }

    /**
     * Get all attempts for a user.
     */
    @Transactional(readOnly = true)
    public List<AttemptResponse> getUserAttempts(Long userId) {
        List<ExamAttempt> attempts = examAttemptRepository.findByUserId(userId);
        return attempts.stream()
                .map(AttemptResponse::fromEntity)
                .toList();
    }

    /**
     * Submit an answer for a question in the current attempt.
     */
    public AttemptResponse submitAnswer(Long userId, Long attemptId, AnswerRequest request) {
        // Find and validate attempt
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        // Verify ownership
        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have access to this attempt");
        }

        // Check attempt status
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot submit answers to a completed or expired attempt");
        }

        // Check if attempt has expired
        if (attempt.getExpiresAt() != null && LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            attempt.setStatus(AttemptStatus.EXPIRED);
            examAttemptRepository.save(attempt);
            throw new BadRequestException("Attempt has expired");
        }

        // Find and validate question
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + request.getQuestionId()));

        // Verify question belongs to the exam
        boolean questionBelongsToExam = examRepository.existsByIdAndExamQuestionsExamIdAndExamQuestionsQuestionId(
                attempt.getExam().getId(), attempt.getExam().getId(), question.getId());

        if (!questionBelongsToExam) {
            throw new BadRequestException("This question does not belong to the exam");
        }

        // Find and validate option
        QuestionOption option = questionOptionRepository.findById(request.getSelectedOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + request.getSelectedOptionId()));

        // Verify option belongs to the question
        if (!option.getQuestion().getId().equals(question.getId())) {
            throw new BadRequestException("This option does not belong to the specified question");
        }

        // Find existing answer or create new one
        Optional<Answer> existingAnswer = answerRepository
                .findByExamAttemptIdAndQuestionId(attemptId, question.getId());

        Answer answer;
        if (existingAnswer.isPresent()) {
            // Update existing answer
            answer = existingAnswer.get();
            answer.setSelectedOption(option);
        } else {
            // Create new answer
            answer = new Answer();
            answer.setExamAttempt(attempt);
            answer.setQuestion(question);
            
            // Find ExamQuestion relationship
            ExamQuestion examQuestion = examRepository.findExamQuestionByExamIdAndQuestionId(
                    attempt.getExam().getId(), question.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exam question relationship not found"));
            
            answer.setExamQuestion(examQuestion);
            answer.setSelectedOption(option);
        }

        answerRepository.save(answer);

        // Reload attempt to get updated answers
        ExamAttempt updatedAttempt = examAttemptRepository.findById(attemptId).get();
        return AttemptResponse.fromEntity(updatedAttempt);
    }

    /**
     * Submit the entire attempt for evaluation.
     */
    public AttemptResponse submitAttempt(Long userId, Long attemptId) {
        // Find and validate attempt
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        // Verify ownership
        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have access to this attempt");
        }

        // Check attempt status
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Attempt is already submitted or expired");
        }

        // Check if attempt has expired
        if (attempt.getExpiresAt() != null && LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            attempt.setStatus(AttemptStatus.EXPIRED);
            attempt.setSubmittedAt(LocalDateTime.now());
            examAttemptRepository.save(attempt);
            throw new BadRequestException("Attempt has expired and been auto-submitted");
        }

        // Mark attempt as submitted
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);
        return AttemptResponse.fromEntity(savedAttempt);
    }

    /**
     * Check and expire any overdue attempts.
     */
    @Transactional
    public void expireOverdueAttempts() {
        List<ExamAttempt> inProgressAttempts = examAttemptRepository.findByStatus(AttemptStatus.IN_PROGRESS);
        LocalDateTime now = LocalDateTime.now();

        for (ExamAttempt attempt : inProgressAttempts) {
            if (attempt.getExpiresAt() != null && now.isAfter(attempt.getExpiresAt())) {
                attempt.setStatus(AttemptStatus.EXPIRED);
                attempt.setSubmittedAt(now);
                examAttemptRepository.save(attempt);
            }
        }
    }
}
