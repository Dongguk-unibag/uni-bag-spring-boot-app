package org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable;

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
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.FollowRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;
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
class FriendTimeTableServiceTest {
    @InjectMocks
    private FriendTimeTableService friendTimeTableService;

    @Mock
    private TimetableService timetableService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private FollowRepository followRepository;

    @Nested
    @DisplayName("친구 시간표 리스트 조회")
    class FriendTimeTableListGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long followeeId = 2L;

            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

            TimeTable timeTable1 = createTimeTable(1L, 2025, 1, followee);
            TimeTable timeTable2 = createTimeTable(2L, 2025, 2, followee);

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(true);
            given(timeTableRepository.findAllByUser(eq(followee))).willReturn(List.of(timeTable1, timeTable2));

            // when
            friendTimeTableService.getFriendTimeTableList(follower, followeeId);

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timeTableRepository).should(times(1)).findAllByUser(eq(followee));
        }

        @Test
        @DisplayName("실패 - 팔로우 대상 유저가 존재하지 않을 때")
        void whenFolloweeDoesNotExist_MustReturnError() {
            // given
            Long followeeId = 2L;

            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendTimeTableList(follower, followeeId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(never()).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timeTableRepository).should(never()).findAllByUser(eq(followee));

        }

        @Test
        @DisplayName("실패 - 팔로우 관계가 존재하지 않아 접근 권한이 없을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() {
            // given
            Long followeeId = 2L;

            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(false);

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendTimeTableList(follower, followeeId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AccessDeniedError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timeTableRepository).should(never()).findAllByUser(eq(followee));
        }
    }

    @Nested
    @DisplayName("친구 특정 시간표 조회")
    class FriendTimeTableGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long timeTableId = 1L;
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

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

            TimeTable timeTable = createTimeTable(timeTableId, 2025, 1, followee);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            given(timeTableRepository.findById(eq(timeTableId))).willReturn(Optional.of(timeTable));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(true);
            given(timetableService.getTimetableWithLectures(eq(timeTable))).willReturn(lectureTimeMap);

            // when
            friendTimeTableService.getFriendTimeTableById(follower, timeTableId);

            // then
            then(timeTableRepository).should(times(1)).findById(eq(timeTableId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timetableService).should(times(1)).getTimetableWithLectures(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 시간표가 존재하지 않을 때")
        void whenTimeTableDoesNotExist_MustReturnError() {
            // given
            Long timeTableId = 1L;
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            given(timeTableRepository.findById(eq(timeTableId))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendTimeTableById(follower, timeTableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchTimeTableError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findById(eq(timeTableId));
            then(followRepository).should(never()).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timetableService).should(never()).getTimetableWithLectures(any());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우 관계가 존재하지 않아 접근 권한이 없을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() { // given
            Long timeTableId = 1L;
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            TimeTable timeTable = createTimeTable(timeTableId, 2025, 1, followee);

            given(timeTableRepository.findById(eq(timeTableId))).willReturn(Optional.of(timeTable));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(false);

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendTimeTableById(follower, timeTableId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AccessDeniedError.getMessage());

            // then
            then(timeTableRepository).should(times(1)).findById(eq(timeTableId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timetableService).should(never()).getTimetableWithLectures(any());

        }
    }

    @Nested
    @DisplayName("친구 primary 시간표 조회")
    class FriendPrimaryTimeTableGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long followeeId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

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

            TimeTable timeTable = createTimeTable(1L, 2025, 1, followee);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(true);
            given(timeTableRepository.findByUserAndIsPrimary(eq(followee), eq(true))).willReturn(Optional.of(timeTable));
            given(timetableService.getTimetableWithLectures(eq(timeTable))).willReturn(lectureTimeMap);

            // when
            friendTimeTableService.getFriendPrimaryTimeTable(follower, followeeId);

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(followee), eq(true));
            then(timetableService).should(times(1)).getTimetableWithLectures(eq(timeTable));

        }

        @Test
        @DisplayName("실패 - 팔로우 대상 유저가 존재하지 않을 때")
        void whenFolloweeDoesNotExist_MustReturnError() {
            // given
            Long followeeId = 2L;
            User follower = createUser(1L, "민수");

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendPrimaryTimeTable(follower, followeeId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            //  then
            then(userRepository).should(times(1)).findById(eq(followeeId));
        }

        @Test
        @DisplayName("실패 - 팔로우 관계가 존재하지 않을 때")
        void whenFollowRelationshipDoesNotExist_MustReturnError() {
            // given
            Long followeeId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(false);

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendPrimaryTimeTable(follower, followeeId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AccessDeniedError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
        }

        @Test
        @DisplayName("실패 - 기본 시간표가 존재하지 않을 때")
        void whenPrimaryTimeTableDoesNotExist_MustReturnError() {
            //given
            Long followeeId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(followeeId, "희진");

            given(userRepository.findById(eq(followeeId))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(true);
            given(timeTableRepository.findByUserAndIsPrimary(eq(followee), eq(true))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getFriendPrimaryTimeTable(follower, followeeId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoPrimaryTimeTableError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(followeeId));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(followee), eq(true));
        }
    }

    @Nested
    @DisplayName("secondary 친구 시간표 조회")
    class SecondaryFriendTimeTableGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            Follow secondaryFriend = createFollow(1L, follower, followee, true);

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

            TimeTable timeTable = createTimeTable(1L, 2025, 1, followee);

            Map<DgLecture, LectureTimeColor> lectureTimeMap = Map.of(
                    lecture1, new LectureTimeColor(List.of(dgLectureTime1), "#ffffff"),
                    lecture2, new LectureTimeColor(List.of(dgLectureTime2), "#ffff00"),
                    lecture3, new LectureTimeColor(List.of(dgLectureTime3), "#ffff99")
            );

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.of(secondaryFriend));
            given(timeTableRepository.findByUserAndIsPrimary(eq(followee), eq(true))).willReturn(Optional.of(timeTable));
            given(timetableService.getTimetableWithLectures(eq(timeTable))).willReturn(lectureTimeMap);

            // when
            friendTimeTableService.getSecondaryFriendTimeTable(follower);

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(followee), eq(true));
            then(timetableService).should(times(1)).getTimetableWithLectures(eq(timeTable));
        }

        @Test
        @DisplayName("실패 - 세컨더리 친구가 존재하지 않을 때")
        void whenNoSecondaryFriendExists_MustReturnError() {
            // given
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getSecondaryFriendTimeTable(follower))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSecondaryFriendError.getMessage());

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
        }

        @Test
        @DisplayName("실패 - 세컨더리 친구의 기본 시간표가 존재하지 않을 때")
        void whenSecondaryFriendPrimaryTimeTableDoesNotExist_MustReturnError() {
            // given
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            Follow secondaryFriend = createFollow(1L, follower, followee, true);

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.of(secondaryFriend));
            given(timeTableRepository.findByUserAndIsPrimary(eq(followee), eq(true))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> friendTimeTableService.getSecondaryFriendTimeTable(follower))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoPrimaryTimeTableError.getMessage());

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
            then(timeTableRepository).should(times(1)).findByUserAndIsPrimary(eq(followee), eq(true));
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

    private Follow createFollow(Long followId, User follower, User followee, boolean isSecondaryFriend) {
        return new Follow(followId, follower, followee, isSecondaryFriend);
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
                .dgLectureTimes(List.of())
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