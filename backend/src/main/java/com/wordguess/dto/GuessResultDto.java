package com.wordguess.dto;

import com.wordguess.model.GuessAttempt;

import java.util.List;

public class GuessResultDto {
    private String sessionId;
    private String status; // "IN_PROGRESS", "WON", "LOST"
    private int attemptNumber;
    private int remainingAttempts;
    private List<GuessAttempt> previousAttempts;
    private String message;
    private String targetWord; // revealed only on WON or LOST

    private long gamesPlayedToday;
    private long remainingGamesToday;

    public GuessResultDto() {}

    public long getGamesPlayedToday() {
        return gamesPlayedToday;
    }

    public void setGamesPlayedToday(long gamesPlayedToday) {
        this.gamesPlayedToday = gamesPlayedToday;
    }

    public long getRemainingGamesToday() {
        return remainingGamesToday;
    }

    public void setRemainingGamesToday(long remainingGamesToday) {
        this.remainingGamesToday = remainingGamesToday;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public List<GuessAttempt> getPreviousAttempts() {
        return previousAttempts;
    }

    public void setPreviousAttempts(List<GuessAttempt> previousAttempts) {
        this.previousAttempts = previousAttempts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord;
    }
}
