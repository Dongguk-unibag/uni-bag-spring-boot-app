package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTableLecture;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class MyEnrolledLectureReadResponseDto {
    private List<MyEnrolledLecture> enrolledLectures;

    public static MyEnrolledLectureReadResponseDto of(List<TimeTableLecture> timeTableLectures){
        return MyEnrolledLectureReadResponseDto.builder()
                .enrolledLectures(timeTableLectures.stream()
                        .map(MyEnrolledLecture::of).toList()
                ).build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class MyEnrolledLecture {
    @Schema(example = "1", description = "수강중인 강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "수강중인 강의 이름")
    private String lectureName;

    @Schema(example = "#ffffff", description = "강의 색상 코드")
    private String lectureColor;

    public static MyEnrolledLecture of(TimeTableLecture timeTableLecture){
        DgLecture lecture = timeTableLecture.getLecture();

        return MyEnrolledLecture.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getCourseName())
                .lectureColor(timeTableLecture.getLectureColor())
                .build();
    }
}