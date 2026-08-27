'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import apiClient from '@/lib/api';
import { Result } from '@/types';

export default function ResultDetailPage() {
  const router = useRouter();
  const params = useParams();
  const resultId = Number(params.id);

  const [result, setResult] = useState<Result | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadResult() {
      try {
        const response = await apiClient.get<Result>(`/results/${resultId}`);
        setResult(response.data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load result');
      } finally {
        setIsLoading(false);
      }
    }
    loadResult();
  }, [resultId]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar />
        <main className="flex-1 p-8">
          <Card>
            <p className="text-red-600 text-center">{error || 'Result not found'}</p>
            <Button onClick={() => router.push('/results')} className="mt-4">
              Back to Results
            </Button>
          </Card>
        </main>
      </div>
    );
  }

  const isPass = result.status === 'PASS';

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="max-w-3xl mx-auto">
          <Button onClick={() => router.push('/results')} variant="secondary" className="mb-4">
            ← Back to Results
          </Button>

          <Card className="mb-6">
            <div className="text-center mb-6">
              <div className={`inline-flex items-center justify-center w-24 h-24 rounded-full mb-4 ${
                isPass ? 'bg-green-100' : 'bg-red-100'
              }`}>
                <span className={`text-4xl font-bold ${isPass ? 'text-green-600' : 'text-red-600'}`}>
                  {result.percentage.toFixed(0)}%
                </span>
              </div>
              <h1 className="text-2xl font-bold mb-2">{result.examAttempt.exam.title}</h1>
              <span className={`inline-block px-4 py-2 rounded-full text-sm font-semibold ${
                isPass 
                  ? 'bg-green-100 text-green-700' 
                  : 'bg-red-100 text-red-700'
              }`}>
                {isPass ? 'PASSED' : 'FAILED'}
              </span>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <p className="text-sm text-gray-500 mb-1">Total Questions</p>
                <p className="text-2xl font-bold">{result.totalQuestions}</p>
              </div>
              <div className="bg-green-50 p-4 rounded-lg text-center">
                <p className="text-sm text-green-600 mb-1">Correct</p>
                <p className="text-2xl font-bold text-green-700">{result.correctAnswers}</p>
              </div>
              <div className="bg-red-50 p-4 rounded-lg text-center">
                <p className="text-sm text-red-600 mb-1">Incorrect</p>
                <p className="text-2xl font-bold text-red-700">{result.incorrectAnswers}</p>
              </div>
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <p className="text-sm text-gray-500 mb-1">Unanswered</p>
                <p className="text-2xl font-bold">{result.unansweredQuestions}</p>
              </div>
            </div>

            <div className="border-t pt-6">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500 mb-1">Your Score</p>
                  <p className="text-3xl font-bold text-violet-600">{result.score} / {result.maxScore}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500 mb-1">Passing Score</p>
                  <p className="text-3xl font-bold">{result.examAttempt.exam.passingScore}%</p>
                </div>
              </div>
            </div>

            <div className="mt-6 pt-6 border-t">
              <p className="text-sm text-gray-500">
                Completed on: {new Date(result.createdAt).toLocaleDateString()} at{' '}
                {new Date(result.createdAt).toLocaleTimeString()}
              </p>
            </div>
          </Card>

          <div className="flex gap-4">
            <Button onClick={() => router.push('/exams')} className="flex-1">
              Take Another Exam
            </Button>
            <Button onClick={() => router.push('/analytics')} variant="secondary" className="flex-1">
              View Analytics
            </Button>
          </div>
        </div>
      </main>
    </div>
  );
}
