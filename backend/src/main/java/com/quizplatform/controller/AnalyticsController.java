package com.quizplatform.controller;

import com.quizplatform.dto.AdminAnalyticsDto;
import com.quizplatform.dto.ExamAnalyticsDto;
import com.quizplatform.dto.StudentAnalyticsDto;
import com.quizplatform.exception.UnauthorizedException;
import com.quizplatform.security.CustomUserDetails;
import com.quizplatform.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get student analytics for the currently authenticated user.
     * Accessible by STUDENT role.
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAnalyticsDto> getStudentAnalytics() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = getCurrentUserId(); // In real implementation, fetch from user details
        
        // For now, we'll need to get user ID from authentication principal
        // This is a simplified version - in production, extract from JWT claims
        StudentAnalyticsDto analytics = analyticsService.getStudentAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get analytics for a specific exam.
     * Accessible by ADMIN or INSTRUCTOR role.
     */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<ExamAnalyticsDto> getExamAnalytics(@PathVariable Long examId) {
        ExamAnalyticsDto analytics = analyticsService.getExamAnalytics(examId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get system-wide admin analytics.
     * Accessible by ADMIN role only.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminAnalyticsDto> getAdminAnalytics() {
        AdminAnalyticsDto analytics = analyticsService.getAdminAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Helper method to extract user ID from authentication.
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }
        throw new UnauthorizedException("Unable to extract user ID from authentication context");
    }
}
