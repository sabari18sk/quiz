'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import { examService } from '@/services/examService';
import { Exam } from '@/types';

export default function ExamsPage() {
  const router = useRouter();
  const [exams, setExams] = useState<Exam[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadExams() {
      try {
        const data = await examService.getPublishedExams();
        setExams(data);
      } catch (error) {
        console.error('Failed to load exams:', error);
      } finally {
        setIsLoading(false);
      }
    }
    loadExams();
  }, []);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
          Available Exams
        </h1>
        <p className="text-gray-600 mb-8">Browse and take available exams</p>

        {exams.length === 0 ? (
          <Card>
            <p className="text-gray-500 text-center py-12">No exams available at the moment.</p>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {exams.map((exam) => (
              <Card key={exam.id} className="hover:shadow-lg transition-shadow">
                <h3 className="font-semibold text-xl mb-2">{exam.title}</h3>
                <p className="text-gray-600 text-sm mb-4 line-clamp-3">{exam.description}</p>
                <div className="flex justify-between items-center text-sm text-gray-500 mb-4 pb-4 border-b">
                  <span className="flex items-center">
                    <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    {exam.durationMinutes} min
                  </span>
                  <span className="flex items-center">
                    <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                    </svg>
                    {exam.totalMarks} pts
                  </span>
                </div>
                <div className="flex items-center justify-between mb-4">
                  <span className="text-sm bg-violet-100 text-violet-700 px-3 py-1 rounded-full">
                    Pass: {exam.passingScore}%
                  </span>
                  <span className="text-sm bg-gray-100 text-gray-700 px-3 py-1 rounded-full">
                    {exam.questions?.length || 0} questions
                  </span>
                </div>
                <Button onClick={() => router.push(`/exams/${exam.id}`)} className="w-full">
                  View Details
                </Button>
              </Card>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
