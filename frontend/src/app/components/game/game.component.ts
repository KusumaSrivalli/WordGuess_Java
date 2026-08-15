import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { GameService } from '../../services/game.service';
import { ReportService } from '../../services/report.service';
import { GuessAttempt, LetterStatus } from '../../models/game.model';
import { DailyConsistencyPoint } from '../../models/report.model';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {
  currentUserId: string = '';
  currentUsername: string = '';
  sessionId: string | null = null;
  currentGuess: string = '';
  attempts: GuessAttempt[] = [];
  gameStatus: 'IDLE' | 'IN_PROGRESS' | 'WON' | 'LOST' = 'IDLE';

  viewMode: 'DASHBOARD' | 'PLAY' = 'DASHBOARD';

  gamesPlayedToday: number = 0;
  remainingGamesToday: number = 3;

  errorMessage: string = '';
  llmAlertMessage: string = '';
  showWinModal: boolean = false;
  showLossModal: boolean = false;
  revealedTargetWord: string = '';

  // Dashboard Stats State
  consistencyHeatmap: DailyConsistencyPoint[] = [];
  activeStreak: number = 0;
  totalGamesPlayedMonth: number = 0;
  totalWinsMonth: number = 0;
  winRateMonth: string = '0.0';

  keyboardRows: string[][] = [
    ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'],
    ['A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'],
    ['ENTER', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '⌫']
  ];

  letterKeyStatus: { [key: string]: LetterStatus } = {};

  constructor(
    private authService: AuthService,
    private gameService: GameService,
    private reportService: ReportService,
    private router: Router
  ) {}

  ngOnInit() {
    const user = this.authService.currentUserValue;
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }
    this.currentUserId = user.userId;
    this.currentUsername = user.username;
    this.checkGameStatus();
    this.loadUserHeatmaps();
  }

  get isStartDisabled(): boolean {
    return this.remainingGamesToday <= 0 && this.gameStatus !== 'IN_PROGRESS';
  }

  get boardGrid(): { char: string; status: LetterStatus | 'EMPTY' }[][] {
    const grid: { char: string; status: LetterStatus | 'EMPTY' }[][] = [];
    for (let rowIndex = 0; rowIndex < 5; rowIndex++) {
      const row: { char: string; status: LetterStatus | 'EMPTY' }[] = [];
      if (rowIndex < this.attempts.length) {
        const att = this.attempts[rowIndex];
        for (let i = 0; i < 5; i++) {
          row.push({ char: att.guessedWord[i], status: att.feedback[i] });
        }
      } else if (rowIndex === this.attempts.length && this.gameStatus === 'IN_PROGRESS') {
        for (let i = 0; i < 5; i++) {
          const char = this.currentGuess[i] || '';
          row.push({ char, status: 'EMPTY' });
        }
      } else {
        for (let i = 0; i < 5; i++) {
          row.push({ char: '', status: 'EMPTY' });
        }
      }
      grid.push(row);
    }
    return grid;
  }

  checkGameStatus() {
    this.gameService.getGameStatus(this.currentUserId).subscribe({
      next: (res) => {
        this.gamesPlayedToday = res.gamesPlayedToday;
        this.remainingGamesToday = res.remainingGamesToday;

        if (res.hasActiveSession && res.activeSessionId) {
          this.sessionId = res.activeSessionId;
          this.attempts = res.attempts || [];
          this.gameStatus = 'IN_PROGRESS';
          this.updateKeyboardStatus();
        }
      }
    });
  }

  loadUserHeatmaps() {
    this.reportService.getUserConsistencyHeatmap(this.currentUserId).subscribe({
      next: (pts) => {
        this.consistencyHeatmap = pts;
        this.calculateStreakAndStats(pts);
      }
    });
  }

  calculateStreakAndStats(pts: DailyConsistencyPoint[]) {
    this.totalGamesPlayedMonth = pts.reduce((sum, p) => sum + p.gamesPlayed, 0);
    this.totalWinsMonth = pts.reduce((sum, p) => sum + p.gamesWon, 0);
    this.winRateMonth = this.totalGamesPlayedMonth > 0
      ? ((this.totalWinsMonth / this.totalGamesPlayedMonth) * 100).toFixed(1)
      : '0.0';

    let streak = 0;
    for (let i = pts.length - 1; i >= 0; i--) {
      if (pts[i].gamesPlayed > 0) {
        streak++;
      } else {
        if (i < pts.length - 1) break;
      }
    }
    this.activeStreak = streak;
  }

  onStartOrResumeClick() {
    if (this.gameStatus === 'IN_PROGRESS' && this.sessionId) {
      this.viewMode = 'PLAY';
    } else if (this.remainingGamesToday > 0) {
      this.startNewGame();
    }
  }

  startNewGame() {
    this.errorMessage = '';
    this.llmAlertMessage = '';
    this.showWinModal = false;
    this.showLossModal = false;
    this.currentGuess = '';
    this.attempts = [];
    this.letterKeyStatus = {};

    this.gameService.startNewGame(this.currentUserId).subscribe({
      next: (res) => {
        this.sessionId = res.sessionId;
        this.gameStatus = 'IN_PROGRESS';
        this.gamesPlayedToday = res.gamesPlayedToday;
        this.remainingGamesToday = res.remainingGamesToday;
        this.viewMode = 'PLAY';
      },
      error: (err) => {
        this.errorMessage = err.message || 'Could not start new game.';
      }
    });
  }

  backToDashboard() {
    this.viewMode = 'DASHBOARD';
    this.checkGameStatus();
    this.loadUserHeatmaps();
  }

  submitGuess() {
    if (!this.sessionId || this.gameStatus !== 'IN_PROGRESS') return;

    if (this.currentGuess.length !== 5) {
      this.errorMessage = 'Please enter a complete 5-letter word.';
      return;
    }

    this.errorMessage = '';
    this.llmAlertMessage = '';
    const wordToSubmit = this.currentGuess.toUpperCase();

    this.gameService.submitGuess(this.sessionId, wordToSubmit).subscribe({
      next: (res) => {
        this.attempts = res.previousAttempts;
        this.updateKeyboardStatus();
        this.currentGuess = '';

        if (res.status === 'WON') {
          this.gameStatus = 'WON';
          this.revealedTargetWord = res.targetWord || wordToSubmit;
          this.showWinModal = true;
          this.loadUserHeatmaps();
        } else if (res.status === 'LOST') {
          this.gameStatus = 'LOST';
          this.revealedTargetWord = res.targetWord || '';
          this.showLossModal = true;
          this.loadUserHeatmaps();
        }
      },
      error: (err) => {
        const msg = err.message || '';
        if (msg.includes('INVALID_WORD')) {
          this.llmAlertMessage = `🤖 LLM Validation: '${wordToSubmit}' is not a valid 5-letter English word. Please enter a real word!`;
        } else {
          this.errorMessage = msg || 'Submission failed.';
        }
      }
    });
  }

  onKeyClick(key: string) {
    if (this.gameStatus !== 'IN_PROGRESS' || this.viewMode !== 'PLAY') return;

    if (key === 'ENTER') {
      this.submitGuess();
    } else if (key === '⌫' || key === 'BACKSPACE') {
      if (this.currentGuess.length > 0) {
        this.currentGuess = this.currentGuess.slice(0, -1);
      }
    } else if (/^[A-Z]$/i.test(key) && this.currentGuess.length < 5) {
      this.currentGuess += key.toUpperCase();
    }
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (this.gameStatus !== 'IN_PROGRESS' || this.viewMode !== 'PLAY') return;

    const key = event.key.toUpperCase();
    if (key === 'ENTER') {
      this.submitGuess();
    } else if (key === 'BACKSPACE') {
      this.onKeyClick('⌫');
    } else if (/^[A-Z]$/.test(key) && key.length === 1) {
      this.onKeyClick(key);
    }
  }

  updateKeyboardStatus() {
    this.attempts.forEach(att => {
      att.guessedWord.split('').forEach((char, idx) => {
        const status = att.feedback[idx];
        const current = this.letterKeyStatus[char];

        if (status === 'GREEN') {
          this.letterKeyStatus[char] = 'GREEN';
        } else if (status === 'ORANGE' && current !== 'GREEN') {
          this.letterKeyStatus[char] = 'ORANGE';
        } else if (status === 'GREY' && !current) {
          this.letterKeyStatus[char] = 'GREY';
        }
      });
    });
  }

  closeModal(type: 'WIN' | 'LOSS') {
    if (type === 'WIN') this.showWinModal = false;
    if (type === 'LOSS') this.showLossModal = false;

    this.checkGameStatus();
    this.loadUserHeatmaps();
    this.viewMode = 'DASHBOARD';
  }
}
