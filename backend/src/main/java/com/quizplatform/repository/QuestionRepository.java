package com.quizplatform.repository;

import com.quizplatform.entity.Question;
import com.quizplatform.enums.Difficulty;
import com.quizplatform.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByType(QuestionType type, Pageable pageable);
    Page<Question> findByDifficulty(Difficulty difficulty, Pageable pageable);
    List<Question> findAllByOrderByCreatedAtDesc();
}
