"use client";

import { useEffect, useState } from 'react';
import * as api from '@/lib/api';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth';

interface Course {
  id: number;
  title: string;
  description: string;
  instructorName: string;
}

export default function CoursesPage() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [error, setError] = useState('');
  const { user } = useAuth(); // To check if user is an instructor

  useEffect(() => {
    api.getCourses()
      .then(data => setCourses(data))
      .catch(err => setError(err.message));
  }, []);

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Available Courses</h1>
        {user?.role === 'INSTRUCTOR' && (
          <Link href="/courses/new" className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
            Create Course
          </Link>
        )}
      </div>
      
      {error && <p className="text-red-500">{error}</p>}
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map(course => (
          <Link key={course.id} href={`/courses/${course.id}`} className="block p-6 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100">
            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900">{course.title}</h5>
            <p className="font-normal text-gray-700">{course.description}</p>
            <p className="mt-4 text-sm text-gray-500">Instructor: {course.instructorName}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}