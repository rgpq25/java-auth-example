# Auth Example — Full Stack Project

A full-stack authentication example with a Spring Boot backend and a React (Vite + Tailwind) frontend.
The frontend consumes backend endpoints for testing and demonstration.

## 📁 Project Structure
.
├── backend/                     # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── README.md (this one)
│
├── frontend/                    # React + Vite + Tailwind app
│   ├── src/
│   ├── package.json
│   └── vite.config.js
│
└── README.md                    # root documentation

## ⚙️ Backend — Spring Boot
### 🧠 Overview

The backend is a Spring Boot project that exposes REST APIs for authentication and testing.
It uses JPA with PostgreSQL and follows a multi-profile configuration (dev, prod).

### 🏗️ Tech Stack

Java 17+

Spring Boot 3+

Spring Data JPA

PostgreSQL

Maven

Profiles: dev, prod

### 🧾 Environment Setup
1. Create your dev config

In src/main/resources/, copy the example file:

cp application-example.properties application-dev.properties

Then fill in the necessary credentials.

2. Run the backend

Using Maven:

cd backend
./mvnw spring-boot:run

Or from your IDE (IntelliJ / VS Code):
run the AuthExampleApplication class.

The backend should start on:

http://localhost:8080


## 🎨 Frontend — Vite + React + Tailwind
### 🧠 Overview

A lightweight frontend built with Vite and React, styled using TailwindCSS.
Used to test and visualize the backend authentication flow.

### ⚙️ Setup
cd frontend
npm install
npm run dev

The frontend runs on:

http://localhost:5173

## 📜 License

MIT — feel free to use and modify.