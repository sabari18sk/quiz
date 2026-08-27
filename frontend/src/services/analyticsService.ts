import apiClient from '@/lib/api';
import { StudentAnalytics, ExamAnalytics, AdminAnalytics } from '@/types';

export const analyticsService = {
  async getStudentAnalytics(): Promise<StudentAnalytics> {
    const response = await apiClient.get<StudentAnalytics>('/analytics/student');
    return response.data;
  },

  async getExamAnalytics(examId: number): Promise<ExamAnalytics> {
    const response = await apiClient.get<ExamAnalytics>(`/analytics/exam/${examId}`);
    return response.data;
  },

  async getAdminAnalytics(): Promise<AdminAnalytics> {
    const response = await apiClient.get<AdminAnalytics>('/analytics/admin');
    return response.data;
  },
};
