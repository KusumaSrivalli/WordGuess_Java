package com.wordguess.dto;

import java.time.LocalDate;

public class DailyConsistencyDto {
    private LocalDate date;
    private long gamesPlayed;
    private long gamesWon;
    private int activityLevel; // 0 = none, 1 = 1 game, 2 = 2 games, 3 = 3 games (max)

    public DailyConsistencyDto() {}

    public DailyConsistencyDto(LocalDate date, long gamesPlayed, long gamesWon, int activityLevel) {
        this.date = date;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.activityLevel = activityLevel;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public long getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(long gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }
}
