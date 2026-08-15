package com.wordguess.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "game_sessions")
public class GameSession {

    @Id
    private String id;
    private String userId;
    private String username;
    private String targetWord;
    private List<GuessAttempt> attempts = new ArrayList<>();
    private String status; // "IN_PROGRESS", "WON", "LOST"
    private LocalDate playDate;

    public GameSession() {}

    public GameSession(String userId, String username, String targetWord, LocalDate playDate) {
        this.userId = userId;
        this.username = username;
        this.targetWord = targetWord;
        this.playDate = playDate;
        this.status = "IN_PROGRESS";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord;
    }

    public List<GuessAttempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<GuessAttempt> attempts) {
        this.attempts = attempts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPlayDate() {
        return playDate;
    }

    public void setPlayDate(LocalDate playDate) {
        this.playDate = playDate;
    }
}
