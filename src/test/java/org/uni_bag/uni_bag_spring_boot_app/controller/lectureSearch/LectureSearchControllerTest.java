package org.uni_bag.uni_bag_spring_boot_app.controller.lectureSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch.LectureSearchResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.lectureSearch.LectureSearchService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LectureSearchController.class)
class LectureSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureSearchService lectureSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("강의 검색")
    class LectureSearchTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            int year = 2025;
            int semester = 2;
            DgLecture lecture1 = createLecture(1L, year, semester, "알고리즘");
            DgLecture lecture2 = createLecture(2L, year, semester, "자료구조");
            LectureSearchResponseDto response = LectureSearchResponseDto.from(List.of(lecture1, lecture2));


            given(lectureSearchService.searchLecture(anyLong(), anyInt(), anyInt(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString()))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/lecture/search/{year}/{semester}", year, semester)
                            .param("cursorId", "1")
                            .param("oc", "불교문화대학")
                            .param("od", "불교학부")
                            .param("om", "불교학전공")
                            .param("grade", "1")
                            .param("professor", "홍길동")
                            .param("lectureName", "대승불교개론"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lectures.length()").value(2))
                    .andExpect(jsonPath("$.lectures[0].lecture.lectureId").value(1))
                    .andExpect(jsonPath("$.lectures[0].lecture.courseName").value("알고리즘"))
                    .andExpect(jsonPath("$.lectures[1].lecture.lectureId").value(2))
                    .andExpect(jsonPath("$.lectures[1].lecture.courseName").value("자료구조"))
            ;
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