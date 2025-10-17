package com.stravamate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stravamate.entity.StravaToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StravaClient {
    private final TokenService tokenService;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.strava.com/api/v3")
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getActivityDetail(StravaToken token, Long activityId) {
        StravaToken valid = tokenService.ensureValidToken(token);
        String json = webClient.get()
                .uri("/activities/" + activityId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + valid.getAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse activity JSON", e);
        }
    }

    public List<JsonNode> listRecentActivities(StravaToken token, Instant after) {
        StravaToken valid = tokenService.ensureValidToken(token);
        int afterEpoch = (int) after.getEpochSecond();
        String json = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/athlete/activities")
                        .queryParam("after", afterEpoch)
                        .queryParam("per_page", 100)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + valid.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.isArray() ? objectMapper.readerForListOf(JsonNode.class).readValue(node) : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse activities list", e);
        }
    }
}

