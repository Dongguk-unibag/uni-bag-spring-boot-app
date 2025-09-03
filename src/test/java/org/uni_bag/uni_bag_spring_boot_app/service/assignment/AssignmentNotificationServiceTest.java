package org.uni_bag.uni_bag_spring_boot_app.service.assignment;

import org.hibernate.validator.constraints.ru.INN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.service.fcm.FcmService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class AssignmentNotificationServiceTest {
    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @Mock
    private FcmService fcmService;

    @Mock
    private Map<String, ScheduledFuture<?>> scheduledTasks;

    @InjectMocks
    private AssignmentNotificationService assignmentNotificationService;

    private User user;

    @BeforeEach
    void setUp() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        this.user = User.from(oAuthUserInfoDto);
    }

    @Nested
    @DisplayName("스케쥴 등록")
    class ScheduleNotificationTest {

        @Test
        @DisplayName("성공 - 스케쥴 등록 시 TaskScheduler에 작업이 등록되고 Runnable 실행 시 알림 전송 및 제거된다")
        void success() {
            // given
            Assignment assignment = createAssignment();
            ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

            // TaskScheduler mock 동작 설정: Runnable을 즉시 실행하고 mockFuture 반환
            given(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
                    .willAnswer(invocation -> {
                        Runnable task = invocation.getArgument(0);
                        task.run();
                        return mockFuture;
                    });

            // when
            assignmentNotificationService.scheduleNotification(assignment);

            // then
            then(taskScheduler).should().schedule(any(Runnable.class), any(Instant.class));
            then(scheduledTasks).should().remove(any(String.class));

            then(fcmService).should(times(1))
                    .sendNotification(eq(assignment.getUser()),
                            eq("과제 마감 1시간 전!"),
                            eq(assignment.getTitle() + " 과제가 곧 마감됩니다."));

        }
    }

    public Assignment createAssignment() {
        return Assignment.builder()
                .id(1L)
                .title("알고리즘 과제")
                .description("알고리즘 1장 풀기")
                .user(user)
                .lecture(createLecture())
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .isCompleted(false)
                .build();
    }

    public DgLecture createLecture() {
        return DgLecture.builder()
                .id(1L)
                .curriculum("컴퓨터공학과 2025")
                .area("전공필수")
                .targetGrade("2학년")
                .courseCode("CS201")
                .courseName("자료구조")
                .instructor("홍길동")
                .classroom("B101")
                .credits(3.0f)
                .theory(2.0f)
                .practical(1.0f)
                .engineeringAccreditation("인증")
                .courseType("강의")
                .courseFormat("온라인")
                .evaluationMethod("중간/기말 시험")
                .gradeType("절대평가")
                .completionType("이수")
                .offeringCollege("공과대학")
                .offeringDepartment("컴퓨터공학과")
                .offeringMajor("소프트웨어")
                .teamTeaching("없음")
                .remarks("특이사항 없음")
                .academicYear(2025)
                .semester(1)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }
}