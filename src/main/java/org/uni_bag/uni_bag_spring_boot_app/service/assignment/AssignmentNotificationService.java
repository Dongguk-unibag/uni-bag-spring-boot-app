package org.uni_bag.uni_bag_spring_boot_app.service.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.constant.AssignmentAlarmType;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.service.fcm.FcmService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentNotificationService {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final FcmService fcmService;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void scheduleNotification(Assignment assignment) {
        LocalDateTime endDateTime = assignment.getEndDateTime();

        log.info("Scheduling notifications for assignment: {} (End time: {})", assignment.getId(), endDateTime);

        scheduleTask(AssignmentAlarmType.YESTERDAY_9AM,
                assignment,
                endDateTime.minusDays(1).withHour(9).withMinute(0).withSecond(0),
                "과제 마감 전날 알림!",
                assignment.getTitle() + " 과제가 내일 마감됩니다.");


        scheduleTask(AssignmentAlarmType.ONE_HOUR_AGO,
                assignment,
                endDateTime.minusHours(1),
                "과제 마감 1시간 전!",
                assignment.getTitle() + " 과제가 곧 마감됩니다.");


    }

    private void scheduleTask(AssignmentAlarmType assignmentAlarmType, Assignment assignment, LocalDateTime notifyTime,
                              String title, String message) {
        log.info("Attempting to schedule task: {} | {}", assignment.getId(), assignmentAlarmType.name());

        if (LocalDateTime.now().isAfter(notifyTime)) {
            log.warn("Skipping scheduling for assignment as the time has already passed: {} | {}", assignment.getId(), assignmentAlarmType.name());
            return;
        }

        final String scheduleId = assignment.getId() + "-" + assignmentAlarmType.getAssigmentSubId();
        Instant instant = notifyTime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
            log.info("Executing scheduled notification: {}", scheduleId);
            fcmService.sendNotification(assignment.getUser(), title, message);
            scheduledTasks.remove(scheduleId); // 완료 후 삭제
            log.info("Completed and removed scheduled task: {}", scheduleId);
        }, instant);

        scheduledTasks.put(scheduleId, scheduledTask);
        log.info("Scheduled task successfully: {}", scheduleId);

    }

    public void rescheduleNotification(Assignment assignment) {
        log.info("Rescheduling notifications for assignment: {}", assignment.getId());
        cancelNotification(assignment);
        scheduleNotification(assignment);
    }

    public void cancelNotification(Assignment assignment) {
        String YESTERDAY_9AM_ID = assignment.getId() + "-" + AssignmentAlarmType.YESTERDAY_9AM.getAssigmentSubId();
        String ONE_HOUR_AGO_ID = assignment.getId() + "-" + AssignmentAlarmType.ONE_HOUR_AGO.getAssigmentSubId();

        log.info("Cancelling notifications for assignment: {}", assignment.getId());

        ScheduledFuture<?> scheduledTask1 = scheduledTasks.remove(YESTERDAY_9AM_ID);
        ScheduledFuture<?> scheduledTask2 = scheduledTasks.remove(ONE_HOUR_AGO_ID);

        if (scheduledTask1 != null) {
            scheduledTask1.cancel(false); // 현재 실행 중이 아닌 경우만 취소
            log.info("Cancelled scheduled task: {}", YESTERDAY_9AM_ID);
        } else {
            log.warn("No scheduled task found for: {}", YESTERDAY_9AM_ID);
        }

        if (scheduledTask2 != null) {
            scheduledTask2.cancel(false); // 현재 실행 중이 아닌 경우만 취소
            log.info("Cancelled scheduled task: {}", ONE_HOUR_AGO_ID);
        } else {
            log.warn("No scheduled task found for: {}", ONE_HOUR_AGO_ID);
        }
    }
}
