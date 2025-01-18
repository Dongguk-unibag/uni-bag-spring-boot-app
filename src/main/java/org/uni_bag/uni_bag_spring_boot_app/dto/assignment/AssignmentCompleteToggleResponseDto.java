package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentCompleteToggleResponseDto {
    @Schema(example = "1", description = "과제 아이디")
    private Long assignmentId;

    @Schema(example = "true", description = "과제 완료 여부")
    private boolean isCompleted;

    public static AssignmentCompleteToggleResponseDto fromEntity(Assignment assignment){
        return AssignmentCompleteToggleResponseDto.builder()
                .assignmentId(assignment.getId())
                .isCompleted(assignment.isCompleted())
                .build();
    }
}
