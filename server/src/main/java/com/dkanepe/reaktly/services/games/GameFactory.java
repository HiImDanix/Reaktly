package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import org.springframework.stereotype.Component;

@Component
public class GameFactory {

        public static Game createGame(Game.GameType type) {
            switch (type) {
                case PERFECT_CLICKER:
                    double AVERAGE_CPS = 6;
                    int minTargetClicks = 10;
                    int maxTargetClicks = 100;
                    int targetClicks = (int) (Math.random() * (maxTargetClicks - minTargetClicks) + minTargetClicks);
                    int targetTime = (int) (targetClicks / AVERAGE_CPS);
                    Game game =  new PerfectClicker(targetClicks);
                    game.setGameDurationMillis(targetTime * 1000);
                    return game;
                default:
                    throw new IllegalArgumentException("Unknown game type: " + type);
            }
        }
}