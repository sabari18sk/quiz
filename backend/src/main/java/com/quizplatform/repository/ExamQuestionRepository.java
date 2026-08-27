package com.quizplatform.repository;

import com.quizplatform.entity.ExamQuestion;
import com.quizplatform.entity.ExamQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
    List<ExamQuestion> findByExamIdOrderByQuestionOrderAsc(Long examId);
    boolean existsByExamIdAndQuestionId(Long examId, Long questionId);
    void deleteByExamId(Long examId);
    void deleteByExamIdAndQuestionId(Long examId, Long questionId);
}
