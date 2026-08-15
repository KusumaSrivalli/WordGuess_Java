package com.wordguess.model;

import java.util.List;

public class GuessAttempt {
    private String guessedWord;
    private List<LetterStatus> feedback;

    public GuessAttempt() {}

    public GuessAttempt(String guessedWord, List<LetterStatus> feedback) {
        this.guessedWord = guessedWord;
        this.feedback = feedback;
    }

    public String getGuessedWord() {
        return guessedWord;
    }

    public void setGuessedWord(String guessedWord) {
        this.guessedWord = guessedWord;
    }

    public List<LetterStatus> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<LetterStatus> feedback) {
        this.feedback = feedback;
    }
}
