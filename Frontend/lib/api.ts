const API_URL = process.env.NEXT_PUBLIC_API_URL;

async function fetcher(path: string, options: RequestInit = {}) {
  const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
  
  const headers = new Headers(options.headers || {});
  headers.set('Content-Type', 'application/json');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || 'An error occurred');
  }

  // For 200 OK responses with no body (e.g., register success)
  if (response.headers.get('content-length') === '0' || response.status === 204) {
    return {};
  }
    
  return response.json();
}

// AUTH
export const registerUser = (data: any) => fetcher('/auth/register', { method: 'POST', body: JSON.stringify(data) });
export const loginUser = (data: any) => fetcher('/auth/login', { method: 'POST', body: JSON.stringify(data) });

// COURSES
export const getCourses = () => fetcher('/courses');
export const getCourseById = (courseId: string) => fetcher(`/courses/${courseId}`);
export const createCourse = (data: any) => fetcher('/courses', { method: 'POST', body: JSON.stringify(data) });

// LECTURES
export const getLecturesForCourse = (courseId: string) => fetcher(`/courses/${courseId}/lectures`);
export const getLectureDetail = (courseId: string, lectureId: string) => fetcher(`/courses/${courseId}/lectures/${lectureId}`);
export const createLecture = (courseId: string, data: any) => fetcher(`/courses/${courseId}/lectures`, { method: 'POST', body: JSON.stringify(data) });

// PROGRESS & COMPLETION
export const markReadingComplete = (courseId: string, lectureId: string) => fetcher(`/courses/${courseId}/lectures/${lectureId}/complete`, { method: 'POST' });
export const submitQuiz = (courseId: string, lectureId: string, data: any) => fetcher(`/courses/${courseId}/lectures/${lectureId}/quiz/submit`, { method: 'POST', body: JSON.stringify(data) });
export const getCourseProgress = (courseId: string) => fetcher(`/courses/${courseId}/progress`);
export const getCompletedLectures = (courseId: string): Promise<number[]> => fetcher(`/courses/${courseId}/completed-lectures`);
