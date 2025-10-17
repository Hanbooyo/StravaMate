package com.stravamate.service;

import com.stravamate.entity.Activity;
import com.stravamate.entity.ActivityDailySummary;
import com.stravamate.entity.ActivityWeeklySummary;
import com.stravamate.repository.ActivityDailySummaryRepository;
import com.stravamate.repository.ActivityRepository;
import com.stravamate.repository.ActivityWeeklySummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {
    private final ActivityRepository activityRepository;
    private final ActivityDailySummaryRepository dailyRepo;
    private final ActivityWeeklySummaryRepository weeklyRepo;

    public void recomputeAllForUser(Long userId) {
        List<Activity> list = activityRepository.findAllByUserId(userId);
        Map<LocalDate, List<Activity>> byDay = list.stream()
                .filter(a -> a.getStartDate() != null)
                .collect(Collectors.groupingBy(a -> a.getStartDate().atZoneSameInstant(ZoneOffset.UTC).toLocalDate()));

        for (Map.Entry<LocalDate, List<Activity>> e : byDay.entrySet()) {
            double dist = e.getValue().stream().map(a -> Optional.ofNullable(a.getDistance()).orElse(0.0)).mapToDouble(Double::doubleValue).sum();
            int time = e.getValue().stream().map(a -> Optional.ofNullable(a.getMovingTime()).orElse(0)).mapToInt(Integer::intValue).sum();
            double elev = e.getValue().stream().map(a -> Optional.ofNullable(a.getElevGain()).orElse(0.0)).mapToDouble(Double::doubleValue).sum();

            ActivityDailySummary s = dailyRepo.findByUserIdAndDay(userId, e.getKey())
                    .orElse(ActivityDailySummary.builder().userId(userId).day(e.getKey()).build());
            s.setTotalDistance(dist);
            s.setTotalMovingTime(time);
            s.setTotalElevGain(elev);
            dailyRepo.save(s);
        }

        Map<LocalDate, List<Activity>> byWeek = list.stream()
                .filter(a -> a.getStartDate() != null)
                .collect(Collectors.groupingBy(a -> weekStart(a.getStartDate())));

        for (Map.Entry<LocalDate, List<Activity>> e : byWeek.entrySet()) {
            double dist = e.getValue().stream().map(a -> Optional.ofNullable(a.getDistance()).orElse(0.0)).mapToDouble(Double::doubleValue).sum();
            int time = e.getValue().stream().map(a -> Optional.ofNullable(a.getMovingTime()).orElse(0)).mapToInt(Integer::intValue).sum();
            double elev = e.getValue().stream().map(a -> Optional.ofNullable(a.getElevGain()).orElse(0.0)).mapToDouble(Double::doubleValue).sum();

            ActivityWeeklySummary s = weeklyRepo.findByUserIdAndWeekStart(userId, e.getKey())
                    .orElse(ActivityWeeklySummary.builder().userId(userId).weekStart(e.getKey()).build());
            s.setTotalDistance(dist);
            s.setTotalMovingTime(time);
            s.setTotalElevGain(elev);
            weeklyRepo.save(s);
        }
    }

    public List<ActivityDailySummary> getLastNDays(Long userId, int days) {
        LocalDate end = LocalDate.now(ZoneOffset.UTC);
        LocalDate start = end.minusDays(days - 1);
        return dailyRepo.findAllByUserIdAndDayBetween(userId, start, end);
    }

    public List<ActivityWeeklySummary> getWeekly(Long userId) {
        return weeklyRepo.findAllByUserIdOrderByWeekStartAsc(userId);
    }

    private LocalDate weekStart(OffsetDateTime odt) {
        LocalDate date = odt.atZoneSameInstant(ZoneOffset.UTC).toLocalDate();
        return date.with(DayOfWeek.MONDAY);
    }
}

