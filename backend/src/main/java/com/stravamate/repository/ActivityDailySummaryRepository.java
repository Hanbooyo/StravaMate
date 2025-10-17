package com.stravamate.repository;

import com.stravamate.entity.ActivityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityDailySummaryRepository extends JpaRepository<ActivityDailySummary, Long> {
    Optional<ActivityDailySummary> findByUserIdAndDay(Long userId, LocalDate day);
    List<ActivityDailySummary> findAllByUserIdAndDayBetween(Long userId, LocalDate start, LocalDate end);
}

