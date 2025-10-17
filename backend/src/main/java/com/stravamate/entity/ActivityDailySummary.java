package com.stravamate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "activity_daily_summary", uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_user_date", columnNames = {"user_id", "day"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Column(name = "total_distance")
    private Double totalDistance; // meters

    @Column(name = "total_moving_time")
    private Integer totalMovingTime; // seconds

    @Column(name = "total_elev_gain")
    private Double totalElevGain; // meters
}

