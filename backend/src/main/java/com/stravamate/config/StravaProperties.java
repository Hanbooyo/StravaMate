package com.stravamate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "strava")
@Data
public class StravaProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String verifyToken; // for webhook verification
    private String webhookCallbackUrl; // optional, for webhook subscription mgmt
    private String frontendBaseUrl; // for redirect after oauth
}

