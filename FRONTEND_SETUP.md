# Frontend Setup Guide

## Prerequisites
- Node.js and npm installed
- Java Spring Boot backend running on port 8080

## Quick Start

### 1. Install Dependencies
```bash
npm install
```

### 2. Start the Development Server
```bash
npm run dev
```

The frontend will be available at: **http://localhost:3000**

## Configuration

### API Proxy Setup
The Next.js configuration (`next.config.ts`) has been updated to proxy all `/api/*` requests to the Java Spring Boot backend running at `http://localhost:8080/api`.

This means:
- Frontend calls to `/api/auth/login` → Proxied to `http://localhost:8080/api/auth/login`
- Frontend calls to `/api/transactions` → Proxied to `http://localhost:8080/api/transactions`
- And so on...

### Environment Variables (Optional)
Create a `.env.local` file if you need to customize settings:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_APP_URL=http://localhost:3000
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run ESLint

## Important Notes

⚠️ **Backend Must Be Running First**
Make sure your Java Spring Boot backend is running on port 8080 before starting the frontend.

To check if backend is running:
```bash
curl http://localhost:8080/api/auth/login
```

## Troubleshooting

### Port Already in Use
If port 3000 is already in use, you can start on a different port:
```bash
PORT=3001 npm run dev
```

### API Connection Issues
1. Verify backend is running: `curl http://localhost:8080/api/health` (if health endpoint exists)
2. Check browser console for CORS errors
3. Ensure `next.config.ts` proxy configuration is correct

### CORS Issues
If you encounter CORS errors, the Spring Boot backend already has CORS configured in `SecurityConfig.java` to allow requests from `http://localhost:3000`.

## Authentication Flow

1. User registers/logs in via frontend
2. Java backend returns JWT token
3. Token is stored (likely in cookies or localStorage)
4. Subsequent API requests include the token in headers
5. Backend validates token and processes requests

## Database

The frontend no longer connects directly to the database. All data operations go through the Java Spring Boot REST API.

The old Prisma setup in the frontend is no longer needed for API routes, but you can keep it if you have any server-side rendering requirements.
