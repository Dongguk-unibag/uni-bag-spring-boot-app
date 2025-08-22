package org.uni_bag.uni_bag_spring_boot_app.service.assignment;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.AssignmentCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.AssignmentUpdateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.AssignmentRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {
    @InjectMocks
    private AssignmentService assignmentService;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private DgLectureRepository dgLectureRepository;

    @Mock
    private AssignmentNotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = createUser();
    }

    @Nested
    @DisplayName("과제 목록 조회")
    class AssignmentListGet {
        @Test()
        @DisplayName("성공")
        void success() {
            // given
            List<Assignment> foundAssignments = createAssignmentList();
            given(assignmentRepository.findAllByUser(any())).willReturn(foundAssignments);

            // when
            assignmentService.getAssignmentList(any());

            // then
            then(assignmentRepository).should(times(1))
                    .findAllByUser(any());
        }
    }

    @Nested
    @DisplayName("특정 과제 조회")
    class AssignmentGet {
        @Test()
        @DisplayName("성공")
        void success() {
            // given
            Assignment foundAssignment = createAssignment();
            given(assignmentRepository.findByIdAndUser(any(), any())).willReturn(Optional.ofNullable(foundAssignment));

            // when
            assignmentService.getAssignment(any(), any());

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 과제 아이디")
        void whenAssignmentDoesNotExist_MustThrowException() {
            // given
            Long invalidAssignmentId = 999L;
            HttpErrorCode noSuchAssignmentError = HttpErrorCode.NoSuchAssignmentError;

            given(assignmentRepository.findByIdAndUser(invalidAssignmentId, user))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> assignmentService.getAssignment(user, invalidAssignmentId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(noSuchAssignmentError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(invalidAssignmentId, user);
        }

    }

    @Nested
    @DisplayName("과제 생성")
    class AssignmentCreate {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            AssignmentCreateRequestDto requestDto = createAssignmentCreateRequest();

            DgLecture lecture = createLecture();
            Assignment assignment = createAssignment();

            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(lecture));
            given(assignmentRepository.save(any())).willReturn(assignment);

            // when
            assignmentService.createAssignment(user, requestDto);

            then(dgLectureRepository).should(times(1)).findById(requestDto.getLectureId());
            then(assignmentRepository).should(times(1)).save(any());

        }

        @Test
        @DisplayName("실패 - 존재하지 않은 강의 아이디")
        void whenLectureDoesNotExist_MustThrowNoSuchLectureError() {
            // given
            AssignmentCreateRequestDto requestDto = createAssignmentCreateRequest();
            given(dgLectureRepository.findById(any())).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> assignmentService.createAssignment(user, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchLectureError.getMessage());

            then(dgLectureRepository).should(times(1)).findById(any());
        }

        @Test
        @DisplayName("실패 - 종료시간이 시작시간보다 빠름")
        void whenEndTimeIsBeforeStartTime_MustThrowNotValidAssignmentTimeError() {
            // given
            AssignmentCreateRequestDto requestDto = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                    LocalDateTime.of(2025, 1, 17, 15, 11, 0)
            );
            DgLecture lecture = createLecture();
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(lecture));

            // when & then
            assertThatThrownBy(() -> assignmentService.createAssignment(user, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(dgLectureRepository).should(times(1)).findById(any());
        }

        @Test
        @DisplayName("실패 - 요청에 종료시간만 포함함")
        void whenOnlyEndTimeProvided_MustThrowNotValidAssignmentTimeError() {
            // given
            AssignmentCreateRequestDto requestDto = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    null,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 0)
            );
            DgLecture lecture = createLecture();
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(lecture));

            // when & then
            assertThatThrownBy(() -> assignmentService.createAssignment(user, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(dgLectureRepository).should(times(1)).findById(any());
        }

        @Test
        @DisplayName("실패 - 요청에 시작시간만 포함함")
        void whenOnlyStartTimeProvided_MustThrowNotValidAssignmentTimeError() {
            // given
            AssignmentCreateRequestDto requestDto = new AssignmentCreateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 0),
                    null
            );
            DgLecture lecture = createLecture();
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(lecture));

            // when & then
            assertThatThrownBy(() -> assignmentService.createAssignment(user, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(dgLectureRepository).should(times(1)).findById(any());
        }
    }

    @Nested
    @DisplayName("과제 수정")
    class AssignmentUpdate {
        @Test
        @DisplayName("성공 - 과제 수정")
        void success() {
            // given
            Long assignmentId = 1L;
            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 0),
                    LocalDateTime.of(2025, 1, 17, 15, 11, 15)
            );
            DgLecture lecture = createLecture();

            Assignment assignment = createAssignment();
            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any())).willReturn(Optional.of(assignment));
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(lecture));

            // when
            assignmentService.updateAssignment(user, assignmentId, requestDto);

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(times(1)).findById(any());
            then(notificationService).should(times(1)).rescheduleNotification(any());
        }

        @Test
        @DisplayName("성공 - 강의 변경 없이 과제 수정")
        void whenLectureNotChanged_MustUpdateAssignment() {
            // given
            Long assignmentId = 1L;
            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    null,
                    LocalDateTime.of(2025, 1, 17, 15, 11, 0),
                    LocalDateTime.of(2025, 1, 17, 15, 11, 15)
            );

            Assignment assignment = createAssignment();
            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any())).willReturn(Optional.of(assignment));

            // when
            assignmentService.updateAssignment(user, assignmentId, requestDto);

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(never()).findById(any());
            then(notificationService).should(times(1)).rescheduleNotification(any());
        }

        @Test
        @DisplayName("성공 - 과제가 존재하지 않을 경우 새로 생성됨")
        void whenAssignmentDoesNotExist_ShouldCreateNewAssignment() {
            // given
            Long nonExistentAssignmentId = 999L;
            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "새 과제",
                    "새 과제 설명",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 12, 0, 0),
                    LocalDateTime.of(2025, 1, 17, 13, 0, 0)
            );

            given(assignmentRepository.findByIdAndUser(eq(nonExistentAssignmentId), any())).willReturn(Optional.empty());
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(createLecture()));
            Assignment assignment = createAssignment();
            given(assignmentRepository.save(any())).willReturn(assignment);

            // when
            assignmentService.updateAssignment(user, nonExistentAssignmentId, requestDto);

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(nonExistentAssignmentId), any());
            then(assignmentRepository).should(times(1)).save(any());
            then(notificationService).should(times(1)).scheduleNotification(assignment);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 강의 아이디")
        void whenLectureDoesNotExist_MustThrowNoSuchLectureError() {
            Long invalidLectureId = 999L;

            // given
            Assignment assignment = createAssignment();
            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "제목",
                    "설명", // 존재하지 않는 강의 아이디
                    invalidLectureId,
                    LocalDateTime.of(2025, 1, 17, 12, 0, 0),
                    LocalDateTime.of(2025, 1, 17, 13, 0, 0)
            );

            given(assignmentRepository.findByIdAndUser(any(), any())).willReturn(Optional.of(assignment));
            given(dgLectureRepository.findById(eq(invalidLectureId))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> assignmentService.updateAssignment(user, 1L, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchLectureError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(times(1)).findById(eq(999L));
            then(notificationService).should(never()).rescheduleNotification(any());
        }

        @Test
        @DisplayName("실패 - 종료시간이 시작시간보다 빠름")
        void whenEndTimeIsBeforeStartTime_MustThrowNotValidAssignmentTimeError() {
            // given
            Long assignmentId = 1L;
            Assignment assignment = createAssignment();

            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "제목",
                    "설명",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 0, 0),
                    LocalDateTime.of(2025, 1, 17, 14, 0, 0)
            );

            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any())).willReturn(Optional.of(assignment));
            given(dgLectureRepository.findById(any())).willReturn(Optional.ofNullable(createLecture()));

            // when & then
            assertThatThrownBy(() -> assignmentService.updateAssignment(user, assignmentId, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(times(1)).findById(any());
            then(notificationService).should(never()).rescheduleNotification(any());
        }

        @Test
        @DisplayName("실패 - 시작시간만 포함된 경우")
        void whenOnlyStartTimeProvided_MustThrowNotValidAssignmentTimeError() {
            // given
            Assignment assignment = createAssignment();

            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "제목",
                    "설명",
                    1L,
                    LocalDateTime.of(2025, 1, 17, 15, 0, 0), // 시작시간만 있음
                    null
            );

            given(assignmentRepository.findByIdAndUser(any(), any())).willReturn(Optional.of(assignment));
            given(dgLectureRepository.findById(any())).willReturn(Optional.ofNullable(createLecture()));

            // when & then
            assertThatThrownBy(() -> assignmentService.updateAssignment(user, 1L, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(times(1)).findById(any());
            then(notificationService).should(never()).rescheduleNotification(any());
        }

        @Test
        @DisplayName("실패 - 종료시간만 포함된 경우")
        void whenOnlyEndTimeProvided_MustThrowNotValidAssignmentTimeError() {
            // given
            Long assignmentId = 1L;
            Assignment assignment = createAssignment();

            AssignmentUpdateRequestDto requestDto = new AssignmentUpdateRequestDto(
                    "제목",
                    "설명",
                    1L,
                    null, // 시작시간 없음
                    LocalDateTime.of(2025, 1, 17, 15, 0, 0) // 종료시간만 있음
            );

            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any())).willReturn(Optional.of(assignment));
            given(dgLectureRepository.findById(requestDto.getLectureId())).willReturn(Optional.ofNullable(createLecture()));


            // when & then
            assertThatThrownBy(() -> assignmentService.updateAssignment(user, assignmentId, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAssignmentTimeError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(any(), any());
            then(dgLectureRepository).should(times(1)).findById(any());
            then(notificationService).should(never()).rescheduleNotification(any());
        }

    }

    @Nested
    @DisplayName("과제 삭제")
    class AssignmentDelete {

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long assignmentId = 1L;
            Assignment assignment = createAssignment();
            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any()))
                    .willReturn(Optional.of(assignment));

            // when
            assignmentService.deleteAssignment(user, assignmentId);

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(assignmentId), any());
            then(assignmentRepository).should(times(1)).delete(assignment);
            then(notificationService).should(times(1)).cancelNotification(assignment);
        }

        @Test
        @DisplayName("성공 - 종료시간이 없는 과제 삭제 시 알림 취소되지 않음")
        void whenAssignmentHasNoEndTime_ShouldNotCancelNotification() {
            // given
            Long assignmentId = 2L;
            Assignment assignment = Assignment.of(
                    2L,
                    "알고리즘 과제",
                    "알고리즘 1장 풀기",
                    createLecture(),
                    null,
                    null,
                    false
            ); // 종료시간 없는 과제

            given(assignmentRepository.findByIdAndUser(eq(assignmentId), any()))
                    .willReturn(Optional.of(assignment));

            // when
            assignmentService.deleteAssignment(user, assignmentId);

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(assignmentId), any());
            then(assignmentRepository).should(times(1)).delete(assignment);
            then(notificationService).should(never()).cancelNotification(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 과제 아이디")
        void whenAssignmentDoesNotExist_MustThrowNoSuchAssignmentError() {
            // given
            Long invalidAssignmentId = 999L;
            given(assignmentRepository.findByIdAndUser(eq(invalidAssignmentId), any()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> assignmentService.deleteAssignment(user, invalidAssignmentId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchAssignmentError.getMessage());

            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(invalidAssignmentId), any());
            then(assignmentRepository).should(never()).delete(any());
            then(notificationService).should(never()).cancelNotification(any());
        }
    }

    @Nested
    @DisplayName("완료한 모든 과제 삭제")
    class AllCompletedAssignmentDelete {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(assignmentRepository.findAllByUserAndIsCompletedTrue(eq(user))).willReturn(List.of(createAssignment()));

            // when
            assignmentService.deleteCompletedAssignment(user);

            // then
            then(assignmentRepository).should(times(1)).findAllByUserAndIsCompletedTrue(eq(user));
        }
    }

    @Nested
    @DisplayName("과제 완료 여부 토글")
    class AssignmentToggle {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Assignment assignment = createAssignment();
            given(assignmentRepository.findByIdAndUser(eq(assignment.getId()),  eq(user))).willReturn(Optional.of(assignment));

            // when
            assignmentService.toggleAssignmentComplete(user, assignment.getId());

            // then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(assignment.getId()), eq(user));
            then(notificationService).should(times(1)).cancelNotification(assignment);
        }

        @Test
        @DisplayName("실패 - 과제가 존재하지 않을 경우")
        void whenAssigmentDoesNotExist_MustThrowException() {
            // given
            Long invalidAssignmentId = 999L;
            given(assignmentRepository.findByIdAndUser(eq(invalidAssignmentId), eq(user))).willReturn(Optional.empty());

            assertThatThrownBy(() -> assignmentService.toggleAssignmentComplete(user, invalidAssignmentId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchAssignmentError.getMessage());

            // when & then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(invalidAssignmentId), eq(user));
            then(notificationService).should(never()).cancelNotification(any());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 유저가 접근한 경우")
        void whenUserTriesToAccessInvalidAssignment_MustThrowException() {
            // given
            Long assignmentId = 1L;
            User invalidUser = createUser();
            given(assignmentRepository.findByIdAndUser(eq(assignmentId), eq(invalidUser))).willReturn(Optional.empty());

            assertThatThrownBy(() -> assignmentService.toggleAssignmentComplete(invalidUser, assignmentId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchAssignmentError.getMessage());

            // when & then
            then(assignmentRepository).should(times(1)).findByIdAndUser(eq(assignmentId), eq(invalidUser));
            then(notificationService).should(never()).cancelNotification(any());
        }
    }


    public AssignmentCreateRequestDto createAssignmentCreateRequest() {
        return new AssignmentCreateRequestDto(
                "알고리즘 과제",
                "알고리즘 1장 풀기",
                1L,
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 17, 15, 12, 0)
        );
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
                .year(2025)
                .semester(1)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }

    public List<Assignment> createAssignmentList() {
        Assignment assignment1 = Assignment.of(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        Assignment assignment2 = Assignment.of(
                2L, "자료구조 과제", "자료구조 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );

        return List.of(assignment1, assignment2);
    }

    public Assignment createAssignment() {
        return Assignment.of(
                1L, "알고리즘 과제", "알고리즘 1장 풀기",
                createLecture(),
                LocalDateTime.of(2025, 1, 17, 15, 11, 58),
                LocalDateTime.of(2025, 1, 24, 23, 59, 59),
                false
        );
    }

    public User createUser() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        return User.from(oAuthUserInfoDto);
    }
}