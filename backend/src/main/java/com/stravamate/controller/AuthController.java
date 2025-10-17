package com.stravamate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stravamate.config.StravaProperties;
import com.stravamate.dto.ApiResponse;
import com.stravamate.dto.StravaTokenResponse;
import com.stravamate.entity.StravaToken;
import com.stravamate.entity.User;
import com.stravamate.repository.StravaTokenRepository;
import com.stravamate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/auth/strava")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final StravaProperties properties;
    private final UserRepository userRepository;
    private final StravaTokenRepository tokenRepository;
    private final WebClient oauthClient = WebClient.create("https://www.strava.com");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/start")
    public RedirectView start() {
        String scope = URLEncoder.encode("read,activity:read_all,profile:read_all", StandardCharsets.UTF_8);
        String redirect = properties.getRedirectUri();
        String url = String.format(
                "https://www.strava.com/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s&approval_prompt=auto&scope=%s",
                properties.getClientId(),
                URLEncoder.encode(redirect, StandardCharsets.UTF_8),
                scope
        );
        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");

        StravaTokenResponse tokenResponse = oauthClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(StravaTokenResponse.class)
                .block();

        if (tokenResponse == null || tokenResponse.getAthlete() == null) {
            throw new RuntimeException("Failed to obtain Strava token");
        }

        Long athleteId = ((Number) tokenResponse.getAthlete().get("id")).longValue();
        String email = tokenResponse.getAthlete().get("email") != null ? tokenResponse.getAthlete().get("email").toString() : null;

        User user = userRepository.findByStravaAthleteId(athleteId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .stravaAthleteId(athleteId)
                        .build()));

        StravaToken token = tokenRepository.findByUserId(user.getId())
                .orElse(StravaToken.builder().userId(user.getId()).build());
        token.setAccessToken(tokenResponse.getAccessToken());
        token.setRefreshToken(tokenResponse.getRefreshToken());
        token.setExpiresAt(tokenResponse.getExpiresAt());
        tokenRepository.save(token);

        String target = properties.getFrontendBaseUrl() + "/dashboard?user_id=" + user.getId();
        return new RedirectView(target);
    }
}

