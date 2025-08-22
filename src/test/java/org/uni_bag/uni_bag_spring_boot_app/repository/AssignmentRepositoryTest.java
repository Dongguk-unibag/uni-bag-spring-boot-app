package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AssignmentRepositoryTest {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("유저가 작성한 특정 과제를 조회한다")
    void testFindByIdAndUser() {
        // given
        User user = entityManager.persist(createUser());
        Assignment assignment = entityManager.persist(createAssignment(user));

        // when
        var result = assignmentRepository.findByIdAndUser(assignment.getId(), user);

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting(Assignment::getUser)
                .isEqualTo(user);
    }

    @Test
    @DisplayName("유저가 작성한 과제들을 조회한다")
    void testFindByUser() {
        //given
        User user = entityManager.persist(createUser());
        Assignment assignment1 = entityManager.persist(createAssignment(user));
        Assignment assignment2 = entityManager.persist(createAssignment(user));
        Assignment assignment3 = entityManager.persist(createAssignment(user));

        // when
        List<Assignment> result = assignmentRepository.findAllByUser(user);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(Assignment::getId)
                .containsExactlyInAnyOrder(
                        assignment1.getId(),
                        assignment2.getId(),
                        assignment3.getId()
                );
    }


    @Test
    @DisplayName("과제 상태가 완료인 모든 과제들을 조회한다")
    void testFindAllByUserAndIsCompletedTrue() {
        // given
        User user = entityManager.persist(createUser());
        Assignment assignment1 = entityManager.persist(createAssignment(user, true));
        Assignment assignment2 = entityManager.persist(createAssignment(user, true));
        entityManager.persist(createAssignment(user, false));

        // when
        List<Assignment> result = assignmentRepository.findAllByUser(user);

        // then
        assertThat(result).contains(assignment1, assignment2);
    }

    @Test
    @DisplayName("마감 시간이 1시간 뒤 또는 내일 오전 9시 이후인 과제 조회한다")
    void testFindAssignmentsAfterOneHour() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        LocalDateTime tomorrow9AM = now.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

        User user = entityManager.persist(createUser());
        // 조건을 만족하는 과제
        Assignment assignment1 = entityManager.persist(createAssignment(user, now, oneHourLater, false));
        Assignment assignment2 = entityManager.persist(createAssignment(user, now, tomorrow9AM, false));

        // 조건을 만족하지 않는 과제
        entityManager.persist(createAssignment(user, now, now.plusMinutes(30), false));
        entityManager.persist(createAssignment(user, now, oneHourLater, true));
        entityManager.persist(createAssignment(user, now, tomorrow9AM, true));


        // when
        List<Assignment> result = assignmentRepository.findAssignmentsAfterOneHour(oneHourLater, tomorrow9AM);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(assignment1, assignment2);
    }


    public Assignment createAssignment(User user) {
        return Assignment.builder()
                .title("알고리즘 과제")
                .description("알고리즘 1장 풀기")
                .user(user)
                .lecture(createLecture())
                .startDateTime(LocalDateTime.of(2025, 1, 17, 15, 11, 58))
                .endDateTime(LocalDateTime.of(2025, 1, 24, 23, 59, 59))
                .isCompleted(false)
                .build();
    }

    public Assignment createAssignment(User user, boolean isCompleted) {
        return Assignment.builder()
                .title("알고리즘 과제")
                .description("알고리즘 1장 풀기")
                .user(user)
                .lecture(createLecture())
                .startDateTime(LocalDateTime.of(2025, 1, 17, 15, 11, 58))
                .endDateTime(LocalDateTime.of(2025, 1, 24, 23, 59, 59))
                .isCompleted(isCompleted)
                .build();
    }

    public Assignment createAssignment(User user, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isCompleted) {
        return Assignment.builder()
                .title("알고리즘 과제")
                .description("알고리즘 1장 풀기")
                .user(user)
                .lecture(createLecture())
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .isCompleted(isCompleted)
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
                .year(2025)
                .semester(1)
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }

    public User createUser() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        return User.from(oAuthUserInfoDto);
    }
}