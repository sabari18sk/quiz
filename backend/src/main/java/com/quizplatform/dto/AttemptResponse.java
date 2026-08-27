package com.quizplatform.dto;

import com.quizplatform.entity.Answer;
import com.quizplatform.entity.ExamAttempt;
import com.quizplatform.enums.AttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for exam attempt response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {

    private Long id;
    private Long examId;
    private String examTitle;
    private Long userId;
    private AttemptStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime expiresAt;
    private List<AnswerDto> answers;

    /**
     * Creates an AttemptResponse from an ExamAttempt entity.
     */
    public static AttemptResponse fromEntity(ExamAttempt attempt) {
        AttemptResponse response = new AttemptResponse();
        response.setId(attempt.getId());
        response.setExamId(attempt.getExam().getId());
        response.setExamTitle(attempt.getExam().getTitle());
        response.setUserId(attempt.getUser().getId());
        response.setStatus(attempt.getStatus());
        response.setStartedAt(attempt.getStartedAt());
        response.setSubmittedAt(attempt.getSubmittedAt());
        response.setExpiresAt(attempt.getExpiresAt());
        
        if (attempt.getAnswers() != null) {
            response.setAnswers(attempt.getAnswers().stream()
                    .map(AnswerDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    /**
     * DTO for individual answer within an attempt.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDto {
        private Long id;
        private Long questionId;
        private String questionText;
        private Long selectedOptionId;
        private String selectedOptionText;

        public static AnswerDto fromEntity(Answer answer) {
            AnswerDto dto = new AnswerDto();
            dto.setId(answer.getId());
            dto.setQuestionId(answer.getQuestion().getId());
            dto.setQuestionText(answer.getQuestion().getText());
            
            if (answer.getSelectedOption() != null) {
                dto.setSelectedOptionId(answer.getSelectedOption().getId());
                dto.setSelectedOptionText(answer.getSelectedOption().getText());
            }
            
            return dto;
        }
    }
}
