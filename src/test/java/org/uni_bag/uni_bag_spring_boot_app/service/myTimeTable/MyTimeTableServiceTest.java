package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.TimetableNotificationService;
import org.uni_bag.uni_bag_spring_boot_app.service.timetable.TimetableService;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MyTimeTableServiceTest {
    @InjectMocks
    private MyTimeTableService myTimeTableService;

    @Mock
    private TimetableService timetableService;

    @Mock
    private TimetableNotificationService timetableNotificationService;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private TimeTableLectureRepository timeTableLectureRepository;

    @Nested
    @DisplayName("나의 시간표 목록 조회")
    class GetMyTimeTableListTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User me = createUser(1L, "민수");
            TimeTable timeTable1 = createTimeTable(1L, 2024, 2, me, true);
            TimeTable timeTable2 = createTimeTable(2L, 2025, 1, me, false);
            TimeTable timeTable3 = createTimeTable(3L, 2025, 2, me, false);

            given(timeTableRepository.findAllByUser(eq(me))).willReturn(List.of(timeTable1, timeTable2, timeTable3));

            // when
            myTimeTableService.getMyTimeTableList(me);

            // then
            then(timeTableRepository).should(times(1)).findAllByUser(me);
        }
    }

    @Nested
    @DisplayName("특정 개인 시간표 조회")
    class TimeTableGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timeTableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(timeTableId, 2025, 2, me, false);

            DgLecture lecture1 = createLecture(1L, 2025, 1, "자료구조");
            DgLectureTime dgLectureTime1 = createDgLectureTime(1L,
                    lecture1,
                    "월",
                    Time.valueOf(LocalTime.of(11, 2, 0)),
                    Time.valueOf(LocalTime.of(12, 2, 0))
            );

            DgLecture lecture2 = createLecture(1L, 2025, 1, "알고리즘");
            DgLectureTime dgLectureTime2 = createDgLectureTime(2L,
                    lecture2,
                    "화",
                    Time.valueOf(LocalTime.of(13, 2, 0)),
                    Time.valueOf(LocalTime.of(14, 2, 0))
            );

            DgLecture lecture3 = createLecture(1L, 2025, 1, "자바 프로그래밍");
            DgLectureTime dgLectureTime3 = createDgLectureTime(2L,
                    lecture3,
                    "수",
                    Time.valueOf(LocalTime.of(15, 2, 0)),
                    Time.valueOf(LocalTime.of(16, 2, 0))
            );

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(timetableService.getTimetableWithLectures(eq(timeTable))).willReturn(lectureTimeMap);

            // when
            myTimeTableService.getMyTimeTableById(me, timeTableId);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(timetableService).should(times(1)).getTimetableWithLectures(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 사용자에게 해당 ID의 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExistForUser_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User me = createUser(1L, "민수");

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.getMyTimeTableById(me, timeTableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
        }
    }

    @Nested
    @DisplayName("특정 시간표에 대한 강의 조회")
    class MyEnrolledLectureGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timeTableId = 1L;
            int year = 2025;
            int semester = 2;
            User user = createUser(1L, "민수");

            TimeTable timeTable = createTimeTable(timeTableId, year, semester, user, true);

            DgLecture lecture1 = createLecture(1L, year, semester, "알고리즘");
            DgLecture lecture2 = createLecture(2L, year, semester, "자료구조");
            DgLecture lecture3 = createLecture(3L, year, semester, "스프링 부트");

            TimeTableLecture timeTableLecture1 = createTimeTableLecture(1L, lecture1, timeTable, "#ffff99");
            TimeTableLecture timeTableLecture2 = createTimeTableLecture(2L, lecture2, timeTable, "#ffff00");
            TimeTableLecture timeTableLecture3 = createTimeTableLecture(3L, lecture3, timeTable, "#ffff11");

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(user))).willReturn(Optional.of(timeTable));
            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(List.of(timeTableLecture1, timeTableLecture2, timeTableLecture3));

            // when
            myTimeTableService.getMyEnrolledLecture(user, timeTableId);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(user));
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 사용자에게 해당 ID의 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExistForUser_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User me = createUser(1L, "민수");

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.getMyEnrolledLecture(me, timeTableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
        }
    }

    @Nested
    @DisplayName("기본 시간표 조회")
    class PrimaryTimeTableGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User me = createUser(1L, "민수");
            DgLecture lecture1 = createLecture(1L, 2025, 1, "자료구조");
            DgLectureTime dgLectureTime1 = createDgLectureTime(1L,
                    lecture1,
                    "월",
                    Time.valueOf(LocalTime.of(11, 2, 0)),
                    Time.valueOf(LocalTime.of(12, 2, 0))
            );

            DgLecture lecture2 = createLecture(1L, 2025, 1, "알고리즘");
            DgLectureTime dgLectureTime2 = createDgLectureTime(2L,
                    lecture2,
                    "화",
                    Time.valueOf(LocalTime.of(13, 2, 0)),
                    Time.valueOf(LocalTime.of(14, 2, 0))
            );

            DgLecture lecture3 = createLecture(1L, 2025, 1, "자바 프로그래밍");
            DgLectureTime dgLectureTime3 = createDgLectureTime(2L,
                    lecture3,
                    "수",
                    Time.valueOf(LocalTime.of(15, 2, 0)),
                    Time.valueOf(LocalTime.of(16, 2, 0))
            );

            TimeTable timeTable = createTimeTable(1L, 2025, 1, me, true);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.of(timeTable));
            given(timetableService.getTimetableWithLectures(eq(timeTable))).willReturn(lectureTimeMap);

            // when
            myTimeTableService.getMyPrimaryTimeTable(me);

            // then
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
            then(timetableService).should(times(1)).getTimetableWithLectures(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 사용자에게 기본 시간표가 존재하지 않을 때")
        void whenPrimaryTimeTableDoesNotExist_MustReturnError() {
            // given
            User me = createUser(1L, "민수");
            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.getMyPrimaryTimeTable(me))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoPrimaryTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
        }

    }


    @Nested
    @DisplayName("시간표 추가")
    class TimeTableCreateTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            int year = 2025;
            int semester = 2;
            MyTimeTableCreateRequestDto request = new MyTimeTableCreateRequestDto(year, semester);

            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(1L, year, semester, me, false);

            given(timeTableRepository.findByUserAndAcademicYearAndSemester(eq(me), eq(request.getYear()), eq(request.getSemester())))
                    .willReturn(Optional.empty());

            given(timeTableRepository.save(any())).willReturn(timeTable);

            // when
            myTimeTableService.createMyTimeTable(me, request);

            // then
            then(timeTableRepository).should(times(1)).findByUserAndAcademicYearAndSemester(eq(me), eq(request.getYear()), eq(request.getSemester()));
            then(timeTableRepository).should(times(1)).save(any());

        }

        @Test
        @DisplayName("실패 - 동일한 학년도와 학기에 이미 시간표가 존재할 때")
        void whenTimeTableAlreadyExistsForYearAndSemester_MustReturnError() {
            // given
            int year = 2025;
            int semester = 2;
            MyTimeTableCreateRequestDto request = new MyTimeTableCreateRequestDto(year, semester);

            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(1L, year, semester, me, false);

            given(timeTableRepository.findByUserAndAcademicYearAndSemester(eq(me), eq(request.getYear()), eq(request.getSemester())))
                    .willReturn(Optional.of(timeTable));

            // when & then
            assertThatThrownBy(() -> myTimeTableService.createMyTimeTable(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyExistSeasonTable.getMessage());

            then(timeTableRepository).should(times(1)).findByUserAndAcademicYearAndSemester(eq(me), eq(request.getYear()), eq(request.getSemester()));
        }
    }

    @Nested
    @DisplayName("개인 시간표 삭제")
    class TimeTableDeleteTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(1L, 2025, 2, me, false);

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.of(timeTable));

            // when
            myTimeTableService.deleteTimeTable(me, timetableId);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timetableId), eq(me));
        }

        @Test
        @DisplayName("실패 - 사용자에게 해당 ID의 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExistForUser_MustReturnError() {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.deleteTimeTable(me, timetableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timetableId), eq(me));
        }
    }

    @Nested
    @DisplayName("Primary 시간표 등록")
    class PrimaryTimeTableUpdateTest {
        @Test
        @DisplayName("성공 - 기존에 Primary 시간표가 존재할 경우")
        void whenOriginalPrimaryExists_MustSwitchPrimarySuccessfully() {
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable foundTimeTable = createTimeTable(1L, 2025, 2, me, false);
            TimeTable originalPrimaryTimeTable = createTimeTable(2L, 2025, 1, me, true);

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.of(foundTimeTable));
            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.of(originalPrimaryTimeTable));

            // when
            myTimeTableService.updateMyPrimaryTimeTable(me, timetableId);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timetableId), eq(me));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
            then(timetableNotificationService).should(times(1)).cancelNotification(eq(originalPrimaryTimeTable));
            then(timetableNotificationService).should(times(1)).scheduleNotification(eq(foundTimeTable));
        }

        @Test
        @DisplayName("성공 - 기존에 Primary 시간표가 존재하지 경우")
        void whenNoOriginalPrimaryExists_MustUpdatePrimarySuccessfully() {
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable foundTimeTable = createTimeTable(1L, 2025, 2, me, false);

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.of(foundTimeTable));
            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.empty());

            // when
            myTimeTableService.updateMyPrimaryTimeTable(me, timetableId);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timetableId), eq(me));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
            then(timetableNotificationService).should(never()).cancelNotification(any(TimeTable.class));
            then(timetableNotificationService).should(times(1)).scheduleNotification(eq(foundTimeTable));
        }

        @Test
        @DisplayName("실패 - 요청한 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExist_MustReturnError() {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.updateMyPrimaryTimeTable(me, timetableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timetableId), eq(me));

        }

        @Test
        @DisplayName("실패 - 요청한 시간표가 이미 Primary인 경우")
        void whenTimeTableIsAlreadyPrimary_MustReturnError() {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable foundTimeTable = createTimeTable(1L, 2025, 2, me, true);

            given(timeTableRepository.findByIdAndUser(eq(timetableId), eq(me))).willReturn(Optional.of(foundTimeTable));

            // when & then
            assertThatThrownBy(() -> myTimeTableService.updateMyPrimaryTimeTable(me, timetableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyPrimaryTimeTableError.getMessage());
        }
    }

    @Nested
    @DisplayName("Primary 시간표 삭제")
    class PrimaryTimeTableDeleteTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(timetableId, 2025, 2, me, true);

            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.of(timeTable));

            // when
            myTimeTableService.deleteMyPrimaryTimeTable(me);

            // then
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
            then(timetableNotificationService).should(times(1)).cancelNotification(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 사용자에게 기본 시간표가 존재하지 않을 때")
        void whenPrimaryTimeTableDoesNotExist_MustReturnError() {
            // given
            User me = createUser(1L, "민수");
            given(timeTableRepository.findByUserAndIsPrimary(eq(me), eq(true))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> myTimeTableService.deleteMyPrimaryTimeTable(me))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoPrimaryTimeTableError.getMessage());

            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(me), eq(true));
            then(timetableNotificationService).should(never()).cancelNotification(any());
        }
    }

    private TimeTable createTimeTable(Long id, int year, int semester, User user, boolean isPrimary) {
        return TimeTable.builder()
                .id(id)
                .academicYear(year)
                .semester(semester)
                .user(user)
                .isPrimary(isPrimary)
                .build();
    }

    private TimeTableLecture createTimeTableLecture(Long id, DgLecture lecture, TimeTable timeTable, String lectureColor) {
        return TimeTableLecture.builder()
                .id(id)
                .lecture(lecture)
                .timeTable(timeTable)
                .lectureColor(lectureColor)
                .build();
    }

    private User createUser(Long userId, String name) {
        return User.builder()
                .id(userId)
                .name(name)
                .build();
    }

    private DgLecture createLecture(Long id, int year, int semester, String courseName) {
        return DgLecture.builder()
                .id(id)
                .curriculum("컴퓨터공학과 2025")
                .area("전공필수")
                .targetGrade("2학년")
                .courseCode("CS201")
                .courseName(courseName)
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
                .academicYear(year)
                .semester(semester)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }

    public DgLectureTime createDgLectureTime(Long id, DgLecture dgLecture, String dayOfWeek, Time startTime, Time endTime) {
        return DgLectureTime.builder()
                .id(id)
                .dgLecture(dgLecture)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}