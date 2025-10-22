package com.stravamate.repository;

import com.stravamate.entity.SegmentEffort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SegmentEffortRepository extends JpaRepository<SegmentEffort, Long> {
    List<SegmentEffort> findAllBySegmentIdOrderByElapsedTimeAsc(Long segmentId, Pageable pageable);
    List<SegmentEffort> findAllByUserIdAndSegmentIdOrderByElapsedTimeAsc(Long userId, Long segmentId);
}

