'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import { examService } from '@/services/examService';
import { ExamAttempt, QuestionOption } from '@/types';

export default function AttemptPage() {
  const router = useRouter();
  const params = useParams();
  const attemptId = Number(params.id);

  const [attempt, setAttempt] = useState<ExamAttempt | null>(null);
  const [selectedAnswers, setSelectedAnswers] = useState<Record<number, number>>({});
  const [timeRemaining, setTimeRemaining] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadAttempt() {
      try {
        const data = await examService.getAttempt(attemptId);
        setAttempt(data);
        
        // Calculate time remaining in seconds
        const expiresAt = new Date(data.expiresAt).getTime();
        const now = new Date().getTime();
        const remaining = Math.max(0, Math.floor((expiresAt - now) / 1000));
        setTimeRemaining(remaining);

        // Load existing answers
        const existingAnswers: Record<number, number> = {};
        data.answers?.forEach((answer) => {
          if (answer.selectedOptionId) {
            existingAnswers[answer.examQuestionId] = answer.selectedOptionId;
          }
        });
        setSelectedAnswers(existingAnswers);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load attempt');
      } finally {
        setIsLoading(false);
      }
    }
    loadAttempt();
  }, [attemptId]);

  // Timer countdown
  useEffect(() => {
    if (timeRemaining <= 0 || !attempt) return;

    const timer = setInterval(() => {
      setTimeRemaining((prev) => {
        if (prev <= 1) {
          handleSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeRemaining, attempt]);

  const handleSelectOption = (questionId: number, optionId: number) => {
    setSelectedAnswers((prev) => ({ ...prev, [questionId]: optionId }));
  };

  const handleSubmit = async () => {
    if (!attempt) return;
    
    setIsSubmitting(true);
    setError('');

    try {
      // Submit all answers
      for (const [questionId, optionId] of Object.entries(selectedAnswers)) {
        await examService.submitAnswer(attemptId, Number(questionId), optionId);
      }

      // Submit the attempt
      const result = await examService.submitAttempt(attemptId);
      router.push(`/results/${result.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit exam');
      setIsSubmitting(false);
    }
  };

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error || !attempt) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar />
        <main className="flex-1 p-8">
          <Card>
            <p className="text-red-600 text-center">{error || 'Attempt not found'}</p>
            <Button onClick={() => router.push('/exams')} className="mt-4">
              Back to Exams
            </Button>
          </Card>
        </main>
      </div>
    );
  }

  const questions = attempt.exam.questions || [];
  const answeredCount = Object.keys(selectedAnswers).length;

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="max-w-4xl mx-auto">
          {/* Header with timer */}
          <div className="bg-white rounded-xl shadow-md p-6 mb-6">
            <div className="flex justify-between items-center mb-4">
              <h1 className="text-2xl font-bold">{attempt.exam.title}</h1>
              <div className={`text-2xl font-mono font-bold ${timeRemaining < 300 ? 'text-red-600' : 'text-violet-600'}`}>
                {formatTime(timeRemaining)}
              </div>
            </div>
            <div className="flex justify-between text-sm text-gray-600">
              <span>Questions: {questions.length}</span>
              <span>Answered: {answeredCount}/{questions.length}</span>
              <span>Score: {attempt.score || 0}/{attempt.exam.totalMarks}</span>
            </div>
          </div>

          {/* Questions */}
          <div className="space-y-6 mb-6">
            {questions.map((eq, index) => (
              <Card key={eq.id}>
                <div className="mb-4">
                  <span className="text-sm text-gray-500">Question {index + 1}</span>
                  <p className="text-lg font-medium mt-1">{eq.question.text}</p>
                </div>
                
                <div className="space-y-2">
                  {eq.question.options?.map((option) => (
                    <label
                      key={option.id}
                      className={`flex items-center p-4 border rounded-lg cursor-pointer transition-colors ${
                        selectedAnswers[eq.id] === option.id
                          ? 'border-violet-500 bg-violet-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <input
                        type="radio"
                        name={`question-${eq.id}`}
                        value={option.id}
                        checked={selectedAnswers[eq.id] === option.id}
                        onChange={() => handleSelectOption(eq.id, option.id)}
                        className="w-4 h-4 text-violet-600"
                      />
                      <span className="ml-3">{option.text}</span>
                    </label>
                  ))}
                </div>
              </Card>
            ))}
          </div>

          {/* Submit button */}
          {error && (
            <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          <div className="bg-white rounded-xl shadow-md p-6">
            <Button
              onClick={handleSubmit}
              isLoading={isSubmitting}
              className="w-full py-3 text-lg"
              disabled={answeredCount === 0}
            >
              Submit Exam
            </Button>
            <p className="text-sm text-gray-500 text-center mt-3">
              You cannot change answers after submission.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}
