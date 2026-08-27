export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'STUDENT';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Question {
  id: number;
  text: string;
  type: 'MULTIPLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY';
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  options?: QuestionOption[];
  correctOptionId?: number;
}

export interface QuestionOption {
  id: number;
  text: string;
  isCorrect: boolean;
}

export interface Exam {
  id: number;
  title: string;
  description: string;
  durationMinutes: number;
  passingScore: number;
  totalMarks: number;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  questions?: ExamQuestion[];
  createdAt: string;
  updatedAt: string;
}

export interface ExamQuestion {
  id: number;
  questionId: number;
  question: Question;
  questionOrder: number;
  marks: number;
}

export interface ExamAttempt {
  id: number;
  examId: number;
  exam: Exam;
  userId: number;
  status: 'IN_PROGRESS' | 'SUBMITTED' | 'EXPIRED';
  score: number;
  percentage: number;
  startedAt: string;
  submittedAt?: string;
  expiresAt: string;
  answers?: Answer[];
}

export interface Answer {
  id: number;
  examAttemptId: number;
  examQuestionId: number;
  selectedOptionId?: number;
  selectedOption?: QuestionOption;
}

export interface Result {
  id: number;
  examAttemptId: number;
  examAttempt: ExamAttempt;
  totalQuestions: number;
  correctAnswers: number;
  incorrectAnswers: number;
  unansweredQuestions: number;
  score: number;
  maxScore: number;
  percentage: number;
  status: 'PASS' | 'FAIL' | 'PENDING';
  createdAt: string;
}

export interface StudentAnalytics {
  totalAttempts: number;
  completedAttempts: number;
  passedAttempts: number;
  failedAttempts: number;
  averageScore: number;
  averagePercentage: number;
  highestScore: number;
  lowestScore: number;
  passRate: number;
  performanceHistory: PerformanceHistory[];
}

export interface PerformanceHistory {
  examTitle: string;
  score: number;
  percentage: number;
  status: 'PASS' | 'FAIL';
  completedAt: string;
}

export interface ExamAnalytics {
  examId: number;
  examTitle: string;
  totalAttempts: number;
  averageScore: number;
  passRate: number;
  highestScore: number;
  lowestScore: number;
}

export interface AdminAnalytics {
  totalUsers: number;
  totalExams: number;
  totalAttempts: number;
  overallPassRate: number;
  averageScore: number;
  topPerformers: UserPerformance[];
}

export interface UserPerformance {
  userId: number;
  userName: string;
  averageScore: number;
  totalAttempts: number;
}
