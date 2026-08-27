package com.quizplatform.service;

import com.quizplatform.dto.EvaluationResultDto;
import com.quizplatform.entity.Answer;
import com.quizplatform.entity.ExamAttempt;
import com.quizplatform.entity.ExamQuestion;
import com.quizplatform.entity.QuestionOption;
import com.quizplatform.enums.AttemptStatus;
import com.quizplatform.enums.ResultStatus;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.repository.AnswerRepository;
import com.quizplatform.repository.ExamAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for evaluating exam attempts.
 */
@Service
@Transactional
public class EvaluationService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    /**
     * Evaluate a submitted exam attempt and calculate scores.
     */
    public EvaluationResultDto evaluateAttempt(Long attemptId) {
        // Find and validate attempt
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        // Verify attempt is submitted
        if (attempt.getStatus() != AttemptStatus.SUBMITTED) {
            throw new BadRequestException("Only submitted attempts can be evaluated. Current status: " + attempt.getStatus());
        }

        // Prevent re-evaluation if already scored
        if (attempt.getScore() != null) {
            // Return existing evaluation
            return buildEvaluationResult(attempt);
        }

        // Get all answers for this attempt
        List<Answer> answers = answerRepository.findByExamAttemptId(attemptId);

        // Calculate metrics
        int totalQuestions = attempt.getExam().getExamQuestions().size();
        int answeredQuestions = answers.size();
        int unansweredQuestions = totalQuestions - answeredQuestions;
        int correctAnswers = 0;
        int incorrectAnswers = 0;

        double earnedScore = 0.0;
        double maximumScore = 0.0;

        // Evaluate each answer
        for (Answer answer : answers) {
            ExamQuestion examQuestion = answer.getExamQuestion();
            maximumScore += examQuestion.getMarks();

            QuestionOption selectedOption = answer.getSelectedOption();
            QuestionOption correctOption = getCorrectOption(examQuestion.getQuestion());

            if (selectedOption != null && correctOption != null) {
                if (selectedOption.getId().equals(correctOption.getId())) {
                    correctAnswers++;
                    earnedScore += examQuestion.getMarks();
                } else {
                    incorrectAnswers++;
                }
            } else if (selectedOption == null) {
                // No answer selected - counted as unanswered
                incorrectAnswers++;
            } else {
                incorrectAnswers++;
            }
        }

        // Calculate percentage
        double percentage = 0.0;
        if (maximumScore > 0) {
            percentage = (earnedScore / maximumScore) * 100.0;
        }

        // Determine pass/fail
        boolean passed = false;
        Double passingPercentage = attempt.getExam().getPassingScore();
        if (passingPercentage != null) {
            passed = percentage >= passingPercentage;
        } else {
            // Default to 50% if no passing score defined
            passed = percentage >= 50.0;
            passingPercentage = 50.0;
        }

        // Update attempt with scores
        attempt.setScore(earnedScore);
        attempt.setTotalMarks(maximumScore);
        examAttemptRepository.save(attempt);

        // Build and return result
        return buildEvaluationResultWithDetails(
                attempt, 
                totalQuestions, 
                answeredQuestions, 
                unansweredQuestions, 
                correctAnswers, 
                incorrectAnswers, 
                earnedScore, 
                maximumScore, 
                percentage, 
                passed, 
                passingPercentage
        );
    }

    /**
     * Get the correct option for a question.
     */
    private QuestionOption getCorrectOption(com.quizplatform.entity.Question question) {
        List<QuestionOption> options = question.getOptions();
        if (options == null || options.isEmpty()) {
            return null;
        }
        
        return options.stream()
                .filter(option -> option.isCorrect())
                .findFirst()
                .orElse(null);
    }

    /**
     * Build evaluation result DTO from existing attempt data.
     */
    private EvaluationResultDto buildEvaluationResult(ExamAttempt attempt) {
        // For already evaluated attempts, we need to recalculate the details
        List<Answer> answers = answerRepository.findByExamAttemptId(attempt.getId());
        int totalQuestions = attempt.getExam().getExamQuestions().size();
        int answeredQuestions = answers.size();
        int unansweredQuestions = totalQuestions - answeredQuestions;
        
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        
        for (Answer answer : answers) {
            ExamQuestion examQuestion = answer.getExamQuestion();
            QuestionOption selectedOption = answer.getSelectedOption();
            QuestionOption correctOption = getCorrectOption(examQuestion.getQuestion());
            
            if (selectedOption != null && correctOption != null && 
                selectedOption.getId().equals(correctOption.getId())) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
            }
        }
        
        double percentage = 0.0;
        if (attempt.getTotalMarks() != null && attempt.getTotalMarks() > 0) {
            percentage = (attempt.getScore() / attempt.getTotalMarks()) * 100.0;
        }
        
        boolean passed = false;
        Double passingPercentage = attempt.getExam().getPassingScore();
        if (passingPercentage != null) {
            passed = attempt.getScore() >= (attempt.getTotalMarks() * passingPercentage / 100.0);
        } else {
            passed = percentage >= 50.0;
            passingPercentage = 50.0;
        }
        
        ResultStatus status = passed ? ResultStatus.PASS : ResultStatus.FAIL;
        
        EvaluationResultDto result = new EvaluationResultDto();
        result.setAttemptId(attempt.getId());
        result.setExamId(attempt.getExam().getId());
        result.setExamTitle(attempt.getExam().getTitle());
        result.setTotalQuestions(totalQuestions);
        result.setAnsweredQuestions(answeredQuestions);
        result.setUnansweredQuestions(unansweredQuestions);
        result.setCorrectAnswers(correctAnswers);
        result.setIncorrectAnswers(incorrectAnswers);
        result.setEarnedScore(attempt.getScore());
        result.setMaximumScore(attempt.getTotalMarks());
        result.setPercentage(percentage);
        result.setPassed(passed);
        result.setPassingPercentage(passingPercentage);
        result.setStatus(status.name());
        
        return result;
    }

    /**
     * Build evaluation result DTO with full details.
     */
    private EvaluationResultDto buildEvaluationResultWithDetails(
            ExamAttempt attempt,
            int totalQuestions,
            int answeredQuestions,
            int unansweredQuestions,
            int correctAnswers,
            int incorrectAnswers,
            double earnedScore,
            double maximumScore,
            double percentage,
            boolean passed,
            double passingPercentage
    ) {
        ResultStatus status = passed ? ResultStatus.PASS : ResultStatus.FAIL;
        
        EvaluationResultDto result = new EvaluationResultDto();
        result.setAttemptId(attempt.getId());
        result.setExamId(attempt.getExam().getId());
        result.setExamTitle(attempt.getExam().getTitle());
        result.setTotalQuestions(totalQuestions);
        result.setAnsweredQuestions(answeredQuestions);
        result.setUnansweredQuestions(unansweredQuestions);
        result.setCorrectAnswers(correctAnswers);
        result.setIncorrectAnswers(incorrectAnswers);
        result.setEarnedScore(earnedScore);
        result.setMaximumScore(maximumScore);
        result.setPercentage(percentage);
        result.setPassed(passed);
        result.setPassingPercentage(passingPercentage);
        result.setStatus(status.name());
        
        return result;
    }
}
