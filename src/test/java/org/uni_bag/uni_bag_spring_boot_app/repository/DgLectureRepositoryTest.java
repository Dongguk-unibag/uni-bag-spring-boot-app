package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DgLectureRepositoryTest {
    @Autowired
    private DgLectureRepository dgLectureRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("과목코드, 학년도, 학기가 일치하는 강의가 존재하면 조회에 성공한다")
    void findByCourseCodeAndAcademicYearAndSemester_success() {
        // given
        DgLecture lecture = createLecture("CS101", 2025, 2);
        entityManager.persist(lecture);

        // when
        Optional<DgLecture> result = dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester("CS101", 2025, 2);

        // then
        assertThat(result).isPresent().get().isEqualTo(lecture);
    }

    @Test
    @DisplayName("과목코드, 학년도, 학기가 일치하는 강의가 존재하지 않으면 빈 값을 반환한다")
    void findByCourseCodeAndAcademicYearAndSemester_empty() {
        // given
        DgLecture lecture = createLecture("CS102", 2025, 2);
        entityManager.persist(lecture);

        // when
        Optional<DgLecture> result = dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester("CS101", 2025, 2);

        // then
        assertThat(result).isEmpty();
    }

    private DgLecture createLecture(String courseCode, int year, int semester) {
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
                .academicYear(year)
                .semester(semester)
                .build();
    }

}