package com.stravamate.service;

import com.stravamate.entity.SegmentEffort;
import com.stravamate.repository.SegmentEffortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SegmentService {
    private final SegmentEffortRepository repo;

    public List<SegmentEffort> getLeaderboard(Long segmentId, int limit) {
        return repo.findAllBySegmentIdOrderByElapsedTimeAsc(segmentId, PageRequest.of(0, Math.max(1, Math.min(200, limit))));
    }

    public List<SegmentEffort> getPersonalBests(Long userId, Long segmentId) {
        return repo.findAllByUserIdAndSegmentIdOrderByElapsedTimeAsc(userId, segmentId);
    }
}

