"use client";

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import * as api from '@/lib/api';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth'; // Import the useAuth hook

// Define interfaces for better type safety
interface LectureSummary {
  id: number;
  title: string;
  type: 'READING' | 'QUIZ';
}

interface CourseDetails {
  id: number;
  title: string;
  description: string;
  instructorName: string;
}

interface Progress {
  completed: number;
  total: number;
  display: string;
}

// --- NEW COMPONENT FOR ADDING LECTURES ---
const AddLectureModal = ({ courseId, onSuccess, onClose }: { courseId: string, onSuccess: () => void, onClose: () => void }) => {
  const [title, setTitle] = useState('');
  const [type, setType] = useState<'READING' | 'QUIZ'>('READING');
  const [text, setText] = useState('');
  const [questions, setQuestions] = useState([{ text: '', options: ['', ''], correctIndex: 0 }]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleAddQuestion = () => {
    setQuestions([...questions, { text: '', options: ['', ''], correctIndex: 0 }]);
  };

  const handleAddOption = (qIndex: number) => {
    const newQuestions = [...questions];
    newQuestions[qIndex].options.push('');
    setQuestions(newQuestions);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    let payload: any;
    if (type === 'READING') {
      payload = { title, type, text, linkUrl: null, questions: null };
    } else { // QUIZ
      const quizQuestions = questions.map(q => ({
        text: q.text,
        options: q.options.filter(opt => opt.trim() !== ''),
        correctIndex: q.correctIndex
      }));
      payload = { title, type, text: null, linkUrl: null, questions: quizQuestions };
    }

    try {
      await api.createLecture(courseId, payload);
      onSuccess();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex items-center justify-center">
      <div className="relative p-8 bg-white w-full max-w-2xl mx-auto rounded-lg shadow-lg">
        <h2 className="text-2xl font-bold mb-4">Add New Lecture</h2>
        {error && <p className="text-red-500 mb-4">{error}</p>}
        <form onSubmit={handleSubmit}>
          {/* Lecture Type Selection */}
          <div className="mb-4">
            <label className="block text-gray-700">Lecture Type</label>
            <select
              value={type}
              onChange={(e) => setType(e.target.value as 'READING' | 'QUIZ')}
              className="w-full p-2 border rounded"
            >
              <option value="READING">Reading</option>
              <option value="QUIZ">Quiz</option>
            </select>
          </div>

          {/* Title */}
          <div className="mb-4">
            <label className="block text-gray-700">Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>

          {type === 'READING' && (
            <div className="mb-4">
              <label className="block text-gray-700">Text Content</label>
              <textarea
                value={text}
                onChange={(e) => setText(e.target.value)}
                rows={5}
                className="w-full p-2 border rounded"
                required
              />
            </div>
          )}

          {type === 'QUIZ' && (
            <div>
              <h3 className="text-xl font-semibold mb-2">Questions</h3>
              {questions.map((q, qIndex) => (
                <div key={qIndex} className="p-4 border rounded-lg mb-4">
                  <div className="mb-2">
                    <label className="block text-gray-700">Question Text</label>
                    <input
                      type="text"
                      value={q.text}
                      onChange={(e) => {
                        const newQuestions = [...questions];
                        newQuestions[qIndex].text = e.target.value;
                        setQuestions(newQuestions);
                      }}
                      className="w-full p-2 border rounded"
                      required
                    />
                  </div>
                  <div className="mb-2">
                    <label className="block text-gray-700">Options</label>
                    {q.options.map((option, oIndex) => (
                      <div key={oIndex} className="flex items-center mb-2">
                        <input
                          type="text"
                          value={option}
                          onChange={(e) => {
                            const newQuestions = [...questions];
                            newQuestions[qIndex].options[oIndex] = e.target.value;
                            setQuestions(newQuestions);
                          }}
                          className="w-full p-2 border rounded"
                          required
                        />
                         <input
                           type="radio"
                           name={`correct-option-${qIndex}`}
                           checked={q.correctIndex === oIndex}
                           onChange={() => {
                             const newQuestions = [...questions];
                             newQuestions[qIndex].correctIndex = oIndex;
                             setQuestions(newQuestions);
                           }}
                           className="ml-2"
                         />
                         <span className="ml-1 text-sm text-gray-600">Correct</span>
                      </div>
                    ))}
                    <button type="button" onClick={() => handleAddOption(qIndex)} className="text-blue-500 hover:underline text-sm mt-2">
                      + Add Option
                    </button>
                  </div>
                </div>
              ))}
              <button type="button" onClick={handleAddQuestion} className="bg-gray-200 text-gray-800 py-2 px-4 rounded hover:bg-gray-300">
                + Add Question
              </button>
            </div>
          )}

          <div className="flex justify-end mt-6 space-x-4">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-500 text-white font-bold py-2 px-4 rounded hover:bg-gray-700"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="bg-blue-500 text-white font-bold py-2 px-4 rounded hover:bg-blue-600 disabled:bg-gray-400"
            >
              {isLoading ? 'Adding...' : 'Add Lecture'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};


// --- The Main Course Detail Page Component ---
export default function CourseDetailPage() {
  const params = useParams();
  const courseId = params.courseId as string;
  const { user } = useAuth();

  const [course, setCourse] = useState<any>(null);
  const [lectures, setLectures] = useState<any[]>([]);
  const [progress, setProgress] = useState<any>(null);
  const [error, setError] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);

  // --- NEW STATE ---
  // State to store the IDs of completed lectures for the current student
  const [completedLectureIds, setCompletedLectureIds] = useState<number[]>([]);

  const fetchCourseData = async () => {
    if (!courseId) return;
    try {
      // --- UPDATED DATA FETCHING ---
      const [courseData, lecturesData, progressData, completedLecturesData] = await Promise.all([
        api.getCourseById(courseId),
        api.getLecturesForCourse(courseId),
        api.getCourseProgress(courseId),
        // Only fetch completed lectures if the user is a student
        user?.role === 'STUDENT' ? api.getCompletedLectures(courseId) : Promise.resolve([])
      ]);
      setCourse(courseData);
      setLectures(lecturesData.lectures);
      setProgress(progressData);
      setCompletedLectureIds(completedLecturesData); // Set the new state
    } catch (err: any) {
      setError(err.message);
    }
  };

  useEffect(() => {
    if (user) { // Ensure user is loaded before fetching
      fetchCourseData();
    }
  }, [courseId, user]);

  const handleAddSuccess = () => {
    setShowAddModal(false);
    fetchCourseData();
  };

  if (error) return <p className="text-red-500">{error}</p>;
  if (!course) return <p>Loading course details...</p>;

  const isCourseOwner = user?.role === 'INSTRUCTOR' && user?.name === course.instructorName;

  return (
    <div>
      {/* ... (keep the header and progress bar sections as they are) ... */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-4xl font-bold">{course.title}</h1>
          <p className="text-lg text-gray-600">by {course.instructorName}</p>
        </div>
        {isCourseOwner && (
          <button
            onClick={() => setShowAddModal(true)}
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
          >
            Add Lecture/Quiz
          </button>
        )}
      </div>

      {user?.role === 'STUDENT' && progress && (
        <div className="mb-6">
          <h3 className="text-xl font-semibold">Your Progress</h3>
          <div className="w-full bg-gray-200 rounded-full h-4 mt-2">
            <div
              className="bg-blue-600 h-4 rounded-full"
              style={{ width: `${(progress.completed / progress.total) * 100}%` }}
            ></div>
          </div>
          <p className="text-right text-gray-500 mt-1">{progress.display}</p>
        </div>
      )}

      <h2 className="text-2xl font-bold mt-8 mb-4">Lectures</h2>
      <div className="space-y-3">
        {/* --- UPDATED RENDERING LOGIC --- */}
        {lectures.map((lecture, index) => {
          // Check if the current lecture has been completed by the student
          const isCurrentLectureCompleted = completedLectureIds.includes(lecture.id);

          // A lecture is unlocked if it's the first one, OR if the previous one has been completed.
          const isUnlocked = index === 0 || completedLectureIds.includes(lectures[index - 1].id);

          const linkContent = (
             <div className="flex items-center justify-between p-4 bg-white rounded-lg border shadow-sm">
                <div className="flex items-center">
                    <span className="text-lg font-semibold mr-4">{index + 1}.</span>
                    <div>
                        <p className="text-xl font-medium">{lecture.title}</p>
                        <span className={`text-sm font-semibold px-2 py-0.5 rounded-full ${lecture.type === 'QUIZ' ? 'bg-yellow-200 text-yellow-800' : 'bg-green-200 text-green-800'}`}>
                            {lecture.type}
                        </span>
                    </div>
                </div>
                {isCurrentLectureCompleted && (
                    <svg className="w-6 h-6 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                )}
            </div>
          );

          // If the user is a student and the lecture is locked, render a disabled div.
          // Instructors can always access all lectures.
          if (user?.role === 'STUDENT' && !isUnlocked) {
            return (
              <div key={lecture.id} className="opacity-50 cursor-not-allowed">
                {linkContent}
              </div>
            );
          } else {
            // Otherwise, render a clickable Next.js Link
            return (
              <Link key={lecture.id} href={`/courses/${courseId}/lectures/${lecture.id}`} className="hover:ring-2 hover:ring-blue-500 rounded-lg transition-all">
                {linkContent}
              </Link>
            );
          }
        })}
      </div>
      
      {showAddModal && (
        <AddLectureModal
          courseId={courseId}
          onSuccess={handleAddSuccess}
          onClose={() => setShowAddModal(false)}
        />
      )}
    </div>
  );
}