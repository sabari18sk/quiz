import apiClient from '@/lib/api';
import { Exam, ExamAttempt, Answer, Result } from '@/types';

export const examService = {
  async getPublishedExams(): Promise<Exam[]> {
    const response = await apiClient.get<Exam[]>('/exams?status=PUBLISHED');
    return response.data;
  },

  async getExamById(id: number): Promise<Exam> {
    const response = await apiClient.get<Exam>(`/exams/${id}`);
    return response.data;
  },

  async startAttempt(examId: number): Promise<ExamAttempt> {
    const response = await apiClient.post<ExamAttempt>('/attempts/start', { examId });
    return response.data;
  },

  async getAttempt(attemptId: number): Promise<ExamAttempt> {
    const response = await apiClient.get<ExamAttempt>(`/attempts/${attemptId}`);
    return response.data;
  },

  async submitAnswer(attemptId: number, questionId: number, optionId: number): Promise<ExamAttempt> {
    const response = await apiClient.post<ExamAttempt>('/attempts/answer', {
      attemptId,
      questionId,
      optionId,
    });
    return response.data;
  },

  async submitAttempt(attemptId: number): Promise<Result> {
    const response = await apiClient.post<Result>(`/attempts/${attemptId}/submit`);
    return response.data;
  },

  async getMyResults(): Promise<Result[]> {
    const response = await apiClient.get<Result[]>('/results/my-results');
    return response.data;
  },
};
