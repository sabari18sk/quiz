package com.quizplatform.service;

import com.quizplatform.dto.QuestionDto;
import com.quizplatform.dto.QuestionListResponse;
import com.quizplatform.dto.QuestionOptionDto;
import com.quizplatform.entity.Question;
import com.quizplatform.entity.QuestionOption;
import com.quizplatform.enums.Difficulty;
import com.quizplatform.enums.QuestionType;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.repository.QuestionOptionRepository;
import com.quizplatform.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Transactional(readOnly = true)
    public QuestionListResponse getAllQuestions(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy != null && !sortBy.isEmpty() ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Question> questionPage = questionRepository.findAll(pageable);

        List<QuestionDto> dtos = questionPage.getContent().stream()
                .map(this::toDto)
                .toList();

        return new QuestionListResponse(
                dtos,
                questionPage.getTotalElements(),
                questionPage.getNumber(),
                questionPage.getSize(),
                questionPage.getTotalPages(),
                questionPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public QuestionListResponse getQuestionsByType(QuestionType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> questionPage = questionRepository.findByType(type, pageable);

        List<QuestionDto> dtos = questionPage.getContent().stream()
                .map(this::toDto)
                .toList();

        return new QuestionListResponse(
                dtos,
                questionPage.getTotalElements(),
                questionPage.getNumber(),
                questionPage.getSize(),
                questionPage.getTotalPages(),
                questionPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public QuestionListResponse getQuestionsByDifficulty(Difficulty difficulty, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> questionPage = questionRepository.findByDifficulty(difficulty, pageable);

        List<QuestionDto> dtos = questionPage.getContent().stream()
                .map(this::toDto)
                .toList();

        return new QuestionListResponse(
                dtos,
                questionPage.getTotalElements(),
                questionPage.getNumber(),
                questionPage.getSize(),
                questionPage.getTotalPages(),
                questionPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return toDto(question);
    }

    @Transactional
    public QuestionDto createQuestion(QuestionDto dto) {
        validateQuestionDto(dto);

        Question question = new Question();
        question.setText(dto.getText());
        question.setType(dto.getType());
        question.setDifficulty(dto.getDifficulty());

        // Save question first
        Question savedQuestion = questionRepository.save(question);

        // Handle options
        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            List<QuestionOption> options = new ArrayList<>();
            boolean hasCorrectOption = false;

            for (QuestionOptionDto optionDto : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setText(optionDto.getText());
                option.setCorrect(optionDto.isCorrect());
                option.setQuestion(savedQuestion);
                options.add(option);

                if (optionDto.isCorrect()) {
                    hasCorrectOption = true;
                }
            }

            // Validate at least one correct option for multiple choice/true-false
            if ((dto.getType() == QuestionType.MULTIPLE_CHOICE || dto.getType() == QuestionType.TRUE_FALSE)
                    && !hasCorrectOption) {
                throw new BadRequestException("At least one option must be marked as correct");
            }

            savedQuestion.setOptions(options);
            questionRepository.save(savedQuestion);
        }

        return toDto(savedQuestion);
    }

    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto dto) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        validateQuestionDto(dto);

        existingQuestion.setText(dto.getText());
        existingQuestion.setType(dto.getType());
        existingQuestion.setDifficulty(dto.getDifficulty());

        // Update options - delete old and create new
        if (existingQuestion.getOptions() != null) {
            for (QuestionOption option : existingQuestion.getOptions()) {
                option.setQuestion(null);
            }
            questionOptionRepository.deleteAll(existingQuestion.getOptions());
            existingQuestion.getOptions().clear();
        }

        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            List<QuestionOption> options = new ArrayList<>();
            boolean hasCorrectOption = false;

            for (QuestionOptionDto optionDto : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setText(optionDto.getText());
                option.setCorrect(optionDto.isCorrect());
                option.setQuestion(existingQuestion);
                options.add(option);

                if (optionDto.isCorrect()) {
                    hasCorrectOption = true;
                }
            }

            if ((dto.getType() == QuestionType.MULTIPLE_CHOICE || dto.getType() == QuestionType.TRUE_FALSE)
                    && !hasCorrectOption) {
                throw new BadRequestException("At least one option must be marked as correct");
            }

            existingQuestion.setOptions(options);
        }

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return toDto(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }

    private void validateQuestionDto(QuestionDto dto) {
        if (dto.getText() == null || dto.getText().trim().isEmpty()) {
            throw new BadRequestException("Question text is required");
        }

        if (dto.getType() == null) {
            throw new BadRequestException("Question type is required");
        }

        if (dto.getDifficulty() == null) {
            throw new BadRequestException("Difficulty is required");
        }

        // Validate options for multiple choice and true-false questions
        if (dto.getType() == QuestionType.MULTIPLE_CHOICE || dto.getType() == QuestionType.TRUE_FALSE) {
            if (dto.getOptions() == null || dto.getOptions().isEmpty()) {
                throw new BadRequestException("At least one option is required for multiple choice questions");
            }

            if (dto.getOptions().size() < 2) {
                throw new BadRequestException("At least two options are required");
            }
        }
    }

    private QuestionDto toDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setType(question.getType());
        dto.setDifficulty(question.getDifficulty());

        if (question.getOptions() != null) {
            List<QuestionOptionDto> optionDtos = question.getOptions().stream()
                    .map(this::optionToDto)
                    .toList();
            dto.setOptions(optionDtos);
        }

        return dto;
    }

    private QuestionOptionDto optionToDto(QuestionOption option) {
        QuestionOptionDto dto = new QuestionOptionDto();
        dto.setId(option.getId());
        dto.setText(option.getText());
        dto.setCorrect(option.isCorrect());
        return dto;
    }
}
