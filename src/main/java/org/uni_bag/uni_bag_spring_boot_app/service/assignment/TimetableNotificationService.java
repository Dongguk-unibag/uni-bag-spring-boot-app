package org.uni_bag.uni_bag_spring_boot_app.service.assignment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.constant.AssignmentAlarmType;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.fcm.FcmService;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TimetableNotificationService {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final FcmService fcmService;
    private final TimeTableLectureRepository timeTableLectureRepository;
    private final Map<String, ScheduledFuture
            <?>> scheduledTasks = new ConcurrentHashMap<>();

    public void scheduleNotification(TimeTable timeTable) {
        List<TimeTableLecture> foundTimetableLectures = timeTableLectureRepository.findAllByTimeTable(timeTable);

        int subId = 1;
        for (TimeTableLecture timeTableLecture : foundTimetableLectures) {
            DgLecture foundLecture = timeTableLecture.getLecture();
            List<DgLectureTime> foundLectureTimes = foundLecture.getDgLectureTimes();
            for (DgLectureTime lectureTime : foundLectureTimes) {
                LocalDate today = LocalDate.now();
                // TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)
                // 오늘이 수요일이면 그대로 반환
                // 오늘이 수요일이 아니면 다음 수요일을 반환
                LocalDate scheduledLocalDate = today.with(TemporalAdjusters.nextOrSame(getDayOfWeek(lectureTime.getDayOfWeek())));

                // 예정된 수업시간 30분 전
                LocalDateTime scheduledLocalDateTime = LocalDateTime.of(scheduledLocalDate, lectureTime.getStartTime().toLocalTime()).minusMinutes(30);

                // 스케줄 예정인 시간이 이미 지났다면
                if(scheduledLocalDateTime.isBefore(LocalDateTime.now())) {
                    scheduledLocalDateTime = scheduledLocalDateTime.plusWeeks(1);
                }

                String scheduleId = timeTable.getId() + "_" + subId++;

                scheduleTask(scheduleId, timeTable.getUser(), foundLecture, lectureTime, scheduledLocalDateTime);
            }
        }
    }

    private void scheduleTask(String scheduleId, User notifyTargetUser, DgLecture lecture, DgLectureTime lectureTime, LocalDateTime notifyTime) {
        log.info("Attempting to schedule lecture reminder task : schedule id: {} | notifyTime id: {}", scheduleId, notifyTime.toString());

        Instant instant = notifyTime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
            log.info("Executing scheduled notification: {}", scheduleId);

            String title = "잠시후 " + lecture.getCourseName() + " 수업이 있어요";
            String description = lecture.getClassroom() + " " + new SimpleDateFormat("HH시 mm분").format(lectureTime.getStartTime());

            fcmService.sendNotification(notifyTargetUser, title, description);
            scheduledTasks.remove(scheduleId); // 완료 후 삭제
            log.info("Completed and removed scheduled task: {}", scheduleId);

            LocalDateTime nextNotifyTime = notifyTime.plusWeeks(1);
            scheduleTask(scheduleId, notifyTargetUser, lecture, lectureTime, nextNotifyTime);

        }, instant);

        scheduledTasks.put(scheduleId, scheduledTask);
        log.info("Scheduled task successfully: {}", scheduleId);

    }

    public void cancelNotification(TimeTable timeTable) {
        int times = 0;
        List<TimeTableLecture> foundTimetableLectures = timeTableLectureRepository.findAllByTimeTable(timeTable);
        for (TimeTableLecture timeTableLecture : foundTimetableLectures) {
            times += timeTableLecture.getLecture().getDgLectureTimes().size();
        }

        for(int subId = 1; subId <= times; subId++) {
            String scheduleId = timeTable.getId() + "_" + subId;
            ScheduledFuture<?> scheduledTask = scheduledTasks.remove(scheduleId);

            if (scheduledTask != null) {
                scheduledTask.cancel(false); // 현재 실행 중이 아닌 경우만 취소
                log.info("Cancelled scheduled task: {}", scheduleId);
            } else {
                log.warn("No scheduled task found for: {}", scheduleId);
            }
        }

    }

    private DayOfWeek getDayOfWeek(String day) {
        final Map<String, DayOfWeek> dayMapping = Map.of(
                "월", DayOfWeek.MONDAY,
                "화", DayOfWeek.TUESDAY,
                "수", DayOfWeek.WEDNESDAY,
                "목", DayOfWeek.THURSDAY,
                "금", DayOfWeek.FRIDAY,
                "토", DayOfWeek.SATURDAY,
                "일", DayOfWeek.SUNDAY
        );

        return dayMapping.getOrDefault(day, null);
    }
}
