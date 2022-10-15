package com.dkanepe.reaktly.actions;

/**
 * This class contains all the actions that can be performed in relation to gameplay incl. specific game actions.
 */
public enum GameplayActions {
    GAME_START_INFO, // GameDTO
    GAME_START_PING,
    GAME_END, // ScoreboardDTO
    PERFECT_CLICKER_CLICKS, // ClickDTO
    PERFECT_CLICKER_GAME_START, // PerfectClickerDTO
    PERFECT_CLICKER_GAME_END // List<PerfectClickerGameStateDTO>
}
