package net.inpercima.mittagstisch.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final LunchService lunchService;

    /**
     * Scheduled task to import lunch data automatically.
     * Runs every day at 10:00 AM Berlin time to prepare data before lunch hours.
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Berlin")
    public void scheduledLunchImport() {
        log.info("Starting scheduled lunch import task");
        try {
            lunchService.importLunches();
            log.info("Scheduled lunch import completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled lunch import: {}", e.getMessage(), e);
        }
    }
}
