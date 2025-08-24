'use client';
import { ReactNode, useEffect } from 'react';
import { useAuthStore } from '@/lib/store';
import { useRouter } from 'next/navigation';


export default function Protected({ children }: { children: ReactNode }) {
const token = useAuthStore((s) => s.token);
const router = useRouter();


useEffect(() => {
if (!token) router.push('/login');
}, [token, router]);


if (!token) return null; // or a spinner
return <>{children}</>;
}