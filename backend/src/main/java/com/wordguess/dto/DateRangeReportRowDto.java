package com.wordguess.dto;

import java.time.LocalDate;

public class DateRangeReportRowDto {
    private LocalDate date;
    private long usersCount;
    private long gamesCount;
    private long correctCount;
    private String winRate;

    public DateRangeReportRowDto() {}

    public DateRangeReportRowDto(LocalDate date, long usersCount, long gamesCount, long correctCount, String winRate) {
        this.date = date;
        this.usersCount = usersCount;
        this.gamesCount = gamesCount;
        this.correctCount = correctCount;
        this.winRate = winRate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(long usersCount) {
        this.usersCount = usersCount;
    }

    public long getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(long gamesCount) {
        this.gamesCount = gamesCount;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(long correctCount) {
        this.correctCount = correctCount;
    }

    public String getWinRate() {
        return winRate;
    }

    public void setWinRate(String winRate) {
        this.winRate = winRate;
    }
}
