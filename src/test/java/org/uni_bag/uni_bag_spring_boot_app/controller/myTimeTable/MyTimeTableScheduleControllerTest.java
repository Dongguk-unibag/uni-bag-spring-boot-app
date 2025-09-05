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
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureColor;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableScheduleService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyTimeTableScheduleController.class)
class MyTimeTableScheduleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyTimeTableScheduleService myTimeTableScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("개인 시간표 강의 일정 등록")
    class MyTimeTableScheduleAddTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me);

            DgLecture lecture1 = createLecture(1L, "자료구조");
            DgLecture lecture2 = createLecture(2L, "알고리즘");
            LectureColor lectureColor1 = new LectureColor(lecture1, "#ffffff");
            LectureColor lectureColor2 = new LectureColor(lecture2, "#ffff00");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);
            NewLectureScheduleDto newLectureScheduleDto2 = NewLectureScheduleDto.of(lectureColor2);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2));

            MyTimeTableScheduleCreateResponseDto response =
                    MyTimeTableScheduleCreateResponseDto.fromEntity(timeTable, List.of(lectureColor1, lectureColor2));

            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeTableId").value("1"))
                    .andExpect(jsonPath("$.lectures[0].lectureId").value("1"))
                    .andExpect(jsonPath("$.lectures[0].lectureColor").value("#ffffff"))
            ;
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnNotFound() throws Exception {
            // given
            Long notExistTimeTableId = 999L;

            DgLecture lecture1 = createLecture(1L, "자료구조");
            DgLecture lecture2 = createLecture(2L, "알고리즘");
            DgLecture lecture3 = createLecture(3L, "자바 프로그래밍");
            LectureColor lectureColor1 = new LectureColor(lecture1, "#ffffff");
            LectureColor lectureColor2 = new LectureColor(lecture2, "#ffff00");
            LectureColor lectureColor3 = new LectureColor(lecture3, "#ffff11");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);
            NewLectureScheduleDto newLectureScheduleDto2 = NewLectureScheduleDto.of(lectureColor2);
            NewLectureScheduleDto newLectureScheduleDto3 = NewLectureScheduleDto.of(lectureColor3);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(notExistTimeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2, newLectureScheduleDto3));

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnNotFound() throws Exception {
            // given
            Long timeTableId = 1L;

            DgLecture notExistLecture1 = createLecture(999L, "자료구조");
            LectureColor lectureColor1 = new LectureColor(notExistLecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1));

            HttpErrorCode noSuchLectureError = HttpErrorCode.NoSuchLectureError;
            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any())).willThrow(new HttpErrorException(noSuchLectureError));

            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchLectureError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchLectureError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 학기 불일치")
        void whenSemesterMismatch_MustReturnBadRequest() throws Exception {
            // given
            Long timeTableId = 2L;

            DgLecture notExistLecture1 = createLecture(1L, "자료구조");
            LectureColor lectureColor1 = new LectureColor(notExistLecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1));

            HttpErrorCode semesterMismatchError = HttpErrorCode.SemesterMismatchError;
            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(semesterMismatchError));

            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(semesterMismatchError.name()))
                    .andExpect(jsonPath("$.message").value(semesterMismatchError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 이미 추가된 강의 중복")
        void whenAlreadyExistLecture_MustReturnConflict() throws Exception {
            Long timeTableId = 1L;

            DgLecture notExistLecture1 = createLecture(1L, "자료구조");
            LectureColor lectureColor1 = new LectureColor(notExistLecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1));

            HttpErrorCode alreadyExistLectureScheduleError = HttpErrorCode.AlreadyExistLectureScheduleError;
            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(alreadyExistLectureScheduleError));

            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyExistLectureScheduleError.name()))
                    .andExpect(jsonPath("$.message").value(alreadyExistLectureScheduleError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 강의 시간이 겹침")
        void whenOverlappingLecture_MustReturnConflict() throws Exception {
            Long timeTableId = 1L;

            DgLecture notExistLecture1 = createLecture(1L, "자료구조");
            LectureColor lectureColor1 = new LectureColor(notExistLecture1, "#ffffff");

            NewLectureScheduleDto newLectureScheduleDto1 = NewLectureScheduleDto.of(lectureColor1);

            MyTimeTableScheduleCreateRequestDto request =
                    new MyTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1));

            HttpErrorCode overLappingLectureError = HttpErrorCode.OverLappingLectureError;
            given(myTimeTableScheduleService.createMyTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(overLappingLectureError));

            mockMvc.perform(post("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(overLappingLectureError.name()))
                    .andExpect(jsonPath("$.message").value(overLappingLectureError.getMessage()));
        }
    }

    @Nested
    @DisplayName("개인 시간표 강의 일정 등록(Ndrims)")
    class NdrimsTimeTableScheduleAddTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long timeTableId = 1L;
            User me = createUser();
            TimeTable timeTable = createTimeTable(timeTableId, me);

            DgLecture lecture1 = createLecture(1L, "자료구조");
            DgLecture lecture2 = createLecture(2L, "알고리즘");
            LectureColor lectureColor1 = new LectureColor(lecture1, "#ffffff");
            LectureColor lectureColor2 = new LectureColor(lecture2, "#ffff00");

            NewNdrimsLectureSchedule newLectureScheduleDto1 =
                    new NewNdrimsLectureSchedule("CS101", "#ffffff");
            NewNdrimsLectureSchedule newLectureScheduleDto2 =
                    new NewNdrimsLectureSchedule("CS102", "#ffff00");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto1, newLectureScheduleDto2));

            MyTimeTableScheduleCreateResponseDto response =
                    MyTimeTableScheduleCreateResponseDto.fromEntity(timeTable, List.of(lectureColor1, lectureColor2));

            given(myTimeTableScheduleService.createNdrimsTimeTableSchedule(any(), any()))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/my/timeTable/schedule/ndrims")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeTableId").value("1"))
                    .andExpect(jsonPath("$.lectures[0].lectureId").value("1"))
                    .andExpect(jsonPath("$.lectures[0].lectureColor").value("#ffffff"))
                    .andExpect(jsonPath("$.lectures[1].lectureId").value("2"))
                    .andExpect(jsonPath("$.lectures[1].lectureColor").value("#ffff00"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnNotFound() throws Exception {
            // given
            Long notExistTimeTableId = 999L;
            NewNdrimsLectureSchedule newLectureScheduleDto =
                    new NewNdrimsLectureSchedule("CS101", "#ffffff");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(notExistTimeTableId, List.of(newLectureScheduleDto));

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableScheduleService.createNdrimsTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(post("/api/my/timeTable/schedule/ndrims")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnNotFound() throws Exception {
            // given
            Long timeTableId = 1L;
            NewNdrimsLectureSchedule newLectureScheduleDto =
                    new NewNdrimsLectureSchedule("CS999", "#ffffff");

            NdrimsTimeTableScheduleCreateRequestDto request =
                    new NdrimsTimeTableScheduleCreateRequestDto(timeTableId, List.of(newLectureScheduleDto));

            HttpErrorCode noSuchLectureError = HttpErrorCode.NoSuchLectureError;
            given(myTimeTableScheduleService.createNdrimsTimeTableSchedule(any(), any()))
                    .willThrow(new HttpErrorException(noSuchLectureError));

            // when & then
            mockMvc.perform(post("/api/my/timeTable/schedule/ndrims")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchLectureError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchLectureError.getMessage()));
        }
    }

    @Nested
    @DisplayName("개인 시간표 강의 일정 삭제")
    class MyTimeTableScheduleDeleteTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            User me = createUser();
            long timeTableId = 1L;
            TimeTable timeTable = createTimeTable(timeTableId, me);

            long lectureId = 1L;
            DgLecture lecture = createLecture(lectureId, "자료구조");

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, lectureId);

            MyTimeTableScheduleDeleteResponseDto response = MyTimeTableScheduleDeleteResponseDto.of(timeTable, lecture);
            given(myTimeTableScheduleService.deleteMyTimeTableSchedule(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeTableId").value("1"))
                    .andExpect(jsonPath("$.lectures.lectureId").value("1"))
                    .andExpect(jsonPath("$.lectures.lectureName").value("자료구조"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 시간표 ID")
        void whenNoSuchTimeTable_MustReturnNotFound() throws Exception {
            // given
            long timeTableId = 999L;

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, 1L);

            HttpErrorCode noSuchTimeTableError = HttpErrorCode.NoSuchTimeTableError;
            given(myTimeTableScheduleService.deleteMyTimeTableSchedule(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableError));

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenNoSuchLecture_MustReturnNotFound() throws Exception {
            // given
            long timeTableId = 1L;

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, 999L); // 존재하지 않은 강의 ID

            HttpErrorCode noSuchLectureError = HttpErrorCode.NoSuchLectureError;
            given(myTimeTableScheduleService.deleteMyTimeTableSchedule(any(), any())).willThrow(new HttpErrorException(noSuchLectureError));

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchLectureError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchLectureError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 스케줄")
        void whenNoSuchLectureSchedule_MustReturnNotFound() throws Exception {
            // given
            long timeTableId = 1L;

            MyTimeTableScheduleDeleteRequestDto request = new MyTimeTableScheduleDeleteRequestDto(timeTableId, 1L);

            HttpErrorCode noSuchTimeTableScheduleError = HttpErrorCode.NoSuchTimeTableScheduleError;
            given(myTimeTableScheduleService.deleteMyTimeTableSchedule(any(), any())).willThrow(new HttpErrorException(noSuchTimeTableScheduleError));

            // when & then
            mockMvc.perform(delete("/api/my/timeTable/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchTimeTableScheduleError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchTimeTableScheduleError.getMessage()));
        }
    }


    private TimeTable createTimeTable(Long id, User user) {
        return TimeTable.builder()
                .id(id)
                .academicYear(2025)
                .semester(1)
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
                .semester(1)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }
}