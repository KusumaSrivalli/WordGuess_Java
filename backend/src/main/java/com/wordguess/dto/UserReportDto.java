package com.wordguess.dto;

import java.time.LocalDate;

public class UserReportDto {
    private String userId;
    private String username;
    private LocalDate date;
    private long numberOfWordsTried;
    private long numberOfCorrectGuesses;

    public UserReportDto() {}

    public UserReportDto(String userId, String username, LocalDate date, long numberOfWordsTried, long numberOfCorrectGuesses) {
        this.userId = userId;
        this.username = username;
        this.date = date;
        this.numberOfWordsTried = numberOfWordsTried;
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getNumberOfWordsTried() {
        return numberOfWordsTried;
    }

    public void setNumberOfWordsTried(long numberOfWordsTried) {
        this.numberOfWordsTried = numberOfWordsTried;
    }

    public long getNumberOfCorrectGuesses() {
        return numberOfCorrectGuesses;
    }

    public void setNumberOfCorrectGuesses(long numberOfCorrectGuesses) {
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
    }
}
