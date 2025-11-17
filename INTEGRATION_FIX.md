# Backend to Frontend Integration - Quick Fix Guide

## Issue Fixed ✅
Removed old Next.js API routes (`src/app/api/`) that were conflicting with the Java Spring Boot backend.

## How It Works Now

### Before (❌ Not Working):
```
Frontend (localhost:3000) → /api/auth/register 
  → Next.js API Route (trying to use Prisma) 
  → Error: Prisma not initialized
```

### After (✅ Working):
```
Frontend (localhost:3000) → /api/auth/register 
  → Next.js Proxy (rewrites in next.config.ts)
  → Java Spring Boot (localhost:8080/api/auth/register)
  → PostgreSQL Database
```

## Steps to Test

1. **Restart Frontend** (if not already running):
   ```bash
   npm run dev
   ```

2. **Verify Backend is Running**:
   ```bash
   curl http://localhost:8080/api/auth/register
   ```
   Should return a 400 error (expected - no body provided)

3. **Test Registration** in browser:
   - Go to `http://localhost:3000/auth/register`
   - Fill in the form:
     - Email: test@example.com
     - Username: testuser
     - Password: Test123!
     - Confirm Password: Test123!
   - Click "Create Account"

## What Changed

### Deleted Files:
- `/src/app/api/auth/login/route.ts`
- `/src/app/api/auth/register/route.ts`
- `/src/app/api/auth/logout/route.ts`
- `/src/app/api/transactions/**`
- `/src/app/api/budgets/**`
- `/src/app/api/analytics/**`
- `/src/app/api/categories/**`
- `/src/app/api/user/**`

### Configuration (Already in place):
**next.config.ts** now proxies all `/api/*` requests to Java backend:
```typescript
async rewrites() {
  return [
    {
      source: '/api/:path*',
      destination: 'http://localhost:8080/api/:path*',
    },
  ]
}
```

## Expected Behavior

### Registration Flow:
1. User submits form on frontend
2. Frontend POSTs to `/api/auth/register`
3. Next.js proxies to `http://localhost:8080/api/auth/register`
4. Java Spring Boot:
   - Validates request
   - Hashes password with BCrypt
   - Saves user to PostgreSQL
   - Generates JWT token
   - Returns `{ token, user }` 
5. Frontend receives response and redirects to dashboard

### Response Format:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "uuid-here",
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

## Troubleshooting

### Still Getting Errors?

1. **Check Backend Logs**:
   Look at the terminal where Java is running for error messages

2. **Verify Both Services Running**:
   - Backend: `http://localhost:8080/api/` should be accessible
   - Frontend: `http://localhost:3000/` should be accessible

3. **Clear Next.js Cache**:
   ```bash
   rm -rf .next
   npm run dev
   ```

4. **Check Browser Console**:
   Open Developer Tools → Console to see actual error messages

5. **Test Backend Directly**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","username":"testuser","password":"Test123!"}'
   ```

## Common Issues

### CORS Errors
- Backend already configured to allow `http://localhost:3000`
- No changes needed

### JWT Token Not Saved
- Frontend needs to store token in localStorage or cookies
- May need to update frontend auth handling

### 401 Unauthorized on Other Endpoints
- Need to include JWT token in Authorization header:
  ```javascript
  headers: {
    'Authorization': `Bearer ${token}`
  }
  ```

## Next Steps

After successful registration:
1. Update frontend to save JWT token
2. Add token to subsequent API requests
3. Test other endpoints (transactions, budgets, etc.)
4. Implement proper error handling in frontend
