package com.quizplatform.repository;

import com.quizplatform.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByExamAttemptId(Long examAttemptId);
    
    /**
     * Find an answer by attempt ID and question ID.
     */
    Optional<Answer> findByExamAttemptIdAndQuestionId(Long examAttemptId, Long questionId);
}
