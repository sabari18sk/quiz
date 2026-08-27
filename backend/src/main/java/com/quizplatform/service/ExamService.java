package com.quizplatform.service;

import com.quizplatform.dto.ExamDto;
import com.quizplatform.dto.ExamQuestionDto;
import com.quizplatform.entity.Exam;
import com.quizplatform.entity.ExamQuestion;
import com.quizplatform.enums.ExamStatus;
import com.quizplatform.entity.Question;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.repository.ExamQuestionRepository;
import com.quizplatform.repository.ExamRepository;
import com.quizplatform.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional(readOnly = true)
    public Page<ExamDto> getAllExams(ExamStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exam> examPage;
        
        if (status != null) {
            examPage = examRepository.findByStatus(status, pageable);
        } else {
            examPage = examRepository.findAll(pageable);
        }
        
        return examPage.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        return toDto(exam);
    }

    @Transactional
    public ExamDto createExam(ExamDto examDto) {
        validateExamDto(examDto);
        
        Exam exam = new Exam();
        exam.setTitle(examDto.getTitle());
        exam.setDescription(examDto.getDescription());
        exam.setDurationMinutes(examDto.getDurationMinutes());
        exam.setPassingScore(examDto.getPassingScore());
        exam.setStatus(ExamStatus.DRAFT);
        
        Exam savedExam = examRepository.save(exam);
        
        // Add questions if provided
        if (examDto.getQuestions() != null && !examDto.getQuestions().isEmpty()) {
            addQuestionsToExam(savedExam, examDto.getQuestions());
        }
        
        return toDto(savedExam);
    }

    @Transactional
    public ExamDto updateExam(Long id, ExamDto examDto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        
        validateExamDto(examDto);
        
        exam.setTitle(examDto.getTitle());
        exam.setDescription(examDto.getDescription());
        exam.setDurationMinutes(examDto.getDurationMinutes());
        exam.setPassingScore(examDto.getPassingScore());
        
        // If questions are provided, replace existing ones
        if (examDto.getQuestions() != null) {
            // Remove existing questions
            examQuestionRepository.deleteByExamId(id);
            // Add new questions
            addQuestionsToExam(exam, examDto.getQuestions());
        }
        
        Exam updatedExam = examRepository.save(exam);
        return toDto(updatedExam);
    }

    @Transactional
    public ExamDto publishExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        
        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new BadRequestException("Only draft exams can be published");
        }
        
        if (exam.getExamQuestions().isEmpty()) {
            throw new BadRequestException("Cannot publish exam without questions");
        }
        
        exam.setStatus(ExamStatus.PUBLISHED);
        Exam publishedExam = examRepository.save(exam);
        return toDto(publishedExam);
    }

    @Transactional
    public ExamDto archiveExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new BadRequestException("Only published exams can be archived");
        }
        
        exam.setStatus(ExamStatus.ARCHIVED);
        Exam archivedExam = examRepository.save(exam);
        return toDto(archivedExam);
    }

    @Transactional
    public ExamDto addQuestionToExam(Long examId, ExamQuestionDto questionDto) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        
        Question question = questionRepository.findById(questionDto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionDto.getQuestionId()));
        
        // Check for duplicate
        if (examQuestionRepository.existsByExamIdAndQuestionId(examId, questionDto.getQuestionId())) {
            throw new BadRequestException("Question already exists in this exam");
        }
        
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setQuestionOrder(questionDto.getQuestionOrder());
        examQuestion.setMarks(questionDto.getMarks());
        
        examQuestionRepository.save(examQuestion);
        
        return toDto(examRepository.save(exam));
    }

    @Transactional
    public void removeQuestionFromExam(Long examId, Long questionId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        
        if (!examQuestionRepository.existsByExamIdAndQuestionId(examId, questionId)) {
            throw new ResourceNotFoundException("Question not found in this exam");
        }
        
        examQuestionRepository.deleteByExamIdAndQuestionId(examId, questionId);
    }

    @Transactional
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        
        if (exam.getStatus() == ExamStatus.PUBLISHED) {
            throw new BadRequestException("Cannot delete a published exam. Archive it first.");
        }
        
        examRepository.delete(exam);
    }

    private void validateExamDto(ExamDto examDto) {
        if (examDto.getTitle() == null || examDto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }
        if (examDto.getTitle().length() > 200) {
            throw new BadRequestException("Title must not exceed 200 characters");
        }
        if (examDto.getDurationMinutes() != null && examDto.getDurationMinutes() <= 0) {
            throw new BadRequestException("Duration must be positive");
        }
        if (examDto.getPassingScore() != null && (examDto.getPassingScore() < 0 || examDto.getPassingScore() > 100)) {
            throw new BadRequestException("Passing score must be between 0 and 100");
        }
    }

    private void addQuestionsToExam(Exam exam, List<ExamQuestionDto> questionDtos) {
        for (ExamQuestionDto dto : questionDtos) {
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + dto.getQuestionId()));
            
            if (examQuestionRepository.existsByExamIdAndQuestionId(exam.getId(), dto.getQuestionId())) {
                throw new BadRequestException("Duplicate question in exam: " + dto.getQuestionId());
            }
            
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExam(exam);
            examQuestion.setQuestion(question);
            examQuestion.setQuestionOrder(dto.getQuestionOrder());
            examQuestion.setMarks(dto.getMarks());
            
            examQuestionRepository.save(examQuestion);
        }
    }

    private ExamDto toDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setPassingScore(exam.getPassingScore());
        dto.setStatus(exam.getStatus());
        
        // Calculate total marks and build question list
        List<ExamQuestionDto> questionDtos = exam.getExamQuestions().stream()
                .map(eq -> {
                    ExamQuestionDto eqDto = new ExamQuestionDto();
                    eqDto.setQuestionId(eq.getQuestion().getId());
                    eqDto.setQuestionOrder(eq.getQuestionOrder());
                    eqDto.setMarks(eq.getMarks());
                    return eqDto;
                })
                .sorted((a, b) -> Integer.compare(a.getQuestionOrder(), b.getQuestionOrder()))
                .collect(Collectors.toList());
        
        dto.setQuestions(questionDtos);
        
        double totalMarks = questionDtos.stream()
                .mapToDouble(ExamQuestionDto::getMarks)
                .sum();
        dto.setTotalMarks(totalMarks);
        
        return dto;
    }
}
