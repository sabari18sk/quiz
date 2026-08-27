'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import { examService } from '@/services/examService';
import { Exam } from '@/types';

export default function ExamDetailPage() {
  const router = useRouter();
  const params = useParams();
  const examId = Number(params.id);
  
  const [exam, setExam] = useState<Exam | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadExam() {
      try {
        const data = await examService.getExamById(examId);
        setExam(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load exam details');
      } finally {
        setIsLoading(false);
      }
    }
    loadExam();
  }, [examId]);

  const handleStartExam = async () => {
    try {
      const attempt = await examService.startAttempt(examId);
      router.push(`/attempt/${attempt.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to start exam');
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error || !exam) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar />
        <main className="flex-1 p-8">
          <Card>
            <p className="text-red-600 text-center">{error || 'Exam not found'}</p>
            <Button onClick={() => router.push('/exams')} className="mt-4">
              Back to Exams
            </Button>
          </Card>
        </main>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <Button onClick={() => router.push('/exams')} variant="secondary" className="mb-4">
          ← Back to Exams
        </Button>
        
        <Card className="max-w-3xl">
          <h1 className="text-3xl font-bold mb-4 bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
            {exam.title}
          </h1>
          <p className="text-gray-600 mb-6">{exam.description}</p>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500 mb-1">Duration</p>
              <p className="text-xl font-semibold">{exam.durationMinutes} min</p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500 mb-1">Total Marks</p>
              <p className="text-xl font-semibold">{exam.totalMarks}</p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500 mb-1">Passing Score</p>
              <p className="text-xl font-semibold">{exam.passingScore}%</p>
            </div>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-sm text-gray-500 mb-1">Questions</p>
              <p className="text-xl font-semibold">{exam.questions?.length || 0}</p>
            </div>
          </div>

          {exam.questions && exam.questions.length > 0 && (
            <div className="mb-6">
              <h2 className="text-lg font-semibold mb-3">Question Overview</h2>
              <div className="space-y-2">
                {exam.questions.map((eq, index) => (
                  <div key={eq.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <span className="font-medium">Question {index + 1}</span>
                    <span className="text-sm text-gray-500">{eq.marks} marks</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="border-t pt-6">
            <Button onClick={handleStartExam} className="w-full py-3 text-lg">
              Start Exam
            </Button>
            <p className="text-sm text-gray-500 text-center mt-3">
              Make sure you have enough time before starting. The exam will auto-submit when time expires.
            </p>
          </div>
        </Card>
      </main>
    </div>
  );
}
