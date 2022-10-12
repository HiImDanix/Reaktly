package com.dkanepe.reaktly.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "games")
@Data
public class GameConfiguration {
    private long prepareTimeMillis = 5000;
    private long instructionsDurationMillis = 5000;
    private long scoreboardDurationMillis = 5000;
}
