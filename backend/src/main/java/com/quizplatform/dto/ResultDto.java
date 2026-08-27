package com.quizplatform.dto;

import com.quizplatform.enums.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    private Long id;
    private Long examAttemptId;
    private Long examId;
    private String examTitle;
    private Long userId;
    private String studentName;
    private String studentEmail;
    
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer unansweredQuestions;
    
    private Double earnedScore;
    private Double maxScore;
    private Double percentage;
    
    private ResultStatus status;
    private LocalDateTime evaluatedAt;
    private LocalDateTime createdAt;
}
