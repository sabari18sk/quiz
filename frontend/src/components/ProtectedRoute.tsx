'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import LoadingSpinner from '@/components/LoadingSpinner';

export default function ProtectedRoute({
  children,
  adminOnly = false,
}: {
  children: React.ReactNode;
  adminOnly?: boolean;
}) {
  const router = useRouter();
  const { isAuthenticated, isAdmin, isLoaded } = useAuth();

  useEffect(() => {
    if (isLoaded && !isAuthenticated) {
      router.push('/login');
      return;
    }
    
    if (isLoaded && adminOnly && !isAdmin) {
      router.push('/dashboard');
      return;
    }
  }, [isAuthenticated, isAdmin, isLoaded, adminOnly, router]);

  if (!isLoaded) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!isAuthenticated || (adminOnly && !isAdmin)) {
    return null;
  }

  return <>{children}</>;
}
