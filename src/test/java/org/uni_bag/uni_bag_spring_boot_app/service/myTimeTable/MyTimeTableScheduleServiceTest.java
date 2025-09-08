package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MyTimeTableScheduleServiceTest {
    @InjectMocks
    private MyTimeTableScheduleService myTimeTableScheduleService;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private DgLectureRepository dgLectureRepository;

    @Mock
    private DgLectureTimeRepository dgLectureTimeRepository;

    @Mock
    private TimeTableLectureRepository timeTableLectureRepository;

    @Nested
    @DisplayName("개인 시간표 강의 일정 등록")
    class MyTimeTableScheduleAddTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            DgLecture lecture1 = createLecture(1L, "자료구조", 2);
            DgLecture lecture2 = createLecture(2L, "알고리즘", 2);
            DgLecture lecture3 = createLecture(3L, "알고리즘", 2);

            TimeTableLecture existingTimeTableLecture1 = createTimeTableLecture(2L, lecture2, timeTable, "#ffff00");
            TimeTableLecture existingTimeTableLecture2 = createTimeTableLecture(2L, lecture3, timeTable, "#ffff11");

            LectureColor newLectureColor1 = new LectureColor(lecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(1L))).willReturn(Optional.of(lecture1));
            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(List.of(existingTimeTableLecture1, existingTimeTableLecture2));

            // when
            myTimeTableScheduleService.createMyTimeTableSchedule(me, request);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(1L));
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(eq(lecture1));
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(eq(timeTable));

            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(eq(lecture2));
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(eq(lecture3));
            then(timeTableLectureRepository).should(times(1)).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.empty());

            DgLecture lecture = createLecture(1L, "자료구조", 2);
            LectureColor newLectureColor = new LectureColor(lecture, "#ffffff");
            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor);
            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.createMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnError() {
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me, 1);

            DgLecture lecture = createLecture(999L, "자료구조", 2);
            LectureColor newLectureColor = new LectureColor(lecture, "#ffffff");
            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor);

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(newLectureScheduleDto.getLectureId()))).willReturn(Optional.empty());

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.createMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchLectureError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(newLectureScheduleDto.getLectureId()));
        }

        @Test
        @DisplayName("실패 - 학기 불일치")
        void whenSemesterMismatch_MustReturnError() {
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me, 1);

            DgLecture lecture = createLecture(999L, "자료구조", 2);
            LectureColor newLectureColor = new LectureColor(lecture, "#ffffff");
            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor);

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(newLectureScheduleDto.getLectureId()))).willReturn(Optional.of(lecture));

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.createMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.SemesterMismatchError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(newLectureScheduleDto.getLectureId()));
        }

        @Test
        @DisplayName("실패 - 이미 추가된 강의 중복")
        void whenAlreadyExistLecture_MustReturnError() {
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            DgLecture lecture = createLecture(1L, "자료구조", 2);

            LectureColor newLectureColor = new LectureColor(lecture, "#ffffff");
            TimeTableLecture existingTimeTableLecture = createTimeTableLecture(2L, lecture, timeTable, "#ffff00");


            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor);

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(newLectureScheduleDto.getLectureId()))).willReturn(Optional.of(lecture));
            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(List.of(existingTimeTableLecture));

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.createMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyExistLectureScheduleError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(newLectureScheduleDto.getLectureId()));
            then(dgLectureTimeRepository).should(times(2)).findAllByDgLecture(eq(lecture));
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 강의 시간이 겹침")
        void whenOverlappingLecture_MustReturnError() {
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            DgLectureTime lecture1Time = createTimeTableLectureTime(1L, "월", Time.valueOf(LocalTime.of(12, 0)), Time.valueOf(LocalTime.of(13, 0)));
            DgLectureTime lecture2Time = createTimeTableLectureTime(2L, "월", Time.valueOf(LocalTime.of(12, 0)), Time.valueOf(LocalTime.of(13, 0)));

            DgLecture lecture1 = createLecture(1L, "자료구조", List.of(lecture1Time));
            DgLecture lecture2 = createLecture(2L, "알고리즘", List.of(lecture2Time));

            TimeTableLecture existingTimeTableLecture1 = createTimeTableLecture(2L, lecture2, timeTable, "#ffff00");

            LectureColor newLectureColor1 = new LectureColor(lecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto = NewLectureScheduleDto.of(newLectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(1L))).willReturn(Optional.of(lecture1));
            given(dgLectureTimeRepository.findAllByDgLecture(eq(lecture1))).willReturn(List.of(lecture1Time));
            given(dgLectureTimeRepository.findAllByDgLecture(eq(lecture2))).willReturn(List.of(lecture2Time));
            given(timeTableLectureRepository.findAllByTimeTable(eq(timeTable))).willReturn(List.of(existingTimeTableLecture1));

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.createMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.OverLappingLectureError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(1L));
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(eq(lecture1));
            then(dgLectureTimeRepository).should(times(1)).findAllByDgLecture(eq(lecture2));
            then(timeTableLectureRepository).should(times(1)).findAllByTimeTable(eq(timeTable));
        }
    }

    @Nested
    @DisplayName("개인 시간표 강의 일정 등록")
    class NdrimsTimeTableScheduleAddTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(1L, me, 2);

            DgLecture lecture1 = createLecture(1L, "자료구조", 2);
            DgLecture lecture2 = createLecture(2L, "알고리즘", 2);

            NewNdrimsLectureSchedule newLectureScheduleDto1 =
                    new NewNdrimsLectureSchedule("CS101", "#ffffff");
            NewNdrimsLectureSchedule newLectureScheduleDto2 =
                    new NewNdrimsLectureSchedule("CS102", "#ffff00");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2));

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester(eq("CS101"), eq(2025), eq(2)))
                    .willReturn(Optional.of(lecture1));
            given(dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester(eq("CS102"), eq(2025), eq(2)))
                    .willReturn(Optional.of(lecture2));

            // when
            myTimeTableScheduleService.createNdrimsTimeTableSchedule(me, request);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findByCourseCodeAndAcademicYearAndSemester(eq("CS101"), eq(2025), eq(2));
            then(dgLectureRepository).should(times(1)).findByCourseCodeAndAcademicYearAndSemester(eq("CS102"), eq(2025), eq(2));
            then(timeTableLectureRepository).should(times(2)).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User me = createUser();

            NewNdrimsLectureSchedule newLectureScheduleDto1 =
                    new NewNdrimsLectureSchedule("CS101", "#ffffff");
            NewNdrimsLectureSchedule newLectureScheduleDto2 =
                    new NewNdrimsLectureSchedule("CS102", "#ffff00");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2));

            assertThatThrownBy(() -> myTimeTableScheduleService.createNdrimsTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(1L, me, 2);

            DgLecture lecture1 = createLecture(1L, "자료구조", 2);

            NewNdrimsLectureSchedule newLectureScheduleDto1 =
                    new NewNdrimsLectureSchedule("CS101", "#ffffff");
            NewNdrimsLectureSchedule newLectureScheduleDto2 =
                    new NewNdrimsLectureSchedule("CS102", "#ffff00");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2));

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester(eq("CS101"), eq(2025), eq(2)))
                    .willReturn(Optional.of(lecture1));
            given(dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester(eq("CS102"), eq(2025), eq(2)))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> myTimeTableScheduleService.createNdrimsTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchLectureError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findByCourseCodeAndAcademicYearAndSemester(eq("CS101"), eq(2025), eq(2));
            then(dgLectureRepository).should(times(1)).findByCourseCodeAndAcademicYearAndSemester(eq("CS102"), eq(2025), eq(2));
        }
    }

    @Nested
    @DisplayName("개인 시간표 강의 일정 삭제")
    class MyTimeTableScheduleDeleteTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() {
            // given
            User me = createUser();
            long timeTableId = 1L;
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            long lectureId = 1L;
            DgLecture lecture = createLecture(lectureId, "자료구조", 2);

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(lectureId))).willReturn(Optional.of(lecture));

            TimeTableLecture timeTableLecture = createTimeTableLecture(1L, lecture, timeTable, "#ffffff");
            given(timeTableLectureRepository.findByTimeTableAndLecture(eq(timeTable), eq(lecture))).willReturn(Optional.of(timeTableLecture));

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, lectureId);

            // when
            myTimeTableScheduleService.deleteMyTimeTableSchedule(me, request);

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(lectureId));
            then(timeTableLectureRepository).should(times(1)).findByTimeTableAndLecture(eq(timeTable), eq(lecture));
            then(timeTableLectureRepository).should(times(1)).delete(eq(timeTableLecture));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnError() {
            // given
            User me = createUser();
            long timeTableId = 1L;
            long lectureId = 1L;

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.empty());

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, lectureId);

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.deleteMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnError() {
            // given
            User me = createUser();
            long timeTableId = 1L;
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            long lectureId = 999L;

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(lectureId))).willReturn(Optional.empty());

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, lectureId);

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.deleteMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchLectureError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(lectureId));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 스케줄")
        void whenNoSuchLectureSchedule_MustReturnError() {
            // given
            User me = createUser();
            long timeTableId = 1L;
            TimeTable timeTable = createTimeTable(timeTableId, me, 2);

            long lectureId = 1L;
            DgLecture lecture = createLecture(lectureId, "자료구조", 2);

            given(timeTableRepository.findByIdAndUser(eq(timeTableId), eq(me))).willReturn(Optional.of(timeTable));
            given(dgLectureRepository.findById(eq(lectureId))).willReturn(Optional.of(lecture));
            given(timeTableLectureRepository.findByTimeTableAndLecture(eq(timeTable), eq(lecture))).willReturn(Optional.empty());

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, lectureId);

            // when
            assertThatThrownBy(() -> myTimeTableScheduleService.deleteMyTimeTableSchedule(me, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableScheduleError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findByIdAndUser(eq(timeTableId), eq(me));
            then(dgLectureRepository).should(times(1)).findById(eq(lectureId));
            then(timeTableLectureRepository).should(times(1)).findByTimeTableAndLecture(eq(timeTable), eq(lecture));
        }
    }

    private TimeTable createTimeTable(Long id, User user, int semester) {
        return TimeTable.builder()
                .id(id)
                .academicYear(2025)
                .semester(semester)
                .user(user)
                .isPrimary(false)
                .build();
    }


    private User createUser() {
        return User.builder()
                .id(1L)
                .name("민수")
                .build();
    }

    private DgLecture createLecture(Long id, String courseName, int semester) {
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
                .semester(semester)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }

    private DgLecture createLecture(Long id, String courseName, List<DgLectureTime> dgLectureTimes) {
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
                .dgLectureTimes(dgLectureTimes)
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

    private DgLectureTime createTimeTableLectureTime(Long id, String dayOfWeek, Time startTime, Time endTime) {
        return DgLectureTime.builder()
                .id(id)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}