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
public class AssignmentDeleteResponseDto {
    @Schema(example = "1", description = "과제 아이디")
    private Long assignmentId;

    public static AssignmentDeleteResponseDto fromEntity(Assignment assignment){
        return AssignmentDeleteResponseDto.builder()
                .assignmentId(assignment.getId())
                .build();
    }
}