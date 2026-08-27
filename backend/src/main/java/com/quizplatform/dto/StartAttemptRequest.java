package com.quizplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for starting a new exam attempt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartAttemptRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;
}
