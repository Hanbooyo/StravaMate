package com.stravamate.controller;

import com.stravamate.dto.ApiResponse;
import com.stravamate.entity.Activity;
import com.stravamate.entity.ActivityDailySummary;
import com.stravamate.entity.ActivityWeeklySummary;
import com.stravamate.dto.MonthlySummary;
import com.stravamate.service.ActivityService;
import com.stravamate.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final SummaryService summaryService;

    @PostMapping("/activities/backfill")
    public ApiResponse<String> backfill(@RequestParam("user_id") Long userId,
                                        @RequestParam(value = "days", defaultValue = "90") int days) {
        int count = activityService.backfillRecent(userId, days);
        summaryService.recomputeAllForUser(userId);
        return ApiResponse.ok("Backfilled " + count + " activities", null);
    }

    @GetMapping("/activities")
    public ApiResponse<List<Activity>> activities(@RequestParam("user_id") Long userId,
                                                  @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
                                                  @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        return ApiResponse.ok(activityService.getActivitiesBetween(userId, start, end));
    }

    @GetMapping("/summary/daily")
    public ApiResponse<List<ActivityDailySummary>> daily(@RequestParam("user_id") Long userId,
                                                         @RequestParam(value = "days", defaultValue = "7") int days) {
        return ApiResponse.ok(summaryService.getLastNDays(userId, days));
    }

    @GetMapping("/summary/weekly")
    public ApiResponse<List<ActivityWeeklySummary>> weekly(@RequestParam("user_id") Long userId) {
        return ApiResponse.ok(summaryService.getWeekly(userId));
    }

    @GetMapping("/summary/monthly")
    public ApiResponse<List<MonthlySummary>> monthly(@RequestParam("user_id") Long userId,
                                                     @RequestParam(value = "year") int year) {
        return ApiResponse.ok(summaryService.getMonthly(userId, year));
    }
}
