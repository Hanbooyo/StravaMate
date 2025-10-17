package com.stravamate.scheduler;

import com.stravamate.repository.UserRepository;
import com.stravamate.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SummaryScheduler {
    private final UserRepository userRepository;
    private final SummaryService summaryService;

    // Run daily at 02:15 UTC
    @Scheduled(cron = "0 15 2 * * *", zone = "UTC")
    public void computeDailySummaries() {
        log.info("Running daily summary recomputation");
        userRepository.findAll().forEach(u -> summaryService.recomputeAllForUser(u.getId()));
    }
}

