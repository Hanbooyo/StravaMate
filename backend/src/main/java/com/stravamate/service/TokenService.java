package com.stravamate.service;

import com.stravamate.config.StravaProperties;
import com.stravamate.dto.StravaTokenResponse;
import com.stravamate.entity.StravaToken;
import com.stravamate.repository.StravaTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final StravaTokenRepository tokenRepository;
    private final StravaProperties properties;
    private final WebClient webClient = WebClient.create("https://www.strava.com");

    public StravaToken ensureValidToken(StravaToken token) {
        long now = Instant.now().getEpochSecond();
        if (token.getExpiresAt() != null && token.getExpiresAt() - now < 60) {
            log.info("Refreshing Strava token for user {}", token.getUserId());
            return refreshToken(token);
        }
        return token;
    }

    public StravaToken refreshToken(StravaToken token) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", properties.getClientId());
        formData.add("client_secret", properties.getClientSecret());
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", token.getRefreshToken());

        StravaTokenResponse response = webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(StravaTokenResponse.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to refresh Strava token");
        }

        token.setAccessToken(response.getAccessToken());
        token.setRefreshToken(response.getRefreshToken());
        token.setExpiresAt(response.getExpiresAt());
        return tokenRepository.save(token);
    }
}

