package org.uni_bag.uni_bag_spring_boot_app.service.timetable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {
    @Mock
    private TimeTableLectureRepository timeTableLectureRepository;

    @Mock
    private DgLectureTimeRepository dgLectureTimeRepository;

    @InjectMocks
    private TimetableService timetableService;

    @Nested
    @DisplayName("시간표 내 강의 목록 조회")
    class TimetableWithLecturesGetTest {
        @Test
        @DisplayName("타임테이블에서 강의와 강의 시간 + 색상 정보를 가져온다 - 성공")
        void getTimetableWithLectures_success() {
            // given
            User user = createUser();
            TimeTable timeTable = createTimeTable(user);
            DgLecture lecture1 = createLecture(1L, "알고리즘");
            DgLecture lecture2 = createLecture(2L, "자료구조");

            TimeTableLecture timeTableLecture1 = createTimeTableLecture(lecture1, timeTable, "#FF0000");
            TimeTableLecture timeTableLecture2 = createTimeTableLecture(lecture2, timeTable, "#00FF00");

            List<TimeTableLecture> timeTableLectures = List.of(timeTableLecture1, timeTableLecture2);

            List<DgLectureTime> lecture1Times = List.of(createDgLectureTime(2L,
                    lecture1,
                    "월",
                    Time.valueOf(LocalTime.of(13, 2, 0)),
                    Time.valueOf(LocalTime.of(14, 2, 0))
            ));
            List<DgLectureTime> lecture2Times = List.of(createDgLectureTime(2L,
                    lecture2,
                    "화",
                    Time.valueOf(LocalTime.of(13, 2, 0)),
                    Time.valueOf(LocalTime.of(14, 2, 0))
            ));

            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(timeTableLectures);
            given(dgLectureTimeRepository.findAllByDgLecture(eq(lecture1))).willReturn(lecture1Times);
            given(dgLectureTimeRepository.findAllByDgLecture(eq(lecture2))).willReturn(lecture2Times);

            // when
            timetableService.getTimetableWithLectures(timeTable);

            // then
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(timeTable);
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(lecture1);
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(lecture2);
        }

        @Test
        @DisplayName("타임테이블에 강의가 없을 경우 빈 Map 반환")
        void getTimetableWithLectures_empty() {
            // given
            User user = createUser();
            TimeTable timeTable = createTimeTable(user);
            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(List.of());

            // when
            timetableService.getTimetableWithLectures(timeTable);

            // then
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(timeTable);
            then(dgLectureTimeRepository).shouldHaveNoInteractions();
        }
    }

    private TimeTable createTimeTable(User user) {
        return TimeTable.builder()
                .id(1L)
                .academicYear(2025)
                .semester(2)
                .user(user)
                .isPrimary(false)
                .build();
    }

    private TimeTableLecture createTimeTableLecture(DgLecture lecture, TimeTable timeTable, String lectureColor) {
        return TimeTableLecture.builder()
                .id(1L)
                .lecture(lecture)
                .timeTable(timeTable)
                .lectureColor(lectureColor)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("최민수")
                .build();
    }

    private DgLecture createLecture(Long id, String courseName) {
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
                .academicYear(2025)
                .semester(2)
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