package com.stravamate.repository;

import com.stravamate.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByUserIdAndStravaActivityId(Long userId, Long stravaActivityId);
    List<Activity> findAllByUserIdAndStartDateBetween(Long userId, OffsetDateTime start, OffsetDateTime end);
    List<Activity> findAllByUserId(Long userId);
    List<Activity> findAllByRouteHash(String routeHash);
}
