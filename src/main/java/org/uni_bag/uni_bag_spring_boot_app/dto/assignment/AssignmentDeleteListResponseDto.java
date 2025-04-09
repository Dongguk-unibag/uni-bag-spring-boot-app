package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentDeleteListResponseDto {

    @Schema(description = "삭제된 과제 리스트")
    private List<AssignmentDeleteResponseDto> deletedAssignment;

    public static AssignmentDeleteListResponseDto fromEntityList(List<Assignment> assignments) {
        return AssignmentDeleteListResponseDto.builder()
                .deletedAssignment(AssignmentDeleteResponseDto.fromEntity(assignments))
                .build();
    }
}
