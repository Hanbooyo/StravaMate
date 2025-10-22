package com.stravamate.controller;

import com.stravamate.dto.ApiResponse;
import com.stravamate.entity.Activity;
import com.stravamate.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping("/mine")
    public ApiResponse<Map<String, Long>> myRoutes(@RequestParam("user_id") Long userId) {
        return ApiResponse.ok(routeService.getMyRouteUsageCounts(userId));
    }

    @GetMapping("/{routeHash}/leaderboard")
    public ApiResponse<List<Activity>> leaderboard(@PathVariable("routeHash") String routeHash) {
        return ApiResponse.ok(routeService.getLeaderboardForRoute(routeHash));
    }
}

