package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentLectureDto {
    @Schema(example = "1", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "강의 이름")
    private String lectureName;

    public static AssignmentLectureDto fromEntity(DgLecture lecture){
        return AssignmentLectureDto.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getCourseName())
                .build();
    }
}
