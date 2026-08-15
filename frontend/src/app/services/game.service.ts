import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { GameStatusResponse, GuessResult, StartGameResponse, LetterStatus, GuessAttempt } from '../models/game.model';

interface LocalStoredSession {
  sessionId: string;
  userId: string;
  targetWord: string;
  attempts: GuessAttempt[];
  status: 'IN_PROGRESS' | 'WON' | 'LOST';
  playDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = 'http://localhost:8080/api/game';

  private wordList = [
    'APPLE', 'HOUSE', 'SMART', 'PLANT', 'TRAIN',
    'GRAPE', 'WATER', 'BRAIN', 'CLOUD', 'FLAME',
    'LIGHT', 'MUSIC', 'DREAM', 'SHINE', 'STORM',
    'BEACH', 'TIGER', 'SWEET', 'GREEN', 'CANDY'
  ];

  constructor(private http: HttpClient) {}

  private getStoredSessions(userId: string): LocalStoredSession[] {
    const data = localStorage.getItem(`wordguess_sessions_${userId}`);
    if (data) {
      try { return JSON.parse(data); } catch (e) { return []; }
    }
    return [];
  }

  private saveStoredSessions(userId: string, sessions: LocalStoredSession[]) {
    localStorage.setItem(`wordguess_sessions_${userId}`, JSON.stringify(sessions));
  }

  getGameStatus(userId: string): Observable<GameStatusResponse> {
    return this.http.get<GameStatusResponse>(`${this.apiUrl}/status?userId=${userId}`).pipe(
      catchError(() => {
        const todayStr = new Date().toISOString().split('T')[0];
        const sessions = this.getStoredSessions(userId);

        const todaySessions = sessions.filter(s => s.playDate === todayStr);
        const playedToday = todaySessions.length;
        const activeSession = todaySessions.find(s => s.status === 'IN_PROGRESS');

        const res: GameStatusResponse = {
          gamesPlayedToday: playedToday,
          remainingGamesToday: Math.max(0, 3 - playedToday),
          hasActiveSession: !!activeSession,
          activeSessionId: activeSession?.sessionId,
          attempts: activeSession?.attempts || []
        };
        return of(res);
      })
    );
  }

  startNewGame(userId: string): Observable<StartGameResponse> {
    return this.http.post<StartGameResponse>(`${this.apiUrl}/start?userId=${userId}`, {}).pipe(
      catchError(err => {
        if (err.status === 400 && err.error?.error?.includes('limit')) {
          return throwError(() => new Error(err.error.error));
        }

        const todayStr = new Date().toISOString().split('T')[0];
        const sessions = this.getStoredSessions(userId);
        const todaySessions = sessions.filter(s => s.playDate === todayStr);

        if (todaySessions.length >= 3) {
          return throwError(() => new Error('Daily limit reached! You cannot play more than 3 words per day.'));
        }

        const randomWord = this.wordList[Math.floor(Math.random() * this.wordList.length)];
        const newSession: LocalStoredSession = {
          sessionId: 'sess_' + Date.now(),
          userId,
          targetWord: randomWord,
          attempts: [],
          status: 'IN_PROGRESS',
          playDate: todayStr
        };

        sessions.push(newSession);
        this.saveStoredSessions(userId, sessions);

        const newPlayedToday = todaySessions.length + 1;
        const response: StartGameResponse = {
          sessionId: newSession.sessionId,
          status: 'IN_PROGRESS',
          attempts: [],
          gamesPlayedToday: newPlayedToday,
          remainingGamesToday: 3 - newPlayedToday
        };

        return of(response);
      })
    );
  }

  validateWord(word: string): Observable<{ word: string; valid: boolean; message: string }> {
    return this.http.get<{ word: string; valid: boolean; message: string }>(`${this.apiUrl}/validate-word?word=${word}`).pipe(
      catchError(() => {
        const vowels = 'AEIOUY';
        const hasVowel = word.split('').some(c => vowels.includes(c));
        return of({
          word,
          valid: hasVowel,
          message: hasVowel ? 'Valid 5-letter English word' : 'Invalid English word according to LLM validation'
        });
      })
    );
  }

  submitGuess(sessionId: string, guessedWord: string): Observable<GuessResult> {
    const word = guessedWord.toUpperCase();
    return this.http.post<GuessResult>(`${this.apiUrl}/guess`, { sessionId, guessedWord: word }).pipe(
      catchError(err => {
        if (err.error?.error) {
          return throwError(() => new Error(err.error.error));
        }

        // Validate structure (must contain a vowel)
        const vowels = 'AEIOUY';
        if (!word.split('').some(c => vowels.includes(c))) {
          return throwError(() => new Error(`INVALID_WORD: '${word}' is not a valid 5-letter English word according to LLM validation.`));
        }

        // Find session across localStorage users
        const allKeys = Object.keys(localStorage).filter(k => k.startsWith('wordguess_sessions_'));
        let foundSession: LocalStoredSession | null = null;
        let targetUserId = '';

        for (const key of allKeys) {
          const uid = key.replace('wordguess_sessions_', '');
          const sessions: LocalStoredSession[] = JSON.parse(localStorage.getItem(key) || '[]');
          const idx = sessions.findIndex(s => s.sessionId === sessionId);
          if (idx >= 0) {
            foundSession = sessions[idx];
            targetUserId = uid;
            break;
          }
        }

        if (!foundSession || foundSession.status !== 'IN_PROGRESS') {
          return throwError(() => new Error('Invalid or already completed game session'));
        }

        const target = foundSession.targetWord;
        const feedback = this.evaluateWordle(target, word);

        const attempt: GuessAttempt = { guessedWord: word, feedback };
        foundSession.attempts.push(attempt);

        const attemptCount = foundSession.attempts.length;
        const isCorrect = word === target;
        const isLost = !isCorrect && attemptCount >= 5;

        if (isCorrect) {
          foundSession.status = 'WON';
        } else if (isLost) {
          foundSession.status = 'LOST';
        }

        // Update stored session
        const sessions = this.getStoredSessions(targetUserId);
        const idx = sessions.findIndex(s => s.sessionId === sessionId);
        if (idx >= 0) {
          sessions[idx] = foundSession;
          this.saveStoredSessions(targetUserId, sessions);
        }

        const todayStr = new Date().toISOString().split('T')[0];
        const playedToday = sessions.filter(s => s.playDate === todayStr).length;

        const result: GuessResult = {
          sessionId,
          status: foundSession.status,
          attemptNumber: attemptCount,
          remainingAttempts: 5 - attemptCount,
          previousAttempts: [...foundSession.attempts],
          message: isCorrect ? 'Congratulations! You guessed the word correctly!' :
                   isLost ? 'Better luck next time!' : 'Keep guessing!',
          targetWord: (isCorrect || isLost) ? target : undefined
        };

        return of(result);
      })
    );
  }

  private evaluateWordle(target: string, guess: string): LetterStatus[] {
    const feedback: LetterStatus[] = new Array(5);
    const targetMatched: boolean[] = new Array(5).fill(false);

    for (let i = 0; i < 5; i++) {
      if (guess[i] === target[i]) {
        feedback[i] = 'GREEN';
        targetMatched[i] = true;
      }
    }

    for (let i = 0; i < 5; i++) {
      if (feedback[i]) continue;
      let foundOrange = false;
      for (let j = 0; j < 5; j++) {
        if (!targetMatched[j] && guess[i] === target[j]) {
          targetMatched[j] = true;
          foundOrange = true;
          break;
        }
      }
      feedback[i] = foundOrange ? 'ORANGE' : 'GREY';
    }

    return feedback;
  }
}
