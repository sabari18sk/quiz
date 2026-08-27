package com.quizplatform.dto;

import com.quizplatform.enums.ExamStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDto {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Double passingScore;
    private ExamStatus status;
    private Double totalMarks;
    private List<ExamQuestionDto> questions;
}
