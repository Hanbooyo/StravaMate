package com.stravamate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "segment_efforts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_segment_effort", columnNames = {"user_id", "segment_effort_id"})
}, indexes = {
        @Index(name = "idx_segment_efforts_segment_id", columnList = "segment_id"),
        @Index(name = "idx_segment_efforts_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegmentEffort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId; // local Activity id

    @Column(name = "segment_effort_id", nullable = false)
    private Long segmentEffortId; // Strava Segment Effort ID

    @Column(name = "segment_id", nullable = false)
    private Long segmentId; // Strava Segment ID

    @Column(name = "segment_name")
    private String segmentName;

    @Column(name = "elapsed_time")
    private Integer elapsedTime; // seconds

    @Column(name = "start_date")
    private OffsetDateTime startDate;
}

