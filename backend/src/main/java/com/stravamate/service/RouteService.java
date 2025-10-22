package com.stravamate.service;

import com.stravamate.entity.Activity;
import com.stravamate.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final ActivityRepository activityRepository;

    public List<String> getMyRouteHashes(Long userId) {
        List<Activity> list = activityRepository.findAllByUserId(userId);
        return list.stream()
                .map(Activity::getRouteHash)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Long> getMyRouteUsageCounts(Long userId) {
        List<Activity> list = activityRepository.findAllByUserId(userId);
        return list.stream()
                .map(Activity::getRouteHash)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }

    public List<Activity> getLeaderboardForRoute(String routeHash) {
        return activityRepository.findAllByRouteHash(routeHash);
    }
}
