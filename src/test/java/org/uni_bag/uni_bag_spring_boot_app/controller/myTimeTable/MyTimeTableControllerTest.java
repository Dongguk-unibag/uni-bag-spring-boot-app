package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableService;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MyTimeTableController.class)
class MyTimeTableControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyTimeTableService myTimeTableService;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("특정 개인 시간표 조회")
    class TimeTableGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timeTableId = 1L;

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

            TimeTable timeTable = createTimeTable(timeTableId, 2025, 1, me, false);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            MyTimeTableReadResponseDto response = MyTimeTableReadResponseDto.of(timeTable, lectureTimeMap);

            given(myTimeTableService.getMyTimeTableById(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/my/timeTable/{timeTableId}", timeTableId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.timeTableInfo.timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.year").value("2025"))
                    .andExpect(jsonPath("$.timeTableInfo.semester").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.primary").value("false"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID로 조회했을 때")
        void whenTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            Long timeTableId = 999L;

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableService.getMyTimeTableById(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(get("/api/my/timeTable/{timeTableId}", timeTableId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }
    }

    @Nested
    @DisplayName("개인 시간표 리스트 조회")
    class MyTimeTableListGetTest {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success() throws Exception {
            // given
            User me = createUser(1L, "민수");
            TimeTable timeTable1 = createTimeTable(1L, 2024, 2, me, true);
            TimeTable timeTable2 = createTimeTable(2L, 2025, 1, me, false);
            TimeTable timeTable3 = createTimeTable(3L, 2025, 2, me, false);

            MyTimeTableListReadResponseDto response = MyTimeTableListReadResponseDto.fromEntity(List.of(timeTable1, timeTable2, timeTable3));

            given(myTimeTableService.getMyTimeTableList(any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/my/timeTable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.timeTables[0].timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTables[0].year").value("2024"))
                    .andExpect(jsonPath("$.timeTables[0].semester").value("2"))
                    .andExpect(jsonPath("$.timeTables[0].primary").value("true"))
                    .andExpect(jsonPath("$.timeTables[1].timeTableId").value("2"))
                    .andExpect(jsonPath("$.timeTables[1].year").value("2025"))
                    .andExpect(jsonPath("$.timeTables[1].semester").value("1"))
                    .andExpect(jsonPath("$.timeTables[1].primary").value("false"))
                    .andExpect(jsonPath("$.timeTables[2].timeTableId").value("3"))
                    .andExpect(jsonPath("$.timeTables[2].year").value("2025"))
                    .andExpect(jsonPath("$.timeTables[2].semester").value("2"))
                    .andExpect(jsonPath("$.timeTables[2].primary").value("false"));
        }
    }

    @Nested
    @DisplayName("특정 시간표에 대한 강의 조회")
    class MyEnrolledLectureGetTest {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success() throws Exception {
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

            MyEnrolledLectureReadResponseDto response =
                    MyEnrolledLectureReadResponseDto.of(List.of(timeTableLecture1, timeTableLecture2, timeTableLecture3));


            given(myTimeTableService.getMyEnrolledLecture(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/my/timeTable/{timeTableId}/lecture", timeTableId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.enrolledLectures[0].lectureId").value(1L))
                    .andExpect(jsonPath("$.enrolledLectures[0].lectureName").value("알고리즘"))
                    .andExpect(jsonPath("$.enrolledLectures[0].lectureColor").value("#ffff99"))
                    .andExpect(jsonPath("$.enrolledLectures[1].lectureId").value(2L))
                    .andExpect(jsonPath("$.enrolledLectures[1].lectureName").value("자료구조"))
                    .andExpect(jsonPath("$.enrolledLectures[1].lectureColor").value("#ffff00"))
                    .andExpect(jsonPath("$.enrolledLectures[2].lectureId").value(3L))
                    .andExpect(jsonPath("$.enrolledLectures[2].lectureName").value("스프링 부트"))
                    .andExpect(jsonPath("$.enrolledLectures[2].lectureColor").value("#ffff11"))
            ;
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 시간표 ID로 조회했을 때")
        @WithMockUser
        void whenTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            Long timeTableId = 999L;

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableService.getMyEnrolledLecture(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(get("/api/my/timeTable/{timeTableId}/lecture", timeTableId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }
    }

    @Nested
    @DisplayName("기본 시간표 조회")
    class MyPrimaryTimeTableGetTest {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success() throws Exception {
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

            MyTimetableGetResponseDto responseDto = MyTimetableGetResponseDto.of(timeTable, lectureTimeMap);

            given(myTimeTableService.getMyPrimaryTimeTable(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(get("/api/my/timeTable/primary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.timeTableInfo.timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.year").value("2025"))
                    .andExpect(jsonPath("$.timeTableInfo.semester").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.primary").value("true"));
        }

        @Test
        @DisplayName("실패 - 사용자의 기본 시간표가 존재하지 않을 때")
        @WithMockUser
        void whenPrimaryTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            HttpErrorCode noPrimaryTimeTableError = HttpErrorCode.NoPrimaryTimeTableError;
            given(myTimeTableService.getMyPrimaryTimeTable(any())).willThrow(new HttpErrorException(noPrimaryTimeTableError));

            // when & then
            mockMvc.perform(get("/api/my/timeTable/primary"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noPrimaryTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noPrimaryTimeTableError.getMessage()));
        }
    }

    @Nested
    @DisplayName("시간표 추가")
    class TimeTableCreateTest {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success() throws Exception {
            // given
            int year = 2025;
            int semester = 2;
            MyTimeTableCreateRequestDto request = new MyTimeTableCreateRequestDto(year, semester);

            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(1L, year, semester, me, false);
            MyTimeTableCreateResponseDto response = MyTimeTableCreateResponseDto.fromEntity(timeTable);

            given(myTimeTableService.createMyTimeTable(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/my/timeTable")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timetableId").value("1"))
                    .andExpect(jsonPath("$.year").value(year))
                    .andExpect(jsonPath("$.semester").value(semester))
                    .andExpect(jsonPath("$.message").value("성공적으로 시간표를 생성하였습니다"));
        }

        @Test
        @DisplayName("실패 - 동일 연도와 학기의 시간표가 이미 존재할 때")
        @WithMockUser
        void whenTimeTableAlreadyExists_MustReturnError() throws Exception {
            // given
            int year = 2025;
            int semester = 2;
            MyTimeTableCreateRequestDto request = new MyTimeTableCreateRequestDto(year, semester);

            HttpErrorCode alreadyExistSeasonTable = HttpErrorCode.AlreadyExistSeasonTable;
            given(myTimeTableService.createMyTimeTable(any(), any())).willThrow(new HttpErrorException(alreadyExistSeasonTable));

            // when & then
            mockMvc.perform(post("/api/my/timeTable")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyExistSeasonTable.name()))
                    .andExpect(jsonPath("$.message").value(alreadyExistSeasonTable.getMessage()));
        }
    }

    @Nested
    @DisplayName("Primary 시간표 등록")
    class PrimaryTimeTableUpdateTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void whenValidRequest_MustUpdatePrimaryTimeTable() throws Exception {
            // given
            Long timetableId = 1L;
            User me = createUser(1L, "민수");
            TimeTable timeTable = createTimeTable(timetableId, 2025, 2, me, false);
            MyPrimaryTimeTableUpdateResponseDto responseDto = MyPrimaryTimeTableUpdateResponseDto.fromEntity(timeTable);

            given(myTimeTableService.updateMyPrimaryTimeTable(any(), any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(put("/api/my/timeTable/primary/{timetableId}", timetableId).with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeTableId").value(timetableId))
                    .andExpect(jsonPath("$.primary").value(false));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID로 요청했을 때")
        void whenTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            Long timetableId = 999L;

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableService.updateMyPrimaryTimeTable(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(put("/api/my/timeTable/primary/{timetableId}", timetableId).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 이미 Primary인 시간표를 다시 등록했을 때")
        void whenTimeTableIsAlreadyPrimary_MustReturnError() throws Exception {
            // given
            Long timetableId = 1L;

            HttpErrorCode alreadyPrimaryTimeTableError = HttpErrorCode.AlreadyPrimaryTimeTableError;
            given(myTimeTableService.updateMyPrimaryTimeTable(any(), any())).willThrow(new HttpErrorException(alreadyPrimaryTimeTableError));

            // when & then
            mockMvc.perform(put("/api/my/timeTable/primary/{timetableId}", timetableId).with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyPrimaryTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(alreadyPrimaryTimeTableError.getMessage()));
        }
    }

    @Nested
    @DisplayName("Primary 시간표 삭제")
    class PrimaryTimeTableDeleteTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timetableId = 1L;
            MyPrimaryTimeTableUpdateResponseDto responseDto =
                    new MyPrimaryTimeTableUpdateResponseDto(timetableId, false);

            given(myTimeTableService.deleteMyPrimaryTimeTable(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/primary").with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeTableId").value(timetableId))
                    .andExpect(jsonPath("$.primary").value(false));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - Primary 시간표 없음")
        void whenNoPrimaryTimeTableExist_MustReturnError() throws Exception {
            // given
            HttpErrorCode noPrimaryTimeTableError = HttpErrorCode.NoPrimaryTimeTableError;
            given(myTimeTableService.deleteMyPrimaryTimeTable(any())).willThrow(new HttpErrorException(noPrimaryTimeTableError));

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/primary").with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noPrimaryTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noPrimaryTimeTableError.getMessage()));
        }
    }

    @Nested
    @DisplayName("개인 시간표 삭제")
    class TimeTableDeleteTest {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timeTableId = 1L;
            MyTimeTableDeleteResponseDto responseDto = MyTimeTableDeleteResponseDto.of(timeTableId);

            given(myTimeTableService.deleteTimeTable(any(), any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/{timeTableId}", timeTableId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.timeTableId").value(timeTableId))
                    .andExpect(jsonPath("$.message").value("성공적으로 시간표를 삭제하였습니다."));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표")
        void whenNoTimeTableExist_MustReturnError() throws Exception {
            // given
            Long invalidId = 999L;
            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableService.deleteTimeTable(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/{timeTableId}", invalidId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()) // 전역 ExceptionHandler 매핑 방식에 따라 400/404 조정
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
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
                .year(year)
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