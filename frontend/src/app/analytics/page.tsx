'use client';

import { useEffect, useState } from 'react';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import LoadingSpinner from '@/components/LoadingSpinner';
import { analyticsService } from '@/services/analyticsService';
import { StudentAnalytics } from '@/types';

export default function AnalyticsPage() {
  const [analytics, setAnalytics] = useState<StudentAnalytics | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadAnalytics() {
      try {
        const data = await analyticsService.getStudentAnalytics();
        setAnalytics(data);
      } catch (error) {
        console.error('Failed to load analytics:', error);
      } finally {
        setIsLoading(false);
      }
    }
    loadAnalytics();
  }, []);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!analytics || analytics.totalAttempts === 0) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar />
        <main className="flex-1 p-8">
          <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
            My Analytics
          </h1>
          <Card className="mt-8">
            <p className="text-gray-500 text-center py-12">No attempts yet. Start taking exams to see your analytics!</p>
          </Card>
        </main>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
          My Analytics
        </h1>
        <p className="text-gray-600 mb-8">Track your learning progress and performance</p>

        {/* Summary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <Card className="bg-gradient-to-br from-violet-500 to-violet-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Total Attempts</h3>
            <p className="text-3xl font-bold">{analytics.totalAttempts}</p>
          </Card>
          <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Passed</h3>
            <p className="text-3xl font-bold">{analytics.passedAttempts}</p>
          </Card>
          <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Failed</h3>
            <p className="text-3xl font-bold">{analytics.failedAttempts}</p>
          </Card>
          <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Pass Rate</h3>
            <p className="text-3xl font-bold">{analytics.passRate.toFixed(1)}%</p>
          </Card>
        </div>

        {/* Score Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card title="Average Score">
            <p className="text-4xl font-bold text-violet-600">{analytics.averageScore.toFixed(1)}</p>
            <p className="text-sm text-gray-500 mt-2">out of {analytics.averagePercentage.toFixed(1)}% average</p>
          </Card>
          <Card title="Highest Score">
            <p className="text-4xl font-bold text-green-600">{analytics.highestScore}</p>
            <p className="text-sm text-gray-500 mt-2">Best performance</p>
          </Card>
          <Card title="Lowest Score">
            <p className="text-4xl font-bold text-orange-600">{analytics.lowestScore}</p>
            <p className="text-sm text-gray-500 mt-2">Room for improvement</p>
          </Card>
        </div>

        {/* Performance History */}
        <Card title="Performance History" className="mb-8">
          {analytics.performanceHistory.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No performance history available.</p>
          ) : (
            <div className="space-y-3">
              {analytics.performanceHistory.map((item, index) => (
                <div key={index} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                  <div>
                    <h4 className="font-semibold">{item.examTitle}</h4>
                    <p className="text-sm text-gray-500">
                      {new Date(item.completedAt).toLocaleDateString()}
                    </p>
                  </div>
                  <div className="flex items-center gap-4">
                    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                      item.status === 'PASS'
                        ? 'bg-green-100 text-green-700'
                        : 'bg-red-100 text-red-700'
                    }`}>
                      {item.status}
                    </span>
                    <span className="text-lg font-bold text-violet-600">{item.percentage.toFixed(1)}%</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </main>
    </div>
  );
}
