package com.quizplatform.controller;

import com.quizplatform.dto.EvaluationResultDto;
import com.quizplatform.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for exam evaluation.
 */
@RestController
@RequestMapping("/api/evaluation")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    /**
     * Evaluate a submitted exam attempt.
     * Students can evaluate their own attempts.
     * Admins can evaluate any attempt.
     */
    @PostMapping("/{attemptId}/evaluate")
    public ResponseEntity<EvaluationResultDto> evaluateAttempt(
            @PathVariable Long attemptId) {
        
        EvaluationResultDto result = evaluationService.evaluateAttempt(attemptId);
        return ResponseEntity.ok(result);
    }
}
