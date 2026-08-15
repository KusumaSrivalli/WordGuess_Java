package com.wordguess.dto;

public class LetterHeatmapDto {
    private String letter;
    private int timesGuessed;
    private int greenCount;
    private int orangeCount;
    private int greyCount;
    private double accuracyRate; // % Green + 0.5*Orange

    public LetterHeatmapDto() {}

    public LetterHeatmapDto(String letter, int timesGuessed, int greenCount, int orangeCount, int greyCount, double accuracyRate) {
        this.letter = letter;
        this.timesGuessed = timesGuessed;
        this.greenCount = greenCount;
        this.orangeCount = orangeCount;
        this.greyCount = greyCount;
        this.accuracyRate = accuracyRate;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getTimesGuessed() {
        return timesGuessed;
    }

    public void setTimesGuessed(int timesGuessed) {
        this.timesGuessed = timesGuessed;
    }

    public int getGreenCount() {
        return greenCount;
    }

    public void setGreenCount(int greenCount) {
        this.greenCount = greenCount;
    }

    public int getOrangeCount() {
        return orangeCount;
    }

    public void setOrangeCount(int orangeCount) {
        this.orangeCount = orangeCount;
    }

    public int getGreyCount() {
        return greyCount;
    }

    public void setGreyCount(int greyCount) {
        this.greyCount = greyCount;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }
}
