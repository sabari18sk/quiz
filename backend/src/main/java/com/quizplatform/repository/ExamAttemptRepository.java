package com.quizplatform.repository;

import com.quizplatform.entity.ExamAttempt;
import com.quizplatform.enums.AttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserId(Long userId);
    List<ExamAttempt> findByUserIdAndExamId(Long userId, Long examId);
    Optional<ExamAttempt> findByUserIdAndExamIdAndStatus(Long userId, Long examId, AttemptStatus status);
    
    /**
     * Find all attempts by exam ID.
     */
    List<ExamAttempt> findByExamId(Long examId);
    
    /**
     * Find all attempts by status.
     */
    List<ExamAttempt> findByStatus(AttemptStatus status);
    
    /**
     * Find submitted attempts by user with pagination.
     */
    Page<ExamAttempt> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * Find all submitted attempts with pagination.
     */
    Page<ExamAttempt> findByStatus(String status, Pageable pageable);
    
    /**
     * Find submitted attempts for an exam with pagination.
     */
    Page<ExamAttempt> findByExamIdAndStatus(Long examId, String status, Pageable pageable);
}
