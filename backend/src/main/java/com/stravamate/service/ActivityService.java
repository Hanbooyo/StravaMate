package com.stravamate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.stravamate.entity.Activity;
import com.stravamate.entity.StravaToken;
import com.stravamate.entity.User;
import com.stravamate.exception.NotFoundException;
import com.stravamate.repository.ActivityRepository;
import com.stravamate.repository.StravaTokenRepository;
import com.stravamate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final UserRepository userRepository;
    private final StravaTokenRepository tokenRepository;
    private final ActivityRepository activityRepository;
    private final StravaClient stravaClient;

    @Async
    public void fetchAndUpsertActivity(Long stravaAthleteId, Long activityId) {
        User user = userRepository.findByStravaAthleteId(stravaAthleteId)
                .orElseThrow(() -> new NotFoundException("User not found for athleteId=" + stravaAthleteId));
        StravaToken token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Token not found for user=" + user.getId()));

        JsonNode json = stravaClient.getActivityDetail(token, activityId);
        upsertFromJson(user.getId(), json);
    }

    public void upsertFromJson(Long userId, JsonNode json) {
        Long stravaId = json.path("id").asLong();
        String type = json.path("type").asText(null);
        String startDateStr = json.path("start_date").asText(null); // ISO 8601 UTC
        OffsetDateTime startDate = startDateStr != null ? OffsetDateTime.parse(startDateStr) : null;
        Double distance = json.path("distance").isNumber() ? json.get("distance").asDouble() : null;
        Integer movingTime = json.path("moving_time").isNumber() ? json.get("moving_time").asInt() : null;
        Double elevGain = json.path("total_elevation_gain").isNumber() ? json.get("total_elevation_gain").asDouble() : null;
        Double avgHr = json.path("average_heartrate").isNumber() ? json.get("average_heartrate").asDouble() : null;
        Double avgSpeed = json.path("average_speed").isNumber() ? json.get("average_speed").asDouble() : null;
        Double calories = json.path("calories").isNumber() ? json.get("calories").asDouble() : null;

        Optional<Activity> existing = activityRepository.findByUserIdAndStravaActivityId(userId, stravaId);
        Activity a = existing.orElseGet(() -> Activity.builder()
                .userId(userId)
                .stravaActivityId(stravaId)
                .build());
        a.setType(type);
        a.setStartDate(startDate);
        a.setDistance(distance);
        a.setMovingTime(movingTime);
        a.setElevGain(elevGain);
        a.setAvgHr(avgHr);
        a.setAvgSpeed(avgSpeed);
        a.setCalories(calories);
        a.setRawJson(json.toString());

        activityRepository.save(a);
    }

    public int backfillRecent(Long userId, int days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        StravaToken token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Token not found for user=" + user.getId()));
        Instant after = Instant.now().minusSeconds(days * 24L * 3600L);
        List<com.fasterxml.jackson.databind.JsonNode> list = stravaClient.listRecentActivities(token, after);
        list.forEach(json -> upsertFromJson(userId, json));
        return list.size();
    }

    public List<Activity> getActivitiesBetween(Long userId, OffsetDateTime start, OffsetDateTime end) {
        return activityRepository.findAllByUserIdAndStartDateBetween(userId, start, end);
    }
}

