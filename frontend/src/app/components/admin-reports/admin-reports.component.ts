import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ReportService } from '../../services/report.service';
import {
  DailyReport, UserReport, PlayerUser, DailyConsistencyPoint,
  LetterHeatmapStat, PlatformOverview, DateRangeReportRow, AdminUserDirectoryItem
} from '../../models/report.model';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  activeTab: 'OVERVIEW' | 'DAILY' | 'USER_REPORT' | 'USERS' = 'OVERVIEW';

  currentAdminName: string = 'Admin';
  currentAdminInitial: string = 'A';

  // Overview State (Image 1)
  overview: PlatformOverview = {
    totalPlayers: 12,
    totalGames: 36,
    totalWins: 12,
    globalWinRate: '33.3%',
    activePlayersToday: 3,
    gamesPlayedToday: 7,
    winsToday: 3
  };
  isLoadingOverview = false;

  // Daily Report State (Image 2 & 3)
  dailyReportMode: 'SINGLE_DAY' | 'DATE_RANGE' = 'SINGLE_DAY';
  selectedDate: string = '2026-08-15';
  dailyReport: DailyReport = {
    date: '2026-08-15',
    numberOfUsers: 3,
    numberOfCorrectGuesses: 3
  };
  dailyTotalGames: number = 3;
  dailyWinRate: string = '66.7%';

  // Date Range State (Image 3)
  rangeStartDate: string = '2026-08-08';
  rangeEndDate: string = '2026-08-15';
  dateRangeRows: DateRangeReportRow[] = [
    { date: '2026-08-05', usersCount: 1, gamesCount: 2, correctCount: 0, winRate: '0.0%' },
    { date: '2026-08-10', usersCount: 2, gamesCount: 4, correctCount: 3, winRate: '75.0%' },
    { date: '2026-08-11', usersCount: 6, gamesCount: 12, correctCount: 4, winRate: '33.3%' },
    { date: '2026-08-12', usersCount: 2, gamesCount: 4, correctCount: 0, winRate: '0.0%' },
    { date: '2026-08-13', usersCount: 1, gamesCount: 2, correctCount: 0, winRate: '0.0%' },
    { date: '2026-08-14', usersCount: 4, gamesCount: 8, correctCount: 2, winRate: '25.0%' },
    { date: '2026-08-15', usersCount: 3, gamesCount: 6, correctCount: 3, winRate: '50.0%' }
  ];
  isLoadingRange = false;

  // Users Directory State (Image 4 & 5)
  userDirectory: AdminUserDirectoryItem[] = [];
  filteredUsers: AdminUserDirectoryItem[] = [];
  userSearchQuery: string = '';
  roleFilter: string = 'all';
  activeActionMenuId: string | null = null;

  // Edit User Modal
  showEditModal: boolean = false;
  editingUser: { id: string; name: string; role: string } = { id: '', name: '', role: 'player' };

  // User Report / Heatmap State
  players: PlayerUser[] = [];
  selectedUserId: string = '';
  selectedPlayerName: string = 'Sairohan';
  userReportList: UserReport[] = [];
  userConsistencyPoints: DailyConsistencyPoint[] = [];
  playerActiveStreak: number = 0;
  isLoadingUser = false;

  constructor(
    private authService: AuthService,
    private reportService: ReportService,
    private router: Router
  ) {}

  ngOnInit() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.currentAdminName = currentUser.username || 'Shobit';
      this.currentAdminInitial = (currentUser.username || 'S').charAt(0).toUpperCase();
    }

    this.fetchOverview();
    this.fetchDailyReport();
    this.fetchUsersDirectory();
    this.fetchPlayers();
  }

  fetchOverview() {
    this.isLoadingOverview = true;
    this.reportService.getPlatformOverview().subscribe({
      next: (res) => {
        this.overview = res;
        this.isLoadingOverview = false;
      },
      error: () => { this.isLoadingOverview = false; }
    });
  }

  fetchDailyReport() {
    this.reportService.getDailyReport(this.selectedDate).subscribe({
      next: (res) => {
        this.dailyReport = res;
        // Total games calculation for single day
        this.dailyTotalGames = Math.max(res.numberOfCorrectGuesses, res.numberOfUsers * 1);
        const rate = this.dailyTotalGames > 0 ? (res.numberOfCorrectGuesses / this.dailyTotalGames) * 100 : 0;
        this.dailyWinRate = rate.toFixed(1) + '%';
      }
    });
  }

  fetchDateRangeReport() {
    this.isLoadingRange = true;
    this.reportService.getDateRangeReport(this.rangeStartDate, this.rangeEndDate).subscribe({
      next: (res) => {
        this.dateRangeRows = res;
        this.isLoadingRange = false;
      },
      error: () => { this.isLoadingRange = false; }
    });
  }

  fetchUsersDirectory() {
    this.reportService.getAdminUsersDirectory().subscribe({
      next: (res) => {
        this.userDirectory = res;
        this.applyUsersFilter();
      }
    });
  }

  applyUsersFilter() {
    this.filteredUsers = this.userDirectory.filter(u => {
      const matchesSearch = u.name.toLowerCase().includes(this.userSearchQuery.toLowerCase()) ||
                            u.username.toLowerCase().includes(this.userSearchQuery.toLowerCase());
      const matchesRole = this.roleFilter === 'all' || u.role.toLowerCase() === this.roleFilter.toLowerCase();
      return matchesSearch && matchesRole;
    });
  }

  toggleActionMenu(userId: string, event: Event) {
    event.stopPropagation();
    if (this.activeActionMenuId === userId) {
      this.activeActionMenuId = null;
    } else {
      this.activeActionMenuId = userId;
    }
  }

  closeActionMenus() {
    this.activeActionMenuId = null;
  }

  openReportForUser(u: AdminUserDirectoryItem) {
    this.closeActionMenus();
    this.selectedUserId = u.id;
    this.selectedPlayerName = u.name;
    this.activeTab = 'USER_REPORT';
    this.fetchUserReportData();
  }

  openEditUserModal(u: AdminUserDirectoryItem) {
    this.closeActionMenus();
    this.editingUser = { id: u.id, name: u.name, role: u.role };
    this.showEditModal = true;
  }

  saveUserEdit() {
    if (!this.editingUser.id) return;
    this.reportService.updateUser(this.editingUser.id, this.editingUser.name, this.editingUser.role).subscribe({
      next: () => {
        this.showEditModal = false;
        this.fetchUsersDirectory();
      }
    });
  }

  deleteUser(u: AdminUserDirectoryItem) {
    this.closeActionMenus();
    if (confirm(`Are you sure you want to delete user @${u.name}?`)) {
      this.reportService.deleteUser(u.id).subscribe({
        next: () => {
          this.fetchUsersDirectory();
        }
      });
    }
  }

  fetchPlayers() {
    this.reportService.getAllPlayers().subscribe({
      next: (res) => {
        this.players = res;
        if (this.players.length > 0 && !this.selectedUserId) {
          this.selectedUserId = this.players[0].id;
          this.fetchUserReportData();
        }
      }
    });
  }

  fetchUserReportData() {
    if (!this.selectedUserId) return;
    this.isLoadingUser = true;

    const p = this.players.find(x => x.id === this.selectedUserId);
    if (p) this.selectedPlayerName = p.username;

    this.reportService.getUserReport(this.selectedUserId).subscribe({
      next: (res) => {
        this.userReportList = res;
        this.isLoadingUser = false;
      }
    });

    this.reportService.getUserConsistencyHeatmap(this.selectedUserId).subscribe({
      next: (pts) => {
        this.userConsistencyPoints = pts;
        let streak = 0;
        for (let i = pts.length - 1; i >= 0; i--) {
          if (pts[i].gamesPlayed > 0) streak++;
          else if (i < pts.length - 1) break;
        }
        this.playerActiveStreak = streak;
      }
    });
  }

  goToPlayGame() {
    this.router.navigate(['/game']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
