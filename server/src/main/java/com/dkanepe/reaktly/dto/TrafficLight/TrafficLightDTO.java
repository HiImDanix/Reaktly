package com.dkanepe.reaktly.dto.TrafficLight;

import com.dkanepe.reaktly.models.games.TrafficLight.TrafficLight.TrafficLight;
import lombok.Data;

@Data
public class TrafficLightDTO {
    private TrafficLight.TARGET_COLOUR targetColour;
    // game state DTO


    public TrafficLightDTO(TrafficLight.TARGET_COLOUR targetColour) {
        this.targetColour = targetColour;
    }
}
