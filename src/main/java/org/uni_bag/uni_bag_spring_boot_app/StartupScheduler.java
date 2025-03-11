package org.uni_bag.uni_bag_spring_boot_app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.repository.AssignmentRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.AssignmentNotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StartupScheduler {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentNotificationService notificationService;

    @Bean
    public ApplicationRunner initializeScheduledTasks() {
        return args -> {
            log.info("Notification scheduling started");

            List<Assignment> assignments = assignmentRepository.findAssignmentsAfterOneHour(
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0)
            );
            log.info("Loaded {} assignments from the database.", assignments.size());

            for (Assignment assignment : assignments) {
                notificationService.scheduleNotification(assignment);
            }

            log.info("Notification scheduling completed.");
        };
    }
}