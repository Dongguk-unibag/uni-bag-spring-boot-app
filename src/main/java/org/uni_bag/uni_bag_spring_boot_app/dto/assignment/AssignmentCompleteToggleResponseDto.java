package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentCompleteToggleResponseDto {
    private Long assignmentId;
    private boolean isCompleted;

    public static AssignmentCompleteToggleResponseDto fromEntity(Assignment assignment){
        return AssignmentCompleteToggleResponseDto.builder()
                .assignmentId(assignment.getId())
                .isCompleted(assignment.isCompleted())
                .build();
    }
}
