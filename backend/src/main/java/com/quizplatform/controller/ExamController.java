package com.quizplatform.controller;

import com.quizplatform.dto.ExamDto;
import com.quizplatform.dto.ExamListResponse;
import com.quizplatform.dto.ExamQuestionDto;
import com.quizplatform.enums.ExamStatus;
import com.quizplatform.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ExamListResponse> getAllExams(
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ExamDto> examPage = examService.getAllExams(status, page, size);
        ExamListResponse response = ExamListResponse.fromPage(examPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ExamDto> getExamById(@PathVariable Long id) {
        ExamDto exam = examService.getExamById(id);
        return ResponseEntity.ok(exam);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamDto> createExam(@Valid @RequestBody ExamDto examDto) {
        ExamDto createdExam = examService.createExam(examDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExam);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamDto> updateExam(@PathVariable Long id, @Valid @RequestBody ExamDto examDto) {
        ExamDto updatedExam = examService.updateExam(id, examDto);
        return ResponseEntity.ok(updatedExam);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamDto> publishExam(@PathVariable Long id) {
        ExamDto publishedExam = examService.publishExam(id);
        return ResponseEntity.ok(publishedExam);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamDto> archiveExam(@PathVariable Long id) {
        ExamDto archivedExam = examService.archiveExam(id);
        return ResponseEntity.ok(archivedExam);
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamDto> addQuestionToExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamQuestionDto questionDto) {
        ExamDto updatedExam = examService.addQuestionToExam(id, questionDto);
        return ResponseEntity.ok(updatedExam);
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeQuestionFromExam(
            @PathVariable Long examId,
            @PathVariable Long questionId) {
        examService.removeQuestionFromExam(examId, questionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
