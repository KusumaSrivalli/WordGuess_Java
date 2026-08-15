package com.wordguess.dto;

import java.time.LocalDate;

public class DailyReportDto {
    private LocalDate date;
    private long numberOfUsers;
    private long numberOfCorrectGuesses;

    public DailyReportDto() {}

    public DailyReportDto(LocalDate date, long numberOfUsers, long numberOfCorrectGuesses) {
        this.date = date;
        this.numberOfUsers = numberOfUsers;
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(long numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public long getNumberOfCorrectGuesses() {
        return numberOfCorrectGuesses;
    }

    public void setNumberOfCorrectGuesses(long numberOfCorrectGuesses) {
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
    }
}
