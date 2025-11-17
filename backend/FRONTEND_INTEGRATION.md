# Frontend Integration Guide

## Overview

This guide explains how to integrate your Next.js frontend with the new Spring Boot backend.

## Changes Required in Next.js Frontend

### 1. Update API Base URL

Create or update `src/lib/api.ts`:

```typescript
// Base API URL
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Helper function to get auth token
export const getAuthToken = () => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('authToken');
  }
  return null;
};

// Helper function to make authenticated requests
export async function fetchWithAuth(url: string, options: RequestInit = {}) {
  const token = getAuthToken();
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers,
  });
  
  if (response.status === 401) {
    // Token expired or invalid
    localStorage.removeItem('authToken');
    window.location.href = '/auth/login';
    throw new Error('Unauthorized');
  }
  
  return response;
}
```

### 2. Update Authentication Flow

Update `src/lib/auth.ts`:

```typescript
import { API_BASE_URL } from './api';

export async function login(username: string, password: string) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password }),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Login failed');
  }
  
  const data = await response.json();
  
  // Store token
  localStorage.setItem('authToken', data.token);
  localStorage.setItem('user', JSON.stringify(data.user));
  
  return data;
}

export async function register(username: string, email: string, password: string) {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, email, password }),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Registration failed');
  }
  
  const data = await response.json();
  
  // Store token
  localStorage.setItem('authToken', data.token);
  localStorage.setItem('user', JSON.stringify(data.user));
  
  return data;
}

export function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  window.location.href = '/auth/login';
}

export function getCurrentUser() {
  if (typeof window !== 'undefined') {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
  return null;
}
```

### 3. Update Transaction API Calls

Example: `src/services/transactions.ts`:

```typescript
import { fetchWithAuth } from '@/lib/api';

export async function getTransactions() {
  const response = await fetchWithAuth('/transactions');
  if (!response.ok) throw new Error('Failed to fetch transactions');
  return response.json();
}

export async function createTransaction(data: TransactionRequest) {
  const response = await fetchWithAuth('/transactions', {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to create transaction');
  return response.json();
}

export async function updateTransaction(id: string, data: TransactionRequest) {
  const response = await fetchWithAuth(`/transactions/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to update transaction');
  return response.json();
}

export async function deleteTransaction(id: string) {
  const response = await fetchWithAuth(`/transactions/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete transaction');
}
```

### 4. Update Type Definitions

Update `src/types/index.ts` to match backend DTOs:

```typescript
export interface User {
  id: string
  username: string
  email: string
}

export interface Transaction {
  id: string
  amount: number
  description: string
  category: string
  type: 'INCOME' | 'EXPENSE'  // Changed from lowercase
  date: string  // ISO datetime string
  createdAt: string
  updatedAt: string
}

export interface TransactionRequest {
  amount: number
  description: string
  category: string
  type: 'INCOME' | 'EXPENSE'
  date: string  // ISO datetime string
}

export interface Budget {
  id: string
  category: string
  amount: number
  spent: number
  remaining: number
  percentageUsed: number
  month: number
  year: number
}

export interface BudgetRequest {
  category: string
  amount: number
  month: number
  year: number
}

export interface Stats {
  totalIncome: number
  totalExpenses: number
  balance: number
  monthlyIncome: number
  monthlyExpenses: number
}

export interface CategoryAnalytics {
  category: string
  amount: number
  count: number
  percentage: number
}

export interface MonthlyAnalytics {
  month: string
  amount: number
}
```

### 5. Update Middleware

Update `src/middleware.ts`:

```typescript
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  const token = request.cookies.get('authToken')?.value || 
                request.headers.get('authorization')?.replace('Bearer ', '')
  
  // Public routes
  const publicPaths = ['/auth/login', '/auth/register']
  const isPublicPath = publicPaths.some(path => request.nextUrl.pathname.startsWith(path))
  
  // If accessing protected route without token, redirect to login
  if (!isPublicPath && !token) {
    return NextResponse.redirect(new URL('/auth/login', request.url))
  }
  
  // If accessing auth pages with token, redirect to dashboard
  if (isPublicPath && token) {
    return NextResponse.redirect(new URL('/dashboard', request.url))
  }
  
  return NextResponse.next()
}

export const config = {
  matcher: [
    '/dashboard/:path*',
    '/transactions/:path*',
    '/budget/:path*',
    '/analytics/:path*',
    '/auth/:path*'
  ]
}
```

### 6. Environment Variables

Create or update `.env.local`:

```bash
# Spring Boot Backend URL
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

For production:

```bash
NEXT_PUBLIC_API_URL=https://your-backend-domain.com/api
```

### 7. Update Login Page Component

Example for `src/app/auth/login/page.tsx`:

```typescript
'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { login } from '@/lib/auth'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await login(username, password)
      router.push('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    // Your JSX here
  )
}
```

## Key Differences from Next.js API Routes

### 1. **Base URL**
- **Before**: `/api/...` (relative)
- **After**: `http://localhost:8080/api/...` (absolute)

### 2. **Authentication**
- **Before**: Cookie-based with middleware
- **After**: JWT token in `Authorization: Bearer <token>` header

### 3. **Data Types**
- **Before**: Type is `'income' | 'expense'` (lowercase)
- **After**: Type is `'INCOME' | 'EXPENSE'` (uppercase enum)

### 4. **Date Format**
- Ensure dates are sent as ISO 8601 strings: `2025-11-15T14:30:00`

### 5. **Error Handling**
- Backend returns structured error responses with status codes
- Handle 401 (Unauthorized) to redirect to login
- Handle validation errors (400) with detailed field errors

## Testing the Integration

1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start Frontend**:
   ```bash
   npm run dev
   ```

3. **Test Flow**:
   - Register a new user
   - Login and verify token storage
   - Create a transaction
   - View dashboard with analytics

## CORS Configuration

The backend is configured to accept requests from `http://localhost:3000`.

For production, update `application.properties`:

```properties
cors.allowed-origins=https://your-frontend-domain.com
```

## Troubleshooting

### 1. CORS Errors
- Ensure backend CORS is configured correctly
- Check that frontend uses correct API URL

### 2. Authentication Errors
- Verify token is stored in localStorage
- Check Authorization header format: `Bearer <token>`
- Ensure token hasn't expired (7 days by default)

### 3. Type Mismatches
- Update frontend types to match backend DTOs
- Use uppercase for transaction types (INCOME/EXPENSE)

## Next Steps

1. ✅ Remove old Next.js API routes from `src/app/api/`
2. ✅ Update all frontend components to use new API
3. ✅ Test all features end-to-end
4. ✅ Deploy backend separately from frontend
5. ✅ Update environment variables for production

---

**Need Help?** Check the backend README and API documentation for detailed endpoint information.
