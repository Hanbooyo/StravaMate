package com.stravamate.repository;

import com.stravamate.entity.ActivityWeeklySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityWeeklySummaryRepository extends JpaRepository<ActivityWeeklySummary, Long> {
    Optional<ActivityWeeklySummary> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    List<ActivityWeeklySummary> findAllByUserIdOrderByWeekStartAsc(Long userId);
}

