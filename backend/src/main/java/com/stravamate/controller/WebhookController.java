package com.stravamate.controller;

import com.stravamate.config.StravaProperties;
import com.stravamate.dto.ApiResponse;
import com.stravamate.dto.StravaWebhookEvent;
import com.stravamate.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/strava")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    private final StravaProperties properties;
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<Map<String, String>> verify(@RequestParam(name = "hub.mode", required = false) String mode,
                                                      @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
                                                      @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if (properties.getVerifyToken() != null && properties.getVerifyToken().equals(verifyToken)) {
            return ResponseEntity.ok(Map.of("hub.challenge", challenge));
        }
        return ResponseEntity.status(403).body(Map.of("error", "Invalid verify token"));
    }

    @PostMapping
    public ApiResponse<Void> receive(@RequestBody StravaWebhookEvent event) {
        log.info("Received webhook: {}", event);
        if ("activity".equalsIgnoreCase(event.getObjectType())) {
            if ("create".equalsIgnoreCase(event.getAspectType()) || "update".equalsIgnoreCase(event.getAspectType())) {
                activityService.fetchAndUpsertActivity(event.getOwnerId(), event.getObjectId());
            }
        }
        return ApiResponse.ok("Event enqueued", null);
    }
}

