package com.quizplatform.repository;

import com.quizplatform.entity.Exam;
import com.quizplatform.entity.ExamQuestion;
import com.quizplatform.enums.ExamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStatus(ExamStatus status);
    Page<Exam> findByStatus(ExamStatus status, Pageable pageable);
    
    /**
     * Check if a question belongs to an exam.
     */
    boolean existsByIdAndExamQuestionsExamIdAndExamQuestionsQuestionId(
            Long examId, Long examId2, Long questionId);
    
    /**
     * Find the ExamQuestion relationship for a specific exam and question.
     */
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.exam.id = :examId AND eq.question.id = :questionId")
    Optional<ExamQuestion> findExamQuestionByExamIdAndQuestionId(
            @Param("examId") Long examId, 
            @Param("questionId") Long questionId);
}
