export interface DailyReport {
  date: string;
  numberOfUsers: number;
  numberOfCorrectGuesses: number;
}

export interface UserReport {
  userId: string;
  username: string;
  date: string;
  numberOfWordsTried: number;
  numberOfCorrectGuesses: number;
}

export interface PlayerUser {
  id: string;
  username: string;
  role: string;
}

export interface DailyConsistencyPoint {
  date: string;
  gamesPlayed: number;
  gamesWon: number;
  activityLevel: number; // 0, 1, 2, 3
}

export interface LetterHeatmapStat {
  letter: string;
  timesGuessed: number;
  greenCount: number;
  orangeCount: number;
  greyCount: number;
  accuracyRate: number;
}

export interface PlatformOverview {
  totalPlayers: number;
  totalGames: number;
  totalWins: number;
  globalWinRate: string;
  activePlayersToday: number;
  gamesPlayedToday: number;
  winsToday: number;
}

export interface DateRangeReportRow {
  date: string;
  usersCount: number;
  gamesCount: number;
  correctCount: number;
  winRate: string;
}

export interface AdminUserDirectoryItem {
  id: string;
  name: string;
  username: string;
  role: string;
  gamesCount: number;
  winsCount: number;
  winRate: string;
  joinedDate: string;
}
