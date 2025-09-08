package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DgLectureTimeRepositoryTest {
    @Autowired
    private DgLectureTimeRepository dgLectureTimeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("특정 강의에 연결된 모든 강의 시간이 존재하면 정상적으로 조회된다")
    void findAllByDgLecture_success() {
        // given
        DgLecture lecture1 = createLecture("CS101");
        DgLecture lecture2 = createLecture("CS102");
        entityManager.persist(lecture1);
        entityManager.persist(lecture2);

        DgLectureTime lectureTime1 = createTimeTableLectureTime(lecture1, "월", Time.valueOf(LocalTime.of(12, 0)), Time.valueOf(LocalTime.of(13, 0)));
        DgLectureTime lectureTime2 = createTimeTableLectureTime(lecture1, "화", Time.valueOf(LocalTime.of(9, 0)), Time.valueOf(LocalTime.of(11, 0)));
        DgLectureTime lectureTime3 = createTimeTableLectureTime(lecture2, "월", Time.valueOf(LocalTime.of(9, 30)), Time.valueOf(LocalTime.of(12, 0)));
        entityManager.persist(lectureTime1);
        entityManager.persist(lectureTime2);
        entityManager.persist(lectureTime3);

        // when
        List<DgLectureTime> result = dgLectureTimeRepository.findAllByDgLecture(lecture1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(lectureTime1, lectureTime2);
    }

    @Test
    @DisplayName("특정 강의에 연결된 강의 시간이 없으면 빈 리스트를 반환한다")
    void findAllByDgLecture_empty() {
        // given
        DgLecture lecture1 = createLecture("CS101");
        DgLecture lecture2 = createLecture("CS102");
        entityManager.persist(lecture1);
        entityManager.persist(lecture2);

        DgLectureTime lectureTime1 = createTimeTableLectureTime(lecture1, "월", Time.valueOf(LocalTime.of(12, 0)), Time.valueOf(LocalTime.of(13, 0)));
        DgLectureTime lectureTime2 = createTimeTableLectureTime(lecture1, "화", Time.valueOf(LocalTime.of(9, 0)), Time.valueOf(LocalTime.of(11, 0)));
        entityManager.persist(lectureTime1);
        entityManager.persist(lectureTime2);

        // when
        List<DgLectureTime> result = dgLectureTimeRepository.findAllByDgLecture(lecture2);

        // then
        assertThat(result).isEmpty();
    }

    private DgLecture createLecture(String courseCode) {
        return DgLecture.builder()
                .curriculum("컴퓨터공학과 2025")
                .area("전공필수")
                .targetGrade("2학년")
                .courseCode(courseCode)
                .courseName("알고리즘")
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
                .academicYear(2025)
                .semester(2)
                .build();
    }

    private DgLectureTime createTimeTableLectureTime(DgLecture lecture, String dayOfWeek, Time startTime, Time endTime) {
        return DgLectureTime.builder()
                .dgLecture(lecture)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}