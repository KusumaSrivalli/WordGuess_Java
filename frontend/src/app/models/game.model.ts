export type LetterStatus = 'GREEN' | 'ORANGE' | 'GREY';

export interface GuessAttempt {
  guessedWord: string;
  feedback: LetterStatus[];
}

export interface GameStatusResponse {
  gamesPlayedToday: number;
  remainingGamesToday: number;
  hasActiveSession: boolean;
  activeSessionId?: string;
  attempts?: GuessAttempt[];
}

export interface StartGameResponse {
  sessionId: string;
  status: 'IN_PROGRESS' | 'WON' | 'LOST';
  attempts: GuessAttempt[];
  gamesPlayedToday: number;
  remainingGamesToday: number;
}

export interface GuessResult {
  sessionId: string;
  status: 'IN_PROGRESS' | 'WON' | 'LOST';
  attemptNumber: number;
  remainingAttempts: number;
  previousAttempts: GuessAttempt[];
  message: string;
  targetWord?: string;
}
