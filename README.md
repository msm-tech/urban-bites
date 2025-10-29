# Urban Bites - Full Stack Restaurant Application

A complete food delivery application built with Spring Boot backend and React frontend.

## 🚀 Features
- User authentication (JWT)
- Menu browsing
- Order management
- Responsive design

## 🛠️ Tech Stack
**Backend:** Spring Boot, PostgreSQL, JWT, Spring Security  
**Frontend:** React, Context API, CSS3

## 📁 Project Structure
urban-bites/
├── backend/ # Spring Boot REST API
├── frontend/ # React application
└── README.md

## ▶️ Running frontend + backend together (development)

This repository contains both the backend (Spring Boot) and frontend (React) projects. The simplest way to run both at the same time is to use the helper scripts included at the repository root:

- Windows (cmd): `run-dev.cmd` — opens two new terminal windows (Backend and Frontend) and starts each service.
- Unix (bash): `run-dev.sh` — runs both services (backend in background) and keeps the frontend in the foreground.

Prerequisites
- Java 11+ and Maven (or use the bundled `mvnw`/`mvnw.cmd` in `backend/`).
- Node.js 14+ and npm (or Yarn) in `frontend/`.

Default ports
- Backend: 8080 (Spring Boot default)
- Frontend: 3000 (Create React App default)

Quick start (Windows)

Open PowerShell or Command Prompt at the repo root and run:

```cmd
run-dev.cmd
```

Quick start (Unix/macOS)

Make the script executable (once) and run it:

```bash
chmod +x run-dev.sh
./run-dev.sh
```

Notes and tips
- The Windows script will open two new terminal windows so you can view logs separately; close them to stop the services.
- If you prefer a single-terminal approach on Windows, open two tabs manually and run the backend and frontend commands below:

Backend (from repo root):

```cmd
cd backend
mvnw.cmd spring-boot:run
```

Frontend (from repo root):

```cmd
cd frontend
npm install
npm start
```

If you change ports or add proxied APIs, update `frontend/package.json` (proxy) or Spring Boot properties under `backend/src/main/resources`.
