package com.wordguess.dto;

public class PlatformOverviewDto {
    private long totalPlayers;
    private long totalGames;
    private long totalWins;
    private String globalWinRate;
    private long activePlayersToday;
    private long gamesPlayedToday;
    private long winsToday;

    public PlatformOverviewDto() {}

    public PlatformOverviewDto(long totalPlayers, long totalGames, long totalWins, String globalWinRate,
                               long activePlayersToday, long gamesPlayedToday, long winsToday) {
        this.totalPlayers = totalPlayers;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.globalWinRate = globalWinRate;
        this.activePlayersToday = activePlayersToday;
        this.gamesPlayedToday = gamesPlayedToday;
        this.winsToday = winsToday;
    }

    public long getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(long totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public long getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(long totalGames) {
        this.totalGames = totalGames;
    }

    public long getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(long totalWins) {
        this.totalWins = totalWins;
    }

    public String getGlobalWinRate() {
        return globalWinRate;
    }

    public void setGlobalWinRate(String globalWinRate) {
        this.globalWinRate = globalWinRate;
    }

    public long getActivePlayersToday() {
        return activePlayersToday;
    }

    public void setActivePlayersToday(long activePlayersToday) {
        this.activePlayersToday = activePlayersToday;
    }

    public long getGamesPlayedToday() {
        return gamesPlayedToday;
    }

    public void setGamesPlayedToday(long gamesPlayedToday) {
        this.gamesPlayedToday = gamesPlayedToday;
    }

    public long getWinsToday() {
        return winsToday;
    }

    public void setWinsToday(long winsToday) {
        this.winsToday = winsToday;
    }
}
