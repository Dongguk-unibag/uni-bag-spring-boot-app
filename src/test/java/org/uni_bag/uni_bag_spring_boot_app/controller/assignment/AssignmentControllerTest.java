package org.uni_bag.uni_bag_spring_boot_app.controller.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.AssignmentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssignmentController.class)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("과제 리스트 조회 성공")
    void assignmentList() throws Exception {
        // given
        AssignmentListReadResponseDto mockResponse = createAssignmentListResponse();
        given(assignmentService.getAssignmentList(any()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/assigment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments.length()").value(mockResponse.getAssignments().size()))
                .andExpect(jsonPath("$.assignments[0].assignmentId").exists())
                .andExpect(jsonPath("$.assignments[0].title").exists())
                .andExpect(jsonPath("$.assignments[0].description").exists())
                .andExpect(jsonPath("$.assignments[0].lecture.lectureId").exists())
                .andExpect(jsonPath("$.assignments[0].lecture.lectureName").exists())
                .andExpect(jsonPath("$.assignments[0].startDateTime").exists())
                .andExpect(jsonPath("$.assignments[0].endDateTime").exists())
                .andExpect(jsonPath("$.assignments[0].isCompleted").exists());

    }

    @Nested
    @DisplayName("특정 과제 조회")
    class AssignmentRead {
        @Test
        @WithMockUser
        @DisplayName("성공- 특정 과제 조회")
        void success() throws Exception {
            // given
            AssignmentReadResponseDto response = createAssignmentReadResponse();
            given(assignmentService.getAssignment(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/assigment/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.assignmentId").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.lecture").exists())
                    .andExpect(jsonPath("$.startDateTime").exists())
                    .andExpect(jsonPath("$.endDateTime").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 과제를 조회")
        void whenAssignmentDoesNotExist_MustReturn404() throws Exception {
            // given
            Long invalidAssignmentId = 999L;
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;
            given(assignmentService.getAssignment(any(), eq(invalidAssignmentId)))
                    .willThrow(new HttpErrorException(noSuchAssignmentError));

            // when & then
            mockMvc.perform(get("/api/assigment/{id}", invalidAssignmentId))
                    .andExpect(status().isNotFound()) // 예외 핸들러에서 404 매핑된다고 가정
                    .andExpect(jsonPath("$.errorCode").value(noSuchAssignmentError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAssignmentError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 작성자가 아닌 유저가 접근")
        void whenUserTriesToAccessInvalidAssignment_MustReturn404() throws Exception {
            // given
            User invalidUser = any();
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;
            given(assignmentService.getAssignment(invalidUser, any()))
                    .willThrow(new HttpErrorException(noSuchAssignmentError));

            // when & then
            mockMvc.perform(get("/api/assigment/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchAssignmentError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAssignmentError.getMessage()));
        }
    }


    @Nested
    @DisplayName("특정 과제 토글")
    class AssignmentToggle {
        @Test
        @WithMockUser
        @DisplayName("성공- 특정 과제 토글")
        void success() throws Exception {
            // given
            AssignmentCompleteToggleResponseDto response = createAssignmentCompleteToggleResponse();
            given(assignmentService.toggleAssignmentComplete(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/assigment/{id}/toggle", 1L).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assignmentId").exists())
                    .andExpect(jsonPath("$.completed").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 과제를 조회")
        void whenAssignmentDoesNotExist_MustReturn404() throws Exception {
            // given
            Long invalidAssignmentId = 999L;
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;
            given(assignmentService.toggleAssignmentComplete(any(), eq(invalidAssignmentId)))
                    .willThrow(new HttpErrorException(noSuchAssignmentError));

            // when & then
            mockMvc.perform(post("/api/assigment/{id}/toggle", invalidAssignmentId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchAssignmentError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAssignmentError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 작성자가 아닌 유저가 접근")
        void whenUserTriesToAccessInvalidAssignment_MustReturn404() throws Exception {
            // given
            User invalidUser = any();
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;
            given(assignmentService.toggleAssignmentComplete(invalidUser, any()))
                    .willThrow(new HttpErrorException(noSuchAssignmentError));

            // when & then
            mockMvc.perform(post("/api/assigment/{id}/toggle", 1L).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchAssignmentError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAssignmentError.getMessage()));
        }
    }

    @Nested
    @DisplayName("과제 생성")
    class AssignmentCreate {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void whenAssignmentCreatedWithEndDateTime_MustReturn201() throws Exception {
            // given
            AssignmentCreateResponseDto response = createAssignmentCreateResponse();
            AssignmentCreateRequestDto request = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58)
            );
            given(assignmentService.createAssignment(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/assigment")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assignmentId").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.lecture").exists())
                    .andExpect(jsonPath("$.startDateTime").exists())
                    .andExpect(jsonPath("$.endDateTime").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 강의 ID")
        void whenUserTriesToCreateAssignmentWithInvalidLectureId_MustReturn404() throws Exception {
            // given
            AssignmentCreateRequestDto request = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    999L, // 존재하지 않는 ID
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58)
            );
            HttpErrorCode noSuchLectureError = HttpErrorCode.NoSuchLectureError;
            given(assignmentService.createAssignment(any(), any()))
                    .willThrow(new HttpErrorException(noSuchLectureError));

            // when & then
            mockMvc.perform(post("/api/assigment")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchLectureError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchLectureError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 시작 시간과 종료시간이 유효하지 않음")
        void whenUserTriesToCreateAssignmentWithInvalidTimeRange_MustReturn400() throws Exception {
            // given
            AssignmentCreateRequestDto request = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 1, 18, 15, 11, 58), // 시작 시간이 종료 시간보다 늦음
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58)
            );
            HttpErrorCode notValidAssignmentTimeError = HttpErrorCode.NotValidAssignmentTimeError;
            given(assignmentService.createAssignment(any(), any()))
                    .willThrow(new HttpErrorException(notValidAssignmentTimeError));

            // when & then
            mockMvc.perform(post("/api/assigment")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(notValidAssignmentTimeError.name()))
                    .andExpect(jsonPath("$.message").value(notValidAssignmentTimeError.getMessage()));
        }
    }

    @Nested
    @DisplayName("과제 수정")
    class AssignmentUpdate {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void whenAssignmentNotFound_MustCreateNewAndReturn201() throws Exception {
            // given
            AssignmentUpdateResponseDto response = createAssignmentUpdateResponse();
            AssignmentUpdateRequestDto request = new AssignmentUpdateRequestDto(
                    "네트워크 과제",
                    "네트워크 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 2, 1, 12, 0, 0),
                    LocalDateTime.of(2025, 2, 2, 12, 0, 0)
            );
            given(assignmentService.updateAssignment(any(), anyLong(), any())).willReturn(response);

            // when & then
            mockMvc.perform(put("/api/assigment/{assignmentId}", 999L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assignmentId").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.lecture").exists())
                    .andExpect(jsonPath("$.startDateTime").exists())
                    .andExpect(jsonPath("$.endDateTime").exists());

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 Lecture ID")
        void whenUserTriesToUpdateAssignmentWithInvalidLectureId_MustReturn404() throws Exception {
            // given
            AssignmentUpdateRequestDto request = new AssignmentUpdateRequestDto(
                    "네트워크 과제",
                    "네트워크 2장 풀기",
                    999L, // 잘못된 LectureId
                    LocalDateTime.of(2025, 2, 1, 12, 0, 0),
                    LocalDateTime.of(2025, 2, 2, 12, 0, 0)
            );
            HttpErrorCode noSuchLectureError = HttpErrorCode.NoSuchLectureError;
            given(assignmentService.updateAssignment(any(), anyLong(), any()))
                    .willThrow(new HttpErrorException(noSuchLectureError));

            // when & then
            mockMvc.perform(put("/api/assigment/{assignmentId}", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchLectureError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchLectureError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 시작 시간과 종료시간이 유효하지 않음")
        void whenUserTriesToUpdateAssignmentWithInvalidTimeRange_MustReturn400() throws Exception {
            // given
            AssignmentUpdateRequestDto request = new AssignmentUpdateRequestDto(
                    "네트워크 과제",
                    "네트워크 2장 풀기",
                    1L,
                    LocalDateTime.of(2025, 2, 3, 12, 0, 0), // 시작이 종료보다 늦음
                    LocalDateTime.of(2025, 2, 2, 12, 0, 0)
            );
            HttpErrorCode notValidAssignmentTimeError = HttpErrorCode.NotValidAssignmentTimeError;
            given(assignmentService.updateAssignment(any(), anyLong(), any()))
                    .willThrow(new HttpErrorException(notValidAssignmentTimeError));

            // when & then
            mockMvc.perform(put("/api/assigment/{assignmentId}", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(notValidAssignmentTimeError.name()))
                    .andExpect(jsonPath("$.message").value(notValidAssignmentTimeError.getMessage()));
        }
    }

    @Nested
    @DisplayName("과제 삭제")
    class AssignmentDelete {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void whenUserDeletesExistingAssignment_MustReturn201() throws Exception {
            // given
            Long assignmentId = 1L;
            AssignmentDeleteResponseDto responseDto = createAssignmentDeleteResponse(assignmentId);

            given(assignmentService.deleteAssignment(any(), eq(assignmentId))).willReturn(responseDto);

            // when & then
            mockMvc.perform(delete("/api/assigment/{assignmentId}", assignmentId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assignmentId").value(assignmentId));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 과제 삭제 시")
        void whenUserTriesToDeleteNonExistingAssignment_MustReturn404() throws Exception {
            // given
            Long invalidId = 999L;
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;

            given(assignmentService.deleteAssignment(any(), eq(invalidId)))
                    .willThrow(new HttpErrorException(noSuchAssignmentError));

            // when & then
            mockMvc.perform(delete("/api/assigment/{assignmentId}", invalidId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchAssignmentError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAssignmentError.getMessage()));
        }
    }


    @Nested
    @DisplayName("완료된 과제 전체 삭제")
    class DeleteCompletedAssignments {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void whenUserDeletesAllCompletedAssignments_MustReturn201() throws Exception {
            // given
            AssignmentDeleteListResponseDto responseDto =
                    new AssignmentDeleteListResponseDto(
                            List.of(createAssignmentDeleteResponse(1L), createAssignmentDeleteResponse(2L))
                    );

            given(assignmentService.deleteCompletedAssignment(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(delete("/api/assigment/completedAssignment")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.deletedAssignment.length()").value(2))
                    .andExpect(jsonPath("$.deletedAssignment[0].assignmentId").exists());
        }
    }


    public AssignmentListReadResponseDto createAssignmentListResponse() {
        Assignment assignment1 = createAssignment(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        Assignment assignment2 = createAssignment(
                2L, "자료구조 과제", "자료구조 2장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 20, 10, 0, 0),
                LocalDateTime.of(2025, 1, 27, 23, 59, 59),
                true
        );

        return AssignmentListReadResponseDto.fromEntity(List.of(assignment1, assignment2));
    }

    public AssignmentReadResponseDto createAssignmentReadResponse() {
        Assignment assignment = createAssignment(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        return AssignmentReadResponseDto.fromEntity(assignment);
    }

    public AssignmentCompleteToggleResponseDto createAssignmentCompleteToggleResponse() {
        Assignment assignment = createAssignment(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );
        return AssignmentCompleteToggleResponseDto.fromEntity(assignment);
    }

    public AssignmentCreateResponseDto createAssignmentCreateResponse() {
        Assignment assignment = createAssignment(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        return AssignmentCreateResponseDto.fromEntity(assignment);
    }

    public AssignmentUpdateResponseDto createAssignmentUpdateResponse() {
        Assignment assignment = createAssignment(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        return AssignmentUpdateResponseDto.fromEntity(assignment);
    }

    private static AssignmentDeleteResponseDto createAssignmentDeleteResponse(Long assignmentId) {
        return new AssignmentDeleteResponseDto(
                assignmentId
        );
    }

    public static DgLecture createLecture() {
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

    public Assignment createAssignment(Long id, String title, String description,
                                       DgLecture lecture, LocalDateTime start, LocalDateTime end,
                                       boolean completed) {
        return Assignment.builder()
                .id(id)
                .title(title)
                .description(description)
                .lecture(lecture)
                .startDateTime(start)
                .endDateTime(end)
                .isCompleted(completed)
                .build();
    }
}
