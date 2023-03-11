package com.dkanepe.reaktly.models.games.TrafficLight.TrafficLight;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Click the button as fast as you can until you reach the displayed number.
 * But be careful, if you go over the number you lose!
 */
@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class TrafficLight extends Game {

    @Transient
    private String title = "Traffic Light";

    public enum TARGET_COLOUR {
        RED,
        YELLOW,
        GREEN
    }
    private TARGET_COLOUR targetColour;

    // Game state

    public TrafficLight(TARGET_COLOUR targetColour) {
        super.setType(GameType.TRAFFIC_LIGHT);
        this.targetColour = targetColour;
    }

    public String getInstructions() {
        return String.format("Pay attention to the colour on the screen. When it changes to your %s - tap as fast as you can!", getTargetColour());
    }

    public String getShortInstructions() {
        return String.format("When the colour changes to %s - tap!", getTargetColour());
    }

}
