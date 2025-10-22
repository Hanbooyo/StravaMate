package com.stravamate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_activity", columnNames = {"user_id", "strava_activity_id"})
}, indexes = {
        @Index(name = "idx_activities_user_id", columnList = "user_id"),
        @Index(name = "idx_activities_strava_activity_id", columnList = "strava_activity_id"),
        @Index(name = "idx_activities_route_hash", columnList = "route_hash")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "strava_activity_id", nullable = false)
    private Long stravaActivityId;

    private String type; // Run, Ride, etc.

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    private Double distance; // meters
    @Column(name = "moving_time")
    private Integer movingTime; // seconds
    @Column(name = "elev_gain")
    private Double elevGain; // meters
    @Column(name = "avg_hr")
    private Double avgHr;
    @Column(name = "avg_speed")
    private Double avgSpeed; // m/s
    private Double calories;

    // Optional fields for route-hash approach
    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_lng")
    private Double startLng;

    @Lob
    @Column(name = "summary_polyline", columnDefinition = "TEXT")
    private String summaryPolyline;

    @Column(name = "route_hash", length = 64)
    private String routeHash;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawJson;

    @UpdateTimestamp
    private Instant updatedAt;
}
