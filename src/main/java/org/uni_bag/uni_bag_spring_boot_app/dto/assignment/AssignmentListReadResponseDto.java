package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentListReadResponseDto {
   private List<AssignmentDto> assignments;

   public static AssignmentListReadResponseDto fromEntity(List<Assignment> assignments){
       return AssignmentListReadResponseDto.builder()
               .assignments(assignments.stream().map(AssignmentDto::fromEntity).toList())
               .build();
   }
}

@Getter
@AllArgsConstructor
@Builder
class AssignmentDto {
    @Schema(example = "1", description = "과제 아이디")
    private Long assignmentId;

    @Schema(example = "알고리즘 과제", description = "과제 제목")
    private String title;

    @Schema(example = "알고리즘 1장 풀기", description = "과제 내용")
    private String description;

    private AssignmentLectureDto lecture;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 시작일")
    private LocalDateTime startDateTime;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 종료일")
    private LocalDateTime endDateTime;

    public static AssignmentDto fromEntity(Assignment assignment){
        return AssignmentDto.builder()
                .assignmentId(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .lecture(assignment.getLecture() == null ? null : AssignmentLectureDto.fromEntity(assignment.getLecture()))
                .startDateTime(assignment.getStartDateTime())
                .endDateTime(assignment.getEndDateTime())
                .build();
    }
}