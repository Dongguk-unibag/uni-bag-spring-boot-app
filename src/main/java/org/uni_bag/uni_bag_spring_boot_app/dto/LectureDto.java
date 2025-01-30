package org.uni_bag.uni_bag_spring_boot_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class LectureDto {
    @Schema(example = "7", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "강의 이름")
    private String lectureName;

    @Schema(example = "홍길동", description = "교수 이름")
    private String instructorName;

    @Schema(example = "S02", description = "강의실 이름")
    private String classRoom;

    @Schema(example = "일반 강의", description = "강의 형태")
    private String lectureFormat;

    @Schema(example = "#ffffff", description = "강의 색상코드")
    private String lectureColor;

    List<LectureTimeDto> lectureTimes;

    public static LectureDto of(DgLecture lecture, LectureTimeColor lectureTimeColor) {
        return LectureDto.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getCourseName())
                .instructorName(lecture.getInstructor())
                .classRoom(lecture.getClassroom())
                .lectureFormat(lecture.getCourseFormat())
                .lectureColor(lectureTimeColor.getLectureColor())
                .lectureTimes(lectureTimeColor.getDgLectureTime().stream().map(LectureTimeDto::of).toList())
                .build();
    }
}
