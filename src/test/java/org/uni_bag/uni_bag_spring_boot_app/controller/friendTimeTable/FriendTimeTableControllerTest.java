package org.uni_bag.uni_bag_spring_boot_app.controller.friendTimeTable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable.FriendTimeTableService;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendTimeTableController.class)
class FriendTimeTableControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendTimeTableService friendTimeTableService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("친구 시간표 리스트 조회")
    class FriendTimeTableListGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long friendId = 1L;

            User user = createUser(friendId, "민수");
            TimeTable timeTable1 = createTimeTable(1L, 2025, 1, user);
            TimeTable timeTable2 = createTimeTable(2L, 2025, 2, user);

            FriendTimeTableListReadResponseDto response =
                    FriendTimeTableListReadResponseDto.fromEntity(user, List.of(timeTable1, timeTable2));

            given(friendTimeTableService.getFriendTimeTableList(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/friend/timeTable").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.friendId").value(friendId))
                    .andExpect(jsonPath("$.friendName").value("민수"))
                    .andExpect(jsonPath("$.timeTables[0].timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTables[0].year").value("2025"))
                    .andExpect(jsonPath("$.timeTables[0].semester").value("1"))
                    .andExpect(jsonPath("$.timeTables[1].timeTableId").value("2"))
                    .andExpect(jsonPath("$.timeTables[1].year").value("2025"))
                    .andExpect(jsonPath("$.timeTables[1].semester").value("2"));


        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 대상 유저가 존재하지 않을 때")
        void whenFolloweeDoesNotExist_MustReturnError() throws Exception {
            // given
            Long friendId = 1L;

            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(friendTimeTableService.getFriendTimeTableList(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 관계가 존재하지 않아 접근 권한이 없을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() throws Exception {
            // given
            Long friendId = 1L;

            HttpErrorCode accessDeniedError = HttpErrorCode.AccessDeniedError;
            given(friendTimeTableService.getFriendTimeTableList(any(), any())).willThrow(new HttpErrorException(accessDeniedError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value(accessDeniedError.name()))
                    .andExpect(jsonPath("$.message").value(accessDeniedError.getMessage()));
        }
    }

    @Nested
    @DisplayName("친구 특정 시간표 조회")
    class FriendTimeTableGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timeTableId = 1L;

            User friend = createUser(1L, "민수");
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

            TimeTable timeTable = createTimeTable(timeTableId, 2025, 1, friend);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            FriendTimeTableReadResponseDto response = FriendTimeTableReadResponseDto.of(friend, timeTable, lectureTimeMap);

            given(friendTimeTableService.getFriendTimeTableById(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/{timeTableId}", timeTableId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.friendId").value("1"))
                    .andExpect(jsonPath("$.friendName").value("민수"))
                    .andExpect(jsonPath("$.timeTableInfo.timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.year").value("2025"))
                    .andExpect(jsonPath("$.timeTableInfo.semester").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.primary").value("false"))
//                    .andExpect(jsonPath("$.lectures[0].lectureId").value("1"))
//                    .andExpect(jsonPath("$.lectures[0].lectureName").value("자료구조"))
//                    .andExpect(jsonPath("$.lectures[0].year").value("1"))
//                    .andExpect(jsonPath("$.lectures[0].semester").value("1"))
//                    .andExpect(jsonPath("$.lectures[0].lectureTimes[0].dayOfWeek").value("월"))
//                    .andExpect(jsonPath("$.lectures[0].lectureTimes[0].startTime").value("11:02:00"))
//                    .andExpect(jsonPath("$.lectures[0].lectureTimes[0].endTime").value("12:02:00"))
            ;
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            Long timeTableId = 1L;

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(friendTimeTableService.getFriendTimeTableById(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/{timeTableId}", timeTableId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 관계가 존재하지 않아 접근 권한이 없을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() throws Exception {
            // given
            Long timeTableId = 1L;

            HttpErrorCode accessDeniedError = HttpErrorCode.AccessDeniedError;
            given(friendTimeTableService.getFriendTimeTableById(any(), any())).willThrow(new HttpErrorException(accessDeniedError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/{timeTableId}", timeTableId))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value(accessDeniedError.name()))
                    .andExpect(jsonPath("$.message").value(accessDeniedError.getMessage()));
        }
    }

    @Nested
    @DisplayName("친구 primary 시간표 조회")
    class FriendPrimaryTimeTableGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long friendId = 1L;

            User friend = createUser(friendId, "민수");
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

            TimeTable timeTable = createTimeTable(1L, 2025, 1, friend);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            FriendTimeTableReadResponseDto response = FriendTimeTableReadResponseDto.of(friend, timeTable, lectureTimeMap);

            given(friendTimeTableService.getFriendPrimaryTimeTable(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/primary").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.friendId").value("1"))
                    .andExpect(jsonPath("$.friendName").value("민수"))
                    .andExpect(jsonPath("$.timeTableInfo.timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.year").value("2025"))
                    .andExpect(jsonPath("$.timeTableInfo.semester").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.primary").value("false"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 대상 유저가 존재하지 않을 때")
        void whenFolloweeDoesNotExist_MustReturnError() throws Exception {
            // given
            Long friendId = 1L;

            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(friendTimeTableService.getFriendPrimaryTimeTable(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/primary").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 관계가 존재하지 않을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() throws Exception {
            // given
            Long friendId = 1L;

            HttpErrorCode accessDeniedError = HttpErrorCode.AccessDeniedError;
            given(friendTimeTableService.getFriendPrimaryTimeTable(any(), any())).willThrow(new HttpErrorException(accessDeniedError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/primary").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value(accessDeniedError.name()))
                    .andExpect(jsonPath("$.message").value(accessDeniedError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 기본 시간표가 존재하지 않을 때")
        void whenPrimaryTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            Long friendId = 1L;

            HttpErrorCode noPrimaryTimeTableError = HttpErrorCode.NoPrimaryTimeTableError;
            given(friendTimeTableService.getFriendPrimaryTimeTable(any(), any())).willThrow(new HttpErrorException(noPrimaryTimeTableError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/primary").param("friendId", String.valueOf(friendId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noPrimaryTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noPrimaryTimeTableError.getMessage()));
        }

    }

    @Nested
    @DisplayName("secondary 친구 시간표 조회")
    class SecondaryFriendTimeTableGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            User friend = createUser(1L, "민수");
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

            TimeTable timeTable = createTimeTable(1L, 2025, 1, friend);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            FriendTimeTableReadResponseDto response = FriendTimeTableReadResponseDto.of(friend, timeTable, lectureTimeMap);

            given(friendTimeTableService.getSecondaryFriendTimeTable(any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/secondary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.friendId").value("1"))
                    .andExpect(jsonPath("$.friendName").value("민수"))
                    .andExpect(jsonPath("$.timeTableInfo.timeTableId").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.year").value("2025"))
                    .andExpect(jsonPath("$.timeTableInfo.semester").value("1"))
                    .andExpect(jsonPath("$.timeTableInfo.primary").value("false"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 세컨더리 친구가 존재하지 않을 때")
        void whenNoSecondaryFriendExists_MustReturnError() throws Exception {
            // given
            HttpErrorCode noSecondaryFriendError = HttpErrorCode.NoSecondaryFriendError;
            given(friendTimeTableService.getSecondaryFriendTimeTable(any())).willThrow(new HttpErrorException(noSecondaryFriendError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/secondary"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSecondaryFriendError.name()))
                    .andExpect(jsonPath("$.message").value(noSecondaryFriendError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 세컨더리 친구의 기본 시간표가 존재하지 않을 때")
        void whenSecondaryFriendPrimaryTimeTableDoesNotExist_MustReturnError() throws Exception {
            // given
            HttpErrorCode noPrimaryTimeTableError = HttpErrorCode.NoPrimaryTimeTableError;
            given(friendTimeTableService.getSecondaryFriendTimeTable(any())).willThrow(new HttpErrorException(noPrimaryTimeTableError));

            // when & then
            mockMvc.perform(get("/api/friend/timeTable/secondary"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noPrimaryTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noPrimaryTimeTableError.getMessage()));

        }
    }

    private TimeTable createTimeTable(Long id, int year, int semester, User user) {
        return TimeTable.builder()
                .id(id)
                .academicYear(year)
                .semester(semester)
                .user(user)
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