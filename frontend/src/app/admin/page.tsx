'use client';

import { useEffect, useState } from 'react';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import LoadingSpinner from '@/components/LoadingSpinner';
import { analyticsService } from '@/services/analyticsService';
import { AdminAnalytics } from '@/types';

export default function AdminDashboardPage() {
  const [analytics, setAnalytics] = useState<AdminAnalytics | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadAnalytics() {
      try {
        const data = await analyticsService.getAdminAnalytics();
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

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
          Admin Dashboard
        </h1>
        <p className="text-gray-600 mb-8">System overview and analytics</p>

        {/* Summary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <Card className="bg-gradient-to-br from-violet-500 to-violet-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Total Users</h3>
            <p className="text-3xl font-bold">{analytics?.totalUsers || 0}</p>
          </Card>
          <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Total Exams</h3>
            <p className="text-3xl font-bold">{analytics?.totalExams || 0}</p>
          </Card>
          <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Total Attempts</h3>
            <p className="text-3xl font-bold">{analytics?.totalAttempts || 0}</p>
          </Card>
          <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
            <h3 className="text-sm opacity-90 mb-1">Overall Pass Rate</h3>
            <p className="text-3xl font-bold">{analytics?.overallPassRate?.toFixed(1) || 0}%</p>
          </Card>
        </div>

        {/* Top Performers */}
        <Card title="Top Performers" className="mb-8">
          {analytics?.topPerformers && analytics.topPerformers.length > 0 ? (
            <div className="space-y-3">
              {analytics.topPerformers.map((performer, index) => (
                <div key={performer.userId} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                  <div className="flex items-center gap-4">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
                      index === 0 ? 'bg-yellow-400 text-yellow-900' :
                      index === 1 ? 'bg-gray-300 text-gray-700' :
                      index === 2 ? 'bg-orange-400 text-orange-900' :
                      'bg-gray-200 text-gray-600'
                    }`}>
                      {index + 1}
                    </div>
                    <div>
                      <h4 className="font-semibold">{performer.userName}</h4>
                      <p className="text-sm text-gray-500">{performer.totalAttempts} attempts</p>
                    </div>
                  </div>
                  <span className="text-lg font-bold text-violet-600">
                    {performer.averageScore.toFixed(1)} avg score
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500 text-center py-8">No performance data available yet.</p>
          )}
        </Card>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="hover:shadow-lg transition-shadow cursor-pointer">
            <h3 className="font-semibold text-lg mb-2">User Management</h3>
            <p className="text-gray-600 text-sm mb-4">Manage user accounts and roles</p>
            <button className="text-violet-600 font-medium hover:text-violet-700">
              Manage Users →
            </button>
          </Card>
          <Card className="hover:shadow-lg transition-shadow cursor-pointer">
            <h3 className="font-semibold text-lg mb-2">Question Bank</h3>
            <p className="text-gray-600 text-sm mb-4">Create and manage questions</p>
            <button className="text-violet-600 font-medium hover:text-violet-700">
              Manage Questions →
            </button>
          </Card>
          <Card className="hover:shadow-lg transition-shadow cursor-pointer">
            <h3 className="font-semibold text-lg mb-2">Exam Management</h3>
            <p className="text-gray-600 text-sm mb-4">Create and publish exams</p>
            <button className="text-violet-600 font-medium hover:text-violet-700">
              Manage Exams →
            </button>
          </Card>
        </div>
      </main>
    </div>
  );
}
