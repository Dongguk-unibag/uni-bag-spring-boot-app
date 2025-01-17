package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentReadResponseDto {
    private Long assignmentId;

    private String title;

    private String description;

    private Long lectureId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public static AssignmentReadResponseDto fromEntity(Assignment assignment){
        return AssignmentReadResponseDto.builder()
                .assignmentId(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .lectureId(assignment.getLecture() == null ? null : assignment.getLecture().getId())
                .startDateTime(assignment.getStartDateTime())
                .endDateTime(assignment.getEndDateTime())
                .build();
    }
}