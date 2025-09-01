package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class TimeTableRepositoryTest {
    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("사용자, 학년도, 학기에 해당하는 시간표가 존재하면 조회에 성공한다")
    void findByUserAndAcademicYearAndSemester_success() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(2025, 2, user, true);
        entityManager.persist(timeTable);

        // when
        Optional<TimeTable> result = timeTableRepository.findByUserAndAcademicYearAndSemester(user, 2025, 2);

        // then
        assertThat(result).isPresent().get().isEqualTo(timeTable);
    }

    @Test
    @DisplayName("사용자, 학년도, 학기에 해당하는 시간표가 존재하지 않으면 빈 값을 반환한다")
    void findByUserAndAcademicYearAndSemester_empty() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        // when
        Optional<TimeTable> result = timeTableRepository.findByUserAndAcademicYearAndSemester(user, 2025, 2);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ID와 사용자에 해당하는 시간표가 존재하면 조회에 성공한다")
    void findByIdAndUser_success_whenTimeTableBelongsToUser() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(2025, 2, user, true);
        entityManager.persist(timeTable);

        // when
        Optional<TimeTable> result = timeTableRepository.findByIdAndUser(timeTable.getId(), user);

        // then
        assertThat(result).isPresent().get().isEqualTo(timeTable);
    }

    @Test
    @DisplayName("ID는 존재하지만 다른 사용자의 시간표라면 빈 값을 반환한다")
    void findByIdAndUser_empty_whenNotBelongsToUser() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        entityManager.persist(user1);
        entityManager.persist(user2);

        TimeTable timeTable = createTimeTable(2025, 2, user1, true);
        entityManager.persist(timeTable);

        // when
        Optional<TimeTable> result = timeTableRepository.findByIdAndUser(timeTable.getId(), user2);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ID에 해당하는 시간표가 존재하지 않으면 빈 값을 반환한다")
    void findByIdAndUser_empty_whenNotExists() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(2025, 2, user, true);
        entityManager.persist(timeTable);

        // when
        Long notExistId = timeTable.getId() + 1;
        Optional<TimeTable> result = timeTableRepository.findByIdAndUser(notExistId, user);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자의 모든 시간표를 조회하면 정상적으로 리스트를 반환한다")
    void findAllByUser_success() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable1 = createTimeTable(2024, 1, user, true);
        TimeTable timeTable2 = createTimeTable(2025, 1, user, true);
        TimeTable timeTable3 = createTimeTable(2025, 2, user, true);
        entityManager.persist(timeTable1);
        entityManager.persist(timeTable2);
        entityManager.persist(timeTable3);

        // when
        List<TimeTable> result = timeTableRepository.findAllByUser(user);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(timeTable1, timeTable2, timeTable3);
    }

    @Test
    @DisplayName("사용자의 시간표가 하나도 없으면 빈 리스트를 반환한다")
    void findAllByUser_empty() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        // when
        List<TimeTable> result = timeTableRepository.findAllByUser(user);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자의 기본 시간표가 존재하면 조회에 성공한다")
    void findByUserAndIsPrimary_success() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable1 = createTimeTable(2025, 2, user, true);
        TimeTable timeTable2 = createTimeTable(2025, 2, user, false);
        entityManager.persist(timeTable1);
        entityManager.persist(timeTable2);

        // when
        Optional<TimeTable> result = timeTableRepository.findByUserAndIsPrimary(user, true);

        // then
        assertThat(result).isPresent().get().isEqualTo(timeTable1);
    }

    @Test
    @DisplayName("사용자의 기본 시간표가 존재하지 않으면 빈 값을 반환한다")
    void findByUserAndIsPrimary_empty() {
        // given
        User user = createUser("민수", "123");
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(2025, 2, user, false);
        entityManager.persist(timeTable);

        // when
        Optional<TimeTable> result = timeTableRepository.findByUserAndIsPrimary(user, true);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("기본 시간표 목록이 존재하면 조회에 성공한다")
    void findAllByIsPrimary_success() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        User user3 = createUser("광래", "345");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        TimeTable timeTable1 = createTimeTable(2025, 2, user1, true);
        TimeTable timeTable2 = createTimeTable(2025, 2, user2, true);
        TimeTable timeTable3 = createTimeTable(2025, 1, user1, false);
        TimeTable timeTable4 = createTimeTable(2025, 1, user3, true);
        TimeTable timeTable5 = createTimeTable(2025, 2, user3, false);
        entityManager.persist(timeTable1);
        entityManager.persist(timeTable2);
        entityManager.persist(timeTable3);
        entityManager.persist(timeTable4);
        entityManager.persist(timeTable5);

        // when
        List<TimeTable> result = timeTableRepository.findAllByIsPrimary(true);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(timeTable1, timeTable2, timeTable4);
    }

    @Test
    @DisplayName("기본 시간표가 하나도 없으면 빈 리스트를 반환한다")
    void findAllByIsPrimary_empty() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        User user3 = createUser("광래", "345");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        TimeTable timeTable1 = createTimeTable(2025, 2, user1, false);
        TimeTable timeTable2 = createTimeTable(2025, 2, user2, false);
        TimeTable timeTable3 = createTimeTable(2025, 2, user3, false);
        entityManager.persist(timeTable1);
        entityManager.persist(timeTable2);
        entityManager.persist(timeTable3);

        // when
        List<TimeTable> result = timeTableRepository.findAllByIsPrimary(true);

        // then
        assertThat(result).isEmpty();
    }

    private TimeTable createTimeTable(int year, int semester, User user, boolean isPrimary) {
        return TimeTable.builder()
                .academicYear(year)
                .semester(semester)
                .user(user)
                .isPrimary(isPrimary)
                .build();
    }

    private User createUser(String name, String snsId) {
        return User.builder()
                .name(name)
                .snsId(snsId)
                .snsType(SnsType.Kakao)
                .studentId(null)
                .build();
    }
}