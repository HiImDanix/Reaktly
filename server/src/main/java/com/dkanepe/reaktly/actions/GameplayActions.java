package com.dkanepe.reaktly.actions;

public enum GameplayActions {
    GAME_START_INFO, // GameDTO
    GAME_START_PING,
    GAME_END, // ScoreboardDTO
    PERFECT_CLICKER_CLICK, // ClickDTO
    PERFECT_CLICKER_GAME_START, // PerfectClickerDTO
    PERFECT_CLICKER_GAME_END; // List<PefectClickerGameStateDTO>
}
