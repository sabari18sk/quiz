import Link from 'next/link';
import { redirect } from 'next/navigation';
import { cookies } from 'next/headers';

export default async function Home() {
  const cookieStore = await cookies();
  const token = cookieStore.get('auth_token');

  if (token) {
    redirect('/dashboard');
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100">
      <main className="w-full max-w-4xl px-6 py-16 text-center">
        <div className="mb-8">
          <h1 className="text-5xl font-bold mb-4 bg-gradient-to-r from-violet-600 to-purple-600 bg-clip-text text-transparent">
            Quiz Platform
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            Master your skills with interactive quizzes and exams
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6 max-w-2xl mx-auto">
          <Link
            href="/login"
            className="card p-8 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group"
          >
            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br from-violet-100 to-purple-100 flex items-center justify-center">
              <svg className="w-8 h-8 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v2a4 4 0 01-4 4H6a4 4 0 01-4-4V7a4 4 0 014-4h7a4 4 0 014 4v1" />
              </svg>
            </div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2 group-hover:text-violet-600 transition-colors">
              Sign In
            </h2>
            <p className="text-gray-500">
              Access your dashboard and continue learning
            </p>
          </Link>

          <Link
            href="/register"
            className="card p-8 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group"
          >
            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br from-violet-100 to-purple-100 flex items-center justify-center">
              <svg className="w-8 h-8 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
              </svg>
            </div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2 group-hover:text-violet-600 transition-colors">
              Create Account
            </h2>
            <p className="text-gray-500">
              Start your learning journey today
            </p>
          </Link>
        </div>

        <div className="mt-16 card p-6 max-w-md mx-auto">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">Features</h3>
          <ul className="text-left text-gray-600 space-y-2">
            <li className="flex items-center">
              <svg className="w-5 h-5 text-violet-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              Interactive multiple-choice exams
            </li>
            <li className="flex items-center">
              <svg className="w-5 h-5 text-violet-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              Real-time results and analytics
            </li>
            <li className="flex items-center">
              <svg className="w-5 h-5 text-violet-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              Track your progress over time
            </li>
            <li className="flex items-center">
              <svg className="w-5 h-5 text-violet-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              Secure role-based access
            </li>
          </ul>
        </div>
      </main>
    </div>
  );
}
