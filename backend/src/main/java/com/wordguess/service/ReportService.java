package com.wordguess.service;

import com.wordguess.dto.*;
import com.wordguess.model.GameSession;
import com.wordguess.model.User;
import com.wordguess.repository.GameSessionRepository;
import com.wordguess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportService(GameSessionRepository gameSessionRepository, UserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Platform Overview: All-time & Today stats matching Image 1
     */
    public PlatformOverviewDto getPlatformOverview() {
        List<User> allUsers = userRepository.findAll();
        List<GameSession> allSessions = gameSessionRepository.findAll();

        long totalPlayers = allUsers.stream().filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole())).count();
        long totalGames = allSessions.size();
        long totalWins = allSessions.stream().filter(s -> "WON".equals(s.getStatus())).count();
        String globalWinRate = totalGames > 0
                ? String.format("%.1f%%", ((double) totalWins / totalGames) * 100.0)
                : "0.0%";

        LocalDate today = LocalDate.now();
        List<GameSession> todaySessions = gameSessionRepository.findByPlayDate(today);

        long activePlayersToday = todaySessions.stream().map(GameSession::getUserId).distinct().count();
        long gamesPlayedToday = todaySessions.size();
        long winsToday = todaySessions.stream().filter(s -> "WON".equals(s.getStatus())).count();

        return new PlatformOverviewDto(
                totalPlayers, totalGames, totalWins, globalWinRate,
                activePlayersToday, gamesPlayedToday, winsToday
        );
    }

    /**
     * Daily Report: Number of distinct users who played on specified date,
     * and number of correct guesses (won games) on that date.
     */
    public DailyReportDto getDailyReport(LocalDate date) {
        List<GameSession> sessions = gameSessionRepository.findByPlayDate(date);

        long distinctUsers = sessions.stream()
                .map(GameSession::getUserId)
                .distinct()
                .count();

        long correctGuesses = sessions.stream()
                .filter(s -> "WON".equals(s.getStatus()))
                .count();

        return new DailyReportDto(date, distinctUsers, correctGuesses);
    }

    /**
     * Date Range Report: Summary rows for each day between startDate and endDate matching Image 3
     */
    public List<DateRangeReportRowDto> getDateRangeReport(LocalDate startDate, LocalDate endDate) {
        List<DateRangeReportRowDto> list = new ArrayList<>();
        LocalDate curr = startDate;

        while (!curr.isAfter(endDate)) {
            List<GameSession> sessions = gameSessionRepository.findByPlayDate(curr);
            long users = sessions.stream().map(GameSession::getUserId).distinct().count();
            long games = sessions.size();
            long correct = sessions.stream().filter(s -> "WON".equals(s.getStatus())).count();
            String winRate = games > 0 ? String.format("%.1f%%", ((double) correct / games) * 100.0) : "0.0%";

            list.add(new DateRangeReportRowDto(curr, users, games, correct, winRate));
            curr = curr.plusDays(1);
        }

        return list;
    }

    /**
     * Admin Users Directory: List of all users matching Image 4 & 5
     */
    public List<UserDetailAdminDto> getAdminUsersDirectory() {
        List<User> allUsers = userRepository.findAll();
        List<UserDetailAdminDto> dtoList = new ArrayList<>();

        for (User u : allUsers) {
            List<GameSession> userSessions = gameSessionRepository.findByUserId(u.getId());
            long games = userSessions.size();
            long wins = userSessions.stream().filter(s -> "WON".equals(s.getStatus())).count();
            String winRate = games > 0 ? String.format("%.1f%%", ((double) wins / games) * 100.0) : "0.0%";

            String roleStr = (u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN")) ? "admin" : "player";
            dtoList.add(new UserDetailAdminDto(
                    u.getId(),
                    u.getUsername(),
                    "@" + u.getUsername(),
                    roleStr,
                    games,
                    wins,
                    winRate,
                    u.getJoinedDate()
            ));
        }

        return dtoList;
    }

    public User updateUser(String id, String newUsername, String newRole) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            user.setUsername(newUsername.trim());
        }
        if (newRole != null && !newRole.trim().isEmpty()) {
            user.setRole(newRole.trim().toUpperCase());
        }
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    /**
     * User Report: List of daily statistics for a specific user.
     */
    public List<UserReportDto> getUserReport(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<GameSession> userSessions = gameSessionRepository.findByUserId(userId);

        Map<LocalDate, List<GameSession>> sessionsByDate = userSessions.stream()
                .collect(Collectors.groupingBy(GameSession::getPlayDate));

        List<UserReportDto> reportList = new ArrayList<>();
        for (Map.Entry<LocalDate, List<GameSession>> entry : sessionsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<GameSession> daySessions = entry.getValue();

            long wordsTried = daySessions.size();
            long correctGuesses = daySessions.stream()
                    .filter(s -> "WON".equals(s.getStatus()))
                    .count();

            reportList.add(new UserReportDto(user.getId(), user.getUsername(), date, wordsTried, correctGuesses));
        }

        reportList.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return reportList;
    }

    public List<User> getAllPlayers() {
        return userRepository.findAll().stream()
                .filter(u -> "PLAYER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * Daily Consistency Heatmap: Returns activity level (0-3 games) for each day over past 30 days.
     */
    public List<DailyConsistencyDto> getUserConsistencyHeatmap(String userId) {
        List<GameSession> userSessions = gameSessionRepository.findByUserId(userId);
        Map<LocalDate, List<GameSession>> sessionsByDate = userSessions.stream()
                .collect(Collectors.groupingBy(GameSession::getPlayDate));

        LocalDate today = LocalDate.now();
        List<DailyConsistencyDto> result = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<GameSession> daySessions = sessionsByDate.getOrDefault(date, Collections.emptyList());
            long gamesPlayed = daySessions.size();
            long gamesWon = daySessions.stream().filter(s -> "WON".equals(s.getStatus())).count();
            int level = (int) Math.min(3, gamesPlayed);

            result.add(new DailyConsistencyDto(date, gamesPlayed, gamesWon, level));
        }

        return result;
    }

    /**
     * User Letter Heatmap: Calculates times guessed, green/orange/grey counts and accuracy for letters A-Z.
     */
    public List<LetterHeatmapDto> getUserLetterHeatmap(String userId) {
        List<GameSession> userSessions = gameSessionRepository.findByUserId(userId);

        Map<String, int[]> statsMap = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            statsMap.put(String.valueOf(c), new int[]{0, 0, 0, 0});
        }

        for (GameSession session : userSessions) {
            if (session.getAttempts() == null) continue;
            for (com.wordguess.model.GuessAttempt att : session.getAttempts()) {
                if (att.getGuessedWord() == null || att.getFeedback() == null) continue;
                String word = att.getGuessedWord().toUpperCase();
                List<com.wordguess.model.LetterStatus> feedback = att.getFeedback();

                for (int i = 0; i < Math.min(word.length(), feedback.size()); i++) {
                    String charStr = String.valueOf(word.charAt(i));
                    com.wordguess.model.LetterStatus status = feedback.get(i);
                    int[] arr = statsMap.get(charStr);
                    if (arr != null) {
                        arr[0]++;
                        if (status == com.wordguess.model.LetterStatus.GREEN) arr[1]++;
                        else if (status == com.wordguess.model.LetterStatus.ORANGE) arr[2]++;
                        else if (status == com.wordguess.model.LetterStatus.GREY) arr[3]++;
                    }
                }
            }
        }

        List<LetterHeatmapDto> result = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            String letter = String.valueOf(c);
            int[] arr = statsMap.get(letter);
            int total = arr[0];
            int green = arr[1];
            int orange = arr[2];
            int grey = arr[3];

            double acc = total > 0 ? ((green * 1.0 + orange * 0.5) / total) * 100.0 : 0.0;
            result.add(new LetterHeatmapDto(letter, total, green, orange, grey, Math.round(acc * 10.0) / 10.0));
        }

        return result;
    }
}
