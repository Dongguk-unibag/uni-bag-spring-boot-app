package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentCreateResponseDto {
    @Schema(example = "알고리즘 과제", description = "과제 제목")
    private String title;

    @Schema(example = "알고리즘 1장 풀기", description = "과제 내용")
    private String description;

    @Schema(example = "1", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 시작일")
    private LocalDateTime startDateTime;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 종료일")
    private LocalDateTime endDateTime;

    public static AssignmentCreateResponseDto fromEntity(Assignment assignment){
        return AssignmentCreateResponseDto.builder()
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .lectureId(assignment.getLecture() == null ? null : assignment.getLecture().getId())
                .startDateTime(assignment.getStartDateTime())
                .endDateTime(assignment.getEndDateTime())
                .build();
    }
}