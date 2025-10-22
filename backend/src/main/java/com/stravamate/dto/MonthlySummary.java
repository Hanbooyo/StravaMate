package com.stravamate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySummary {
    private String month; // e.g., 2025-01
    private Double totalDistance; // meters
    private Integer totalMovingTime; // seconds
    private Double totalElevGain; // meters
}

