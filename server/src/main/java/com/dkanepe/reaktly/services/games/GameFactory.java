package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.models.games.TrafficLight.TrafficLight.TrafficLight;
import org.springframework.stereotype.Component;

@Component
public class GameFactory {

        public static Game createGame(Game.GameType type) {
            switch (type) {
                case PERFECT_CLICKER -> {
                    double AVERAGE_CPS = 6;
                    int minTargetClicks = 10;
                    int maxTargetClicks = 100;
                    int targetClicks = (int) (Math.random() * (maxTargetClicks - minTargetClicks) + minTargetClicks);
                    int targetTime = (int) (targetClicks / AVERAGE_CPS);
                    Game game = new PerfectClicker(targetClicks);
                    game.setGameDurationMillis(targetTime * 1000);
                    return game;
                }
                case TRAFFIC_LIGHT -> {
                    TrafficLight.TARGET_COLOUR targetColour =
                            TrafficLight.TARGET_COLOUR.values()
                                    [(int) (Math.random() * TrafficLight.TARGET_COLOUR.values().length)];
                    return new TrafficLight(targetColour);
                }
                default -> throw new IllegalArgumentException("Unknown game type: " + type);
            }
        }
}