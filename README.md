# Guess the Word — Full-Stack Application

A full-stack web application built with **Angular** (Frontend), **Spring Boot** (Backend), and **MongoDB** (Database) based on the "Guess the Word" specification.

---

## 🚀 Specifications & Features Implemented

### 1. User Roles & Authentication
- **User Roles**: `ADMIN` (runs reports & views analytics) and `PLAYER` (plays the word guess game).
- **Registration Validation**:
  - **Username**: Must be at least 5 letters containing both upper & lower case characters (`(?=.*[a-z])(?=.*[A-Z])[a-zA-Z]{5,}`).
  - **Password**: Must be at least 5 characters containing alpha, numeric, and at least one special character (`$`, `%`, `*`, `&`).

### 2. MongoDB Database & 20 Seeded Words
- **Initial Database Seeding**: Automatically seeds 20 uppercase 5-letter English words on application startup:
  `APPLE`, `HOUSE`, `SMART`, `PLANT`, `TRAIN`, `GRAPE`, `WATER`, `BRAIN`, `CLOUD`, `FLAME`, `LIGHT`, `MUSIC`, `DREAM`, `SHINE`, `STORM`, `BEACH`, `TIGER`, `SWEET`, `GREEN`, `CANDY`.
- Default Admin & Player credentials initialized automatically for instant testing:
  - **Admin**: Username: `AdminUser` | Password: `Admin1$`
  - **Player**: Username: `PlayerOne` | Password: `Player1*`

### 3. Gameplay Logic & Rules
- **Daily Limit**: Users are limited to guessing a maximum of **3 words per day**.
- **Word Guess Arena**:
  - Picked randomly from database per game session.
  - Up to **5 attempts** allowed per session.
  - Submissions must be 5 uppercase letters.
  - **Feedback Color System**:
    - 🟩 **GREEN**: Letter is correct and in the right position.
    - 🟧 **ORANGE**: Letter is correct but in the wrong position.
    - ⬛ **GREY**: Letter is not in the word.
  - **On-Screen & Physical Keyboard Support**: Dynamic key color feedback.
  - **Win Dialog**: Displays congratulatory modal message with target word and OK button to close session.
  - **Loss Dialog**: Displays 'Better luck next time' modal message on 5 failed attempts with OK button to close session.
  - **Game Sequence Persistence**: All past attempts and color feedback stored in MongoDB per session with date.

### 4. Admin Reports Portal
- **Daily Report**: Filter by date to view the number of distinct active users and total number of correct word guesses.
- **User Report**: Select any player to view date-wise breakdown of words attempted, correct guesses, and success rate.

---

## 🛠️ Project Structure

```
WordGuess_Java/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/wordguess/
│       │   │   ├── WordGuessApplication.java
│       │   │   ├── config/ (CorsConfig.java, DataSeeder.java)
│       │   │   ├── controller/ (AuthController, GameController, ReportController)
│       │   │   ├── dto/ (AuthResponse, LoginRequest, RegisterRequest, GuessRequest, etc.)
│       │   │   ├── model/ (User, Word, GameSession, GuessAttempt, LetterStatus)
│       │   │   ├── repository/ (UserRepository, WordRepository, GameSessionRepository)
│       │   │   └── service/ (AuthService, GameService, ReportService)
│       │   └── resources/
│       │       └── application.properties
├── frontend/
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   └── src/
│       ├── index.html
│       ├── main.ts
│       ├── styles.css
│       └── app/
│           ├── app.component.ts
│           ├── app.routes.ts
│           ├── app.config.ts
│           ├── models/ (auth.model, game.model, report.model)
│           ├── services/ (auth.service, game.service, report.service)
│           └── components/
│               ├── navbar/
│               ├── login/
│               ├── register/
│               ├── game/
│               └── admin-reports/
└── README.md
```

---

## 💻 Running the Application

### 1. Prerequisites
- **Java 17+** & **Maven**
- **Node.js 18+** & **npm**
- **MongoDB** running on `mongodb://localhost:27017` (or updated in `backend/src/main/resources/application.properties`)

### 2. Start Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
*The Spring Boot server will run on `http://localhost:8080`.*

### 3. Start Frontend (Angular)
```bash
cd frontend
npm install
npm start
```
*The Angular client will run on `http://localhost:4200`.*
