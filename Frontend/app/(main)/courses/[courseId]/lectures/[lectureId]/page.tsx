"use client";

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import * as api from '@/lib/api';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth';

// --- Reusable Component for Reading Lectures ---
const ReadingLecture = ({ lecture, courseId, onComplete }: any) => {
  const router = useRouter();
  const handleMarkComplete = async () => {
    try {
      await api.markReadingComplete(courseId, lecture.id);
      alert('Lecture marked as complete!');
      router.refresh();
      onComplete();
    } catch (err: any) {
      alert(`Error: ${err.message}`);
    }
  };

  return (
    <div>
      <div className="prose lg:prose-xl mt-4 p-4 border rounded bg-gray-50">
        {/* --- TEXT COLOR FIX --- */}
        <p className="text-gray-900">{lecture.text}</p>
        {lecture.linkUrl && <a href={lecture.linkUrl} target="_blank" rel="noopener noreferrer">External Link</a>}
      </div>
      <button onClick={handleMarkComplete} className="mt-6 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
        Mark as Complete
      </button>
    </div>
  );
};

// --- Reusable Component for Quiz Lectures ---
const QuizLecture = ({ lecture, courseId, onComplete }: any) => {
  const router = useRouter();
  const [selectedIndices, setSelectedIndices] = useState<{ [key: number]: number }>({});
  const [result, setResult] = useState<any>(null);

  const handleOptionChange = (questionIndex: number, optionIndex: number) => {
    setSelectedIndices({
      ...selectedIndices,
      [questionIndex]: optionIndex,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const answers = lecture.questions.map((_: any, index: number) => selectedIndices[index] ?? null);
    try {
      const quizResult = await api.submitQuiz(courseId, lecture.id, { selectedIndices: answers });
      setResult(quizResult);
      if (quizResult.passed) {
        router.refresh();
        onComplete();
      }
    } catch (err: any) {
      alert(`Error submitting quiz: ${err.message}`);
    }
  };

  if (result) {
    return (
      <div className={`p-6 rounded mt-6 ${result.passed ? 'bg-green-100 border-green-500' : 'bg-red-100 border-red-500'} border`}>
        {/* --- TEXT COLOR FIX --- */}
        <h3 className="text-2xl font-bold text-gray-900">Quiz Results</h3>
        <p className="text-gray-900">Your Score: {result.scorePercent.toFixed(2)}%</p>
        <p className="text-gray-900">Correct Answers: {result.correctAnswers} / {result.totalQuestions}</p>
        <p className="font-bold mt-2 text-gray-900">{result.passed ? "Congratulations, you passed!" : "Please try again."}</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit}>
      {lecture.questions.map((q: any, qIndex: number) => (
        <div key={q.id} className="my-6 p-4 border rounded">
          {/* --- TEXT COLOR FIX --- */}
          <p className="font-semibold text-lg text-gray-900">{qIndex + 1}. {q.text}</p>
          <div className="mt-2 space-y-2">
            {q.options.map((option: string, oIndex: number) => (
              <label key={oIndex} className="flex items-center space-x-2 p-2 rounded hover:bg-gray-100 cursor-pointer">
                <input
                  type="radio"
                  name={`question-${qIndex}`}
                  value={oIndex}
                  onChange={() => handleOptionChange(qIndex, oIndex)}
                  checked={selectedIndices[qIndex] === oIndex}
                  className="form-radio h-5 w-5 text-blue-600"
                />
                {/* --- TEXT COLOR FIX --- */}
                <span className="text-gray-900">{option}</span>
              </label>
            ))}
          </div>
        </div>
      ))}
      <button type="submit" className="mt-6 bg-green-500 hover:bg-green-700 text-white font-bold py-3 px-6 rounded text-lg">
        Submit Quiz
      </button>
    </form>
  );
};


// --- Main Page Component ---
export default function LecturePage() {
  const params = useParams();
  const router = useRouter();
  const courseId = params.courseId as string;
  const lectureId = params.lectureId as string;
  const { user } = useAuth();

  const [lecture, setLecture] = useState<any>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!courseId || !lectureId) return;
    api.getLectureDetail(courseId, lectureId)
      .then(data => setLecture(data))
      .catch(err => setError(err.message));
  }, [courseId, lectureId]);

  const handleCompletion = () => {
    window.location.href = `/courses/${courseId}`;
  };

  if (error) return <p className="text-red-500">{error}</p>;
  if (!lecture) return <p>Loading lecture...</p>;

  return (
    <div>
      <Link href={`/courses/${courseId}`} className="text-blue-500 hover:underline mb-4 block">&larr; Back to Course</Link>
      {/* --- TEXT COLOR FIX --- */}
      <h1 className="text-4xl font-bold text-white">{lecture.title}</h1>
      <span className={`text-sm font-semibold px-2 py-0.5 rounded-full ${lecture.type === 'QUIZ' ? 'bg-yellow-200 text-yellow-800' : 'bg-green-200 text-green-800'}`}>
        {lecture.type}
      </span>

      {user?.role === 'STUDENT' && lecture.type === 'READING' && (
        <ReadingLecture lecture={lecture} courseId={courseId} onComplete={handleCompletion} />
      )}
      
      {user?.role === 'STUDENT' && lecture.type === 'QUIZ' && (
        <QuizLecture lecture={lecture} courseId={courseId} onComplete={handleCompletion} />
      )}

      {user?.role === 'INSTRUCTOR' && (
        <div className="mt-6 p-4 bg-gray-100 border rounded">
            {/* --- TEXT COLOR FIX --- */}
            <h3 className="text-xl font-bold text-gray-900">Instructor View</h3>
            {lecture.type === 'READING' && <p className="text-gray-900">{lecture.text}</p>}
            {lecture.type === 'QUIZ' && (
                <div className="mt-2">
                    <p className="text-gray-900">{lecture.questions.length} questions in this quiz.</p>
                </div>
            )}
        </div>
      )}
    </div>
  );
}