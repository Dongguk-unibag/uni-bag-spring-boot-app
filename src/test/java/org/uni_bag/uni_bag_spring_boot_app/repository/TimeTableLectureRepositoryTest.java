package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TimeTableLectureRepositoryTest {
    @Autowired
    private TimeTableLectureRepository timeTableLectureRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("시간표에 포함된 모든 강의를 조회하면 정상적으로 리스트를 반환한다")
    void findAllByTimeTable_success() {
        // given
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        DgLecture lecture1 = createLecture("자료구조");
        DgLecture lecture2 = createLecture("알고리즘");
        DgLecture lecture3 = createLecture("스프링부트");
        entityManager.persist(lecture1);
        entityManager.persist(lecture2);
        entityManager.persist(lecture3);

        TimeTableLecture timeTableLecture1 = createTimeTableLecture(lecture1, timeTable, "#ffffff");
        TimeTableLecture timeTableLecture2 = createTimeTableLecture(lecture2, timeTable, "#ffff00");
        TimeTableLecture timeTableLecture3 = createTimeTableLecture(lecture3, timeTable, "#ffff11");
        entityManager.persist(timeTableLecture1);
        entityManager.persist(timeTableLecture2);
        entityManager.persist(timeTableLecture3);

        // when
        List<TimeTableLecture> result = timeTableLectureRepository.findAllByTimeTable(timeTable);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(timeTableLecture1, timeTableLecture2, timeTableLecture3);
    }

    @Test
    @DisplayName("시간표에 강의가 없으면 빈 리스트를 반환한다")
    void findAllByTimeTable_empty() {
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        // when
        List<TimeTableLecture> result = timeTableLectureRepository.findAllByTimeTable(timeTable);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("시간표와 강의가 일치하는 시간표 강의를 조회하면 성공한다")
    void findByTimeTableAndLecture_success() {
        // given
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        DgLecture lecture = createLecture("자료구조");
        entityManager.persist(lecture);

        TimeTableLecture timeTableLecture = createTimeTableLecture(lecture, timeTable, "#ffffff");
        entityManager.persist(timeTableLecture);

        // when
        Optional<TimeTableLecture> result = timeTableLectureRepository.findByTimeTableAndLecture(timeTable, lecture);

        // then
        assertThat(result).isPresent().get().isEqualTo(timeTableLecture);
    }

    @Test
    @DisplayName("시간표와 강의가 일치하지 않으면 빈 값을 반환한다")
    void findByTimeTableAndLecture_empty() {
        // given
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        DgLecture lecture = createLecture("자료구조");
        entityManager.persist(lecture);

        // when
        Optional<TimeTableLecture> result = timeTableLectureRepository.findByTimeTableAndLecture(timeTable, lecture);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("시간표에 포함된 모든 강의를 삭제하면 해당 시간표의 강의 목록이 비게 된다")
    void deleteAllByTimeTable_success() {
        // given
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        DgLecture lecture1 = createLecture("자료구조");
        DgLecture lecture2 = createLecture("알고리즘");
        DgLecture lecture3 = createLecture("스프링부트");
        entityManager.persist(lecture1);
        entityManager.persist(lecture2);
        entityManager.persist(lecture3);

        TimeTableLecture timeTableLecture1 = createTimeTableLecture(lecture1, timeTable, "#ffffff");
        TimeTableLecture timeTableLecture2 = createTimeTableLecture(lecture2, timeTable, "#ffff00");
        TimeTableLecture timeTableLecture3 = createTimeTableLecture(lecture3, timeTable, "#ffff11");
        entityManager.persist(timeTableLecture1);
        entityManager.persist(timeTableLecture2);
        entityManager.persist(timeTableLecture3);

        // when
        timeTableLectureRepository.deleteAllByTimeTable(timeTable);

        // then
        assertThat(timeTableLectureRepository.findAllByTimeTable(timeTable)).isEmpty();
    }

    @Test
    @DisplayName("강의가 없는 시간표를 삭제 요청해도 오류 없이 동작한다")
    void deleteAllByTimeTable_empty() {
        // given
        User user = createUser();
        entityManager.persist(user);

        TimeTable timeTable = createTimeTable(user);
        entityManager.persist(timeTable);

        DgLecture lecture1 = createLecture("자료구조");
        DgLecture lecture2 = createLecture("알고리즘");
        DgLecture lecture3 = createLecture("스프링부트");
        entityManager.persist(lecture1);
        entityManager.persist(lecture2);
        entityManager.persist(lecture3);

        TimeTableLecture timeTableLecture1 = createTimeTableLecture(lecture1, timeTable, "#ffffff");
        TimeTableLecture timeTableLecture2 = createTimeTableLecture(lecture2, timeTable, "#ffff00");
        TimeTableLecture timeTableLecture3 = createTimeTableLecture(lecture3, timeTable, "#ffff11");
        entityManager.persist(timeTableLecture1);
        entityManager.persist(timeTableLecture2);
        entityManager.persist(timeTableLecture3);

        // when
        timeTableLectureRepository.deleteAllByTimeTable(timeTable);
    }

    private TimeTable createTimeTable(User user) {
        return TimeTable.builder()
                .academicYear(2025)
                .semester(2)
                .user(user)
                .isPrimary(false)
                .build();
    }

    private User createUser() {
        return User.builder()
                .name("민수")
                .snsId("123")
                .snsType(SnsType.Kakao)
                .studentId(null)
                .build();
    }

    private TimeTableLecture createTimeTableLecture(DgLecture lecture, TimeTable timeTable, String lectureColor) {
        return TimeTableLecture.builder()
                .lecture(lecture)
                .timeTable(timeTable)
                .lectureColor(lectureColor)
                .build();
    }

    private DgLecture createLecture(String courseName) {
        return DgLecture.builder()
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
                .build();
    }
}
