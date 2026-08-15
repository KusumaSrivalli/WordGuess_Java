package com.wordguess.dto;

import java.time.LocalDate;

public class UserDetailAdminDto {
    private String id;
    private String name;
    private String username;
    private String role;
    private long gamesCount;
    private long winsCount;
    private String winRate;
    private LocalDate joinedDate;

    public UserDetailAdminDto() {}

    public UserDetailAdminDto(String id, String name, String username, String role, long gamesCount, long winsCount, String winRate, LocalDate joinedDate) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.gamesCount = gamesCount;
        this.winsCount = winsCount;
        this.winRate = winRate;
        this.joinedDate = joinedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(long gamesCount) {
        this.gamesCount = gamesCount;
    }

    public long getWinsCount() {
        return winsCount;
    }

    public void setWinsCount(long winsCount) {
        this.winsCount = winsCount;
    }

    public String getWinRate() {
        return winRate;
    }

    public void setWinRate(String winRate) {
        this.winRate = winRate;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }
}
