package com.wordguess.model;

public enum LetterStatus {
    GREEN,   // Correct letter & right position
    ORANGE,  // Correct letter & wrong position
    GREY     // Letter not in the word
}
