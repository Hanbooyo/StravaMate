package com.stravamate.controller;

import com.stravamate.dto.ApiResponse;
import com.stravamate.entity.SegmentEffort;
import com.stravamate.service.SegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/segments")
@RequiredArgsConstructor
public class SegmentController {
    private final SegmentService segmentService;

    @GetMapping("/{segmentId}/leaderboard")
    public ApiResponse<List<SegmentEffort>> leaderboard(@PathVariable("segmentId") Long segmentId,
                                                        @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return ApiResponse.ok(segmentService.getLeaderboard(segmentId, limit));
    }

    @GetMapping("/personal-bests")
    public ApiResponse<List<SegmentEffort>> personalBests(@RequestParam("user_id") Long userId,
                                                          @RequestParam("segment_id") Long segmentId) {
        return ApiResponse.ok(segmentService.getPersonalBests(userId, segmentId));
    }
}

