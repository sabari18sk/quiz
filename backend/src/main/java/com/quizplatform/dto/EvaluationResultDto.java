package com.quizplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for evaluation results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultDto {
    
    private Long attemptId;
    private Long examId;
    private String examTitle;
    
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer unansweredQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    
    private Double earnedScore;
    private Double maximumScore;
    private Double percentage;
    
    private Boolean passed;
    private Double passingPercentage;
    
    private String status; // PASS, FAIL, PENDING
}
