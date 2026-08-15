package com.wordguess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class GuessRequest {

    private String sessionId;

    @NotBlank(message = "Guessed word is required")
    @Pattern(regexp = "^[A-Z]{5}$", message = "Guessed word must be exactly 5 uppercase letters")
    private String guessedWord;

    public GuessRequest() {}

    public GuessRequest(String sessionId, String guessedWord) {
        this.sessionId = sessionId;
        this.guessedWord = guessedWord;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGuessedWord() {
        return guessedWord;
    }

    public void setGuessedWord(String guessedWord) {
        this.guessedWord = guessedWord;
    }
}
