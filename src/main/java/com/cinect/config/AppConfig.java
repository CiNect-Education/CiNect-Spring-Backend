package com.cinect.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    private int holdTtlMinutes = 10;
    private int paymentTimeoutMinutes = 2;
    private int pointsPerBooking = 10;
    private boolean maintenanceMode = false;
}
