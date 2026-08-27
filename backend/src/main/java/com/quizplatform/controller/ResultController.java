package com.quizplatform.controller;

import com.quizplatform.dto.ResultDto;
import com.quizplatform.dto.ResultListResponse;
import com.quizplatform.enums.UserRole;
import com.quizplatform.service.ResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    /**
     * Get result by attempt ID
     * Students can only view their own results
     */
    @GetMapping("/attempt/{attemptId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ResultDto> getResultByAttemptId(@PathVariable Long attemptId) {
        ResultDto result = resultService.getResultByAttemptId(attemptId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get current user's results (for students)
     */
    @GetMapping("/my-results")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResultListResponse> getMyResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        
        Page<ResultDto> results = resultService.getUserResults(pageable);
        return ResponseEntity.ok(ResultListResponse.fromPage(results));
    }

    /**
     * Get all results (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultListResponse> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        
        Page<ResultDto> results = resultService.getAllResults(pageable);
        return ResponseEntity.ok(ResultListResponse.fromPage(results));
    }

    /**
     * Get results by exam ID (admin only)
     */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultListResponse> getResultsByExamId(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        
        Page<ResultDto> results = resultService.getResultsByExamId(examId, pageable);
        return ResponseEntity.ok(ResultListResponse.fromPage(results));
    }
}
