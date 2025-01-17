package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentDeleteResponseDto {
    private Long assignmentId;

    public static AssignmentDeleteResponseDto fromEntity(Assignment assignment){
        return AssignmentDeleteResponseDto.builder()
                .assignmentId(assignment.getId())
                .build();
    }
}