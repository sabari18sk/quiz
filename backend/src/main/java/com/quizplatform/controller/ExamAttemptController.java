package com.quizplatform.controller;

import com.quizplatform.dto.AnswerRequest;
import com.quizplatform.dto.AttemptResponse;
import com.quizplatform.dto.StartAttemptRequest;
import com.quizplatform.service.ExamAttemptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing exam attempts.
 */
@RestController
@RequestMapping("/api/attempts")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class ExamAttemptController {

    @Autowired
    private ExamAttemptService examAttemptService;

    /**
     * Start a new exam attempt.
     */
    @PostMapping
    public ResponseEntity<AttemptResponse> startAttempt(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StartAttemptRequest request) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        AttemptResponse response = examAttemptService.startAttempt(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current attempt for a specific exam.
     */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<AttemptResponse> getCurrentAttempt(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long examId) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        AttemptResponse response = examAttemptService.getCurrentAttempt(userId, examId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all attempts for the current user.
     */
    @GetMapping
    public ResponseEntity<List<AttemptResponse>> getUserAttempts(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        List<AttemptResponse> responses = examAttemptService.getUserAttempts(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Submit an answer for a question in the current attempt.
     */
    @PostMapping("/{attemptId}/answers")
    public ResponseEntity<AttemptResponse> submitAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long attemptId,
            @Valid @RequestBody AnswerRequest request) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        AttemptResponse response = examAttemptService.submitAnswer(userId, attemptId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Submit the entire attempt for evaluation.
     */
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AttemptResponse> submitAttempt(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long attemptId) {
        
        Long userId = Long.valueOf(userDetails.getUsername());
        AttemptResponse response = examAttemptService.submitAttempt(userId, attemptId);
        return ResponseEntity.ok(response);
    }
}
