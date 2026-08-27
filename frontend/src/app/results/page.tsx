'use client';

import { useEffect, useState } from 'react';
import Sidebar from '@/components/Sidebar';
import Card from '@/components/Card';
import Button from '@/components/Button';
import LoadingSpinner from '@/components/LoadingSpinner';
import { examService } from '@/services/examService';
import { Result } from '@/types';
import Link from 'next/link';

export default function ResultsPage() {
  const [results, setResults] = useState<Result[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadResults() {
      try {
        const data = await examService.getMyResults();
        setResults(data);
      } catch (error) {
        console.error('Failed to load results:', error);
      } finally {
        setIsLoading(false);
      }
    }
    loadResults();
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
          My Results
        </h1>
        <p className="text-gray-600 mb-8">View your exam performance history</p>

        {results.length === 0 ? (
          <Card>
            <p className="text-gray-500 text-center py-12">You haven&apos;t taken any exams yet.</p>
            <div className="text-center mt-4">
              <Link href="/exams">
                <Button>Browse Exams</Button>
              </Link>
            </div>
          </Card>
        ) : (
          <div className="space-y-4">
            {results.map((result) => (
              <Card key={result.id} className="hover:shadow-lg transition-shadow">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg mb-1">{result.examAttempt.exam.title}</h3>
                    <p className="text-sm text-gray-500 mb-2">
                      Completed on {new Date(result.createdAt).toLocaleDateString()}
                    </p>
                    <div className="flex gap-4 text-sm">
                      <span className="text-gray-600">
                        Score: <span className="font-semibold">{result.score}/{result.maxScore}</span>
                      </span>
                      <span className="text-gray-600">
                        Percentage: <span className="font-semibold">{result.percentage.toFixed(1)}%</span>
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${
                        result.status === 'PASS'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}>
                        {result.status}
                      </span>
                    </div>
                    <Link href={`/results/${result.id}`}>
                      <Button variant="secondary" size="sm">View Details</Button>
                    </Link>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
