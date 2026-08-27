'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';

export default function Sidebar() {
  const pathname = usePathname();
  const { user, isAdmin, logout } = useAuth();

  const studentLinks = [
    { href: '/dashboard', label: 'Dashboard' },
    { href: '/exams', label: 'Exams' },
    { href: '/results', label: 'Results' },
    { href: '/analytics', label: 'Analytics' },
  ];

  const adminLinks = [
    { href: '/admin', label: 'Admin Dashboard' },
    { href: '/admin/users', label: 'User Management' },
    { href: '/admin/questions', label: 'Question Bank' },
    { href: '/admin/exams', label: 'Exam Management' },
    { href: '/admin/analytics', label: 'Analytics' },
  ];

  const links = isAdmin ? adminLinks : studentLinks;

  return (
    <aside className="w-64 bg-white shadow-lg min-h-screen">
      <div className="p-6">
        <h2 className="text-xl font-bold bg-gradient-to-r from-lavender to-violet bg-clip-text text-transparent">
          Quiz Platform
        </h2>
        <p className="text-sm text-gray-600 mt-1">{user?.firstName} {user?.lastName}</p>
        <p className="text-xs text-gray-500">{user?.role}</p>
      </div>
      
      <nav className="mt-6">
        {links.map((link) => (
          <Link
            key={link.href}
            href={link.href}
            className={`block px-6 py-3 text-gray-700 hover:bg-lavender-50 hover:text-violet-700 transition-colors ${
              pathname === link.href ? 'bg-lavender-100 text-violet-700 border-r-4 border-violet-600' : ''
            }`}
          >
            {link.label}
          </Link>
        ))}
      </nav>

      <div className="absolute bottom-6 left-6 right-6">
        <button
          onClick={logout}
          className="w-full text-left px-6 py-3 text-gray-700 hover:bg-red-50 hover:text-red-600 transition-colors"
        >
          Logout
        </button>
      </div>
    </aside>
  );
}
