'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import { examService } from '@/services/examService';
import { analyticsService } from '@/services/analyticsService';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import { Exam, StudentAnalytics } from '@/types';

export default function DashboardPage() {
  const router = useRouter();
  const { user } = useAuth();
  const [exams, setExams] = useState<Exam[]>([]);
  const [analytics, setAnalytics] = useState<StudentAnalytics | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [examsData, analyticsData] = await Promise.all([
          examService.getPublishedExams(),
          analyticsService.getStudentAnalytics(),
        ]);
        setExams(examsData.slice(0, 3));
        setAnalytics(analyticsData);
      } catch (error) {
        console.error('Failed to load dashboard data:', error);
      } finally {
        setIsLoading(false);
      }
    }
    loadData();
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
          Welcome back, {user?.firstName}!
        </h1>
        <p className="text-gray-600 mb-8">Here&apos;s your learning overview</p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <Card className="bg-gradient-to-br from-violet-500 to-violet-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Total Attempts</h3>
            <p className="text-3xl font-bold">{analytics?.totalAttempts || 0}</p>
          </Card>
          <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Passed</h3>
            <p className="text-3xl font-bold">{analytics?.passedAttempts || 0}</p>
          </Card>
          <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Average Score</h3>
            <p className="text-3xl font-bold">{analytics?.averagePercentage?.toFixed(1) || 0}%</p>
          </Card>
          <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Pass Rate</h3>
            <p className="text-3xl font-bold">{analytics?.passRate?.toFixed(1) || 0}%</p>
          </Card>
        </div>

        <Card title="Available Exams" className="mb-8">
          {exams.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No exams available at the moment.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {exams.map((exam) => (
                <div key={exam.id} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                  <h3 className="font-semibold text-lg mb-2">{exam.title}</h3>
                  <p className="text-gray-600 text-sm mb-3 line-clamp-2">{exam.description}</p>
                  <div className="flex justify-between items-center text-sm text-gray-500 mb-4">
                    <span>{exam.durationMinutes} min</span>
                    <span>{exam.passingScore}% to pass</span>
                  </div>
                  <Button onClick={() => router.push(`/exams/${exam.id}`)} className="w-full">
                    Start Exam
                  </Button>
                </div>
              ))}
            </div>
          )}
        </Card>

        <div className="flex gap-4">
          <Button onClick={() => router.push('/exams')} className="flex-1">
            View All Exams
          </Button>
          <Button onClick={() => router.push('/results')} variant="secondary" className="flex-1">
            My Results
          </Button>
        </div>
      </main>
    </div>
  );
}
