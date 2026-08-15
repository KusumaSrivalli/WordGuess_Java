import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  DailyReport, UserReport, PlayerUser, DailyConsistencyPoint,
  LetterHeatmapStat, PlatformOverview, DateRangeReportRow, AdminUserDirectoryItem
} from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  getPlatformOverview(): Observable<PlatformOverview> {
    return this.http.get<PlatformOverview>(`${this.apiUrl}/overview`).pipe(
      catchError(() => {
        let allSessions: any[] = [];
        const allKeys = Object.keys(localStorage).filter(k => k.startsWith('wordguess_sessions_'));
        for (const key of allKeys) {
          try {
            const raw = localStorage.getItem(key);
            if (raw) allSessions = allSessions.concat(JSON.parse(raw));
          } catch (e) {}
        }

        const totalGames = allSessions.length;
        const totalWins = allSessions.filter(s => s.status === 'WON').length;
        const globalWinRate = totalGames > 0 ? ((totalWins / totalGames) * 100).toFixed(1) + '%' : '0.0%';

        const todayStr = new Date().toISOString().split('T')[0];
        const todaySessions = allSessions.filter(s => s.playDate === todayStr);

        return of({
          totalPlayers: Math.max(1, new Set(allSessions.map(s => s.userId)).size),
          totalGames,
          totalWins,
          globalWinRate,
          activePlayersToday: new Set(todaySessions.map(s => s.userId)).size,
          gamesPlayedToday: todaySessions.length,
          winsToday: todaySessions.filter(s => s.status === 'WON').length
        });
      })
    );
  }

  getDailyReport(date?: string): Observable<DailyReport> {
    const targetDate = date || new Date().toISOString().split('T')[0];
    const url = `${this.apiUrl}/daily?date=${targetDate}`;

    return this.http.get<DailyReport>(url).pipe(
      catchError(() => {
        let allSessions: any[] = [];
        const allKeys = Object.keys(localStorage).filter(k => k.startsWith('wordguess_sessions_'));
        for (const key of allKeys) {
          try {
            const raw = localStorage.getItem(key);
            if (raw) allSessions = allSessions.concat(JSON.parse(raw));
          } catch (e) {}
        }

        const daySessions = allSessions.filter(s => s.playDate === targetDate);
        const distinctUsers = new Set(daySessions.map(s => s.userId)).size;
        const wins = daySessions.filter(s => s.status === 'WON').length;

        return of({
          date: targetDate,
          numberOfUsers: distinctUsers,
          numberOfCorrectGuesses: wins
        });
      })
    );
  }

  getDateRangeReport(startDate: string, endDate: string): Observable<DateRangeReportRow[]> {
    return this.http.get<DateRangeReportRow[]>(`${this.apiUrl}/range?startDate=${startDate}&endDate=${endDate}`).pipe(
      catchError(() => {
        let allSessions: any[] = [];
        const allKeys = Object.keys(localStorage).filter(k => k.startsWith('wordguess_sessions_'));
        for (const key of allKeys) {
          try {
            const raw = localStorage.getItem(key);
            if (raw) allSessions = allSessions.concat(JSON.parse(raw));
          } catch (e) {}
        }

        const start = new Date(startDate);
        const end = new Date(endDate);
        const list: DateRangeReportRow[] = [];

        for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
          const dateStr = d.toISOString().split('T')[0];
          const daySessions = allSessions.filter(s => s.playDate === dateStr);
          const users = new Set(daySessions.map(s => s.userId)).size;
          const games = daySessions.length;
          const correct = daySessions.filter(s => s.status === 'WON').length;
          const winRate = games > 0 ? ((correct / games) * 100).toFixed(1) + '%' : '0.0%';

          list.push({ date: dateStr, usersCount: users, gamesCount: games, correctCount: correct, winRate });
        }

        return of(list);
      })
    );
  }

  getAdminUsersDirectory(): Observable<AdminUserDirectoryItem[]> {
    return this.http.get<AdminUserDirectoryItem[]>(`${this.apiUrl}/users-directory`).pipe(
      catchError(() => {
        const list: AdminUserDirectoryItem[] = [];
        const regRaw = localStorage.getItem('wordguess_registered_players');

        if (regRaw) {
          try {
            const regList = JSON.parse(regRaw);
            regList.forEach((u: any) => {
              let userSessions: any[] = [];
              try {
                const raw = localStorage.getItem(`wordguess_sessions_${u.userId}`);
                if (raw) userSessions = JSON.parse(raw);
              } catch (e) {}

              const games = userSessions.length;
              const wins = userSessions.filter(s => s.status === 'WON').length;
              const winRate = games > 0 ? ((wins / games) * 100).toFixed(1) + '%' : '0.0%';

              list.push({
                id: u.userId,
                name: u.username,
                username: '@' + u.username,
                role: u.role ? u.role.toLowerCase() : 'player',
                gamesCount: games,
                winsCount: wins,
                winRate,
                joinedDate: '2026-08-14'
              });
            });
          } catch (e) {}
        }

        if (list.length === 0) {
          list.push(
            { id: 'usr_admin1', name: 'Admin1', username: '@Admin1', role: 'admin', gamesCount: 0, winsCount: 0, winRate: '0.0%', joinedDate: '2026-08-14' },
            { id: 'usr_player1', name: 'Player1', username: '@Player1', role: 'player', gamesCount: 1, winsCount: 0, winRate: '0.0%', joinedDate: '2026-08-14' },
            { id: 'usr_srivalli', name: 'Srivalli', username: '@Srivalli', role: 'player', gamesCount: 0, winsCount: 0, winRate: '0.0%', joinedDate: '2026-08-14' },
            { id: 'usr_sairohan', name: 'Sairohan', username: '@Sairohan', role: 'player', gamesCount: 12, winsCount: 3, winRate: '25.0%', joinedDate: '2026-08-14' },
            { id: 'usr_kusuma', name: 'Kusuma', username: '@Kusuma', role: 'player', gamesCount: 8, winsCount: 5, winRate: '62.5%', joinedDate: '2026-08-14' }
          );
        }

        return of(list);
      })
    );
  }

  updateUser(id: string, username: string, role: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}`, { username, role }).pipe(
      catchError(() => of({ id, username, role }))
    );
  }

  deleteUser(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`).pipe(
      catchError(() => of({ success: true }))
    );
  }

  getUserReport(userId: string): Observable<UserReport[]> {
    return this.http.get<UserReport[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError(() => {
        let userSessions: any[] = [];
        try {
          const raw = localStorage.getItem(`wordguess_sessions_${userId}`);
          if (raw) userSessions = JSON.parse(raw);
        } catch (e) {}

        const userStr = localStorage.getItem('wordguess_user');
        let username = 'User';
        if (userStr) {
          try { username = JSON.parse(userStr).username; } catch (e) {}
        }

        const sessionsByDate: { [date: string]: any[] } = {};
        userSessions.forEach(s => {
          if (!sessionsByDate[s.playDate]) sessionsByDate[s.playDate] = [];
          sessionsByDate[s.playDate].push(s);
        });

        const reportList: UserReport[] = [];
        for (const dateStr of Object.keys(sessionsByDate)) {
          const daySessions = sessionsByDate[dateStr];
          const wordsTried = daySessions.length;
          const wins = daySessions.filter(s => s.status === 'WON').length;

          reportList.push({
            userId,
            username,
            date: dateStr,
            numberOfWordsTried: wordsTried,
            numberOfCorrectGuesses: wins
          });
        }

        reportList.sort((a, b) => b.date.localeCompare(a.date));
        return of(reportList);
      })
    );
  }

  getAllPlayers(): Observable<PlayerUser[]> {
    return this.http.get<PlayerUser[]>(`${this.apiUrl}/players`).pipe(
      catchError(() => {
        const users: PlayerUser[] = [];
        const regRaw = localStorage.getItem('wordguess_registered_players');
        if (regRaw) {
          try {
            const regList = JSON.parse(regRaw);
            regList.forEach((u: any) => {
              if (u.role !== 'ADMIN' && !users.some(existing => existing.id === u.userId)) {
                users.push({ id: u.userId, username: u.username, role: u.role || 'PLAYER' });
              }
            });
          } catch (e) {}
        }

        const currentUserData = localStorage.getItem('wordguess_user');
        if (currentUserData) {
          try {
            const u = JSON.parse(currentUserData);
            if (u.role !== 'ADMIN' && !users.some(existing => existing.id === u.userId)) {
              users.push({ id: u.userId, username: u.username, role: u.role || 'PLAYER' });
            }
          } catch (e) {}
        }

        if (!users.some(existing => existing.username.toLowerCase() === 'playerone')) {
          users.push({ id: 'usr_playerone', username: 'PlayerOne', role: 'PLAYER' });
        }

        return of(users);
      })
    );
  }

  getUserConsistencyHeatmap(userId: string): Observable<DailyConsistencyPoint[]> {
    return this.http.get<DailyConsistencyPoint[]>(`${this.apiUrl}/user/${userId}/consistency-heatmap`).pipe(
      catchError(() => {
        let userSessions: any[] = [];
        try {
          const raw = localStorage.getItem(`wordguess_sessions_${userId}`);
          if (raw) userSessions = JSON.parse(raw);
        } catch (e) {}

        const sessionsByDate: { [date: string]: any[] } = {};
        userSessions.forEach(s => {
          if (!sessionsByDate[s.playDate]) sessionsByDate[s.playDate] = [];
          sessionsByDate[s.playDate].push(s);
        });

        const list: DailyConsistencyPoint[] = [];
        const today = new Date();
        for (let i = 29; i >= 0; i--) {
          const d = new Date(today);
          d.setDate(d.getDate() - i);
          const dateStr = d.toISOString().split('T')[0];
          const daySessions = sessionsByDate[dateStr] || [];
          const played = daySessions.length;
          const won = daySessions.filter(s => s.status === 'WON').length;
          const level = Math.min(3, played);

          list.push({
            date: dateStr,
            gamesPlayed: played,
            gamesWon: won,
            activityLevel: level
          });
        }
        return of(list);
      })
    );
  }

  getUserLetterHeatmap(userId: string): Observable<LetterHeatmapStat[]> {
    return this.http.get<LetterHeatmapStat[]>(`${this.apiUrl}/user/${userId}/letter-heatmap`).pipe(
      catchError(() => {
        const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
        const list: LetterHeatmapStat[] = letters.map(char => ({
          letter: char,
          timesGuessed: 0,
          greenCount: 0,
          orangeCount: 0,
          greyCount: 0,
          accuracyRate: 0
        }));
        return of(list);
      })
    );
  }
}
