package com.stravamate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "activity_weekly_summary", uniqueConstraints = {
        @UniqueConstraint(name = "uk_weekly_user_week", columnNames = {"user_id", "week_start"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityWeeklySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart; // Monday

    @Column(name = "total_distance")
    private Double totalDistance; // meters

    @Column(name = "total_moving_time")
    private Integer totalMovingTime; // seconds

    @Column(name = "total_elev_gain")
    private Double totalElevGain; // meters
}

