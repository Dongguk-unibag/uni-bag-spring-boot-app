package org.uni_bag.uni_bag_spring_boot_app.service.lectureSearch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch.LectureSearchResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LectureSearchServiceTest {
    @InjectMocks
    private LectureSearchService lectureSearchService;

    @Mock
    private DgLectureRepository dgLectureRepository;


    @Nested
    @DisplayName("강의 검색")
    class LectureSearchTest{
        @Test
        @DisplayName("성공")
        void success() {
            // given
            int year = 2025;
            int semester = 2;
            DgLecture lecture1 = createLecture(1L, year, semester, "알고리즘");
            DgLecture lecture2 = createLecture(2L, year, semester, "자료구조");
            List<DgLecture> lectures = List.of(lecture1, lecture2);

            given(dgLectureRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(new PageImpl<>(lectures));

            // when
            LectureSearchResponseDto result = lectureSearchService.searchLecture(1L, year, semester, "불교문화대학", "불교학부", "불교학전공", 1, "홍길동", "대승불교개론");

            // then
            assertThat(result.getLectures()).hasSize(2);
        }
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
                .dgLectureTimes(List.of()) // 필요시 DgLectureTime 객체 추가 가능
                .build();
    }
}