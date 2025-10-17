package com.stravamate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StravaWebhookEvent {
    @JsonProperty("object_type")
    private String objectType; // activity

    @JsonProperty("object_id")
    private Long objectId; // activity id

    @JsonProperty("aspect_type")
    private String aspectType; // create/update/delete

    @JsonProperty("owner_id")
    private Long ownerId; // athlete id

    @JsonProperty("event_time")
    private Long eventTime; // epoch seconds
}

