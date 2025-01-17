package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AssignmentUpdateResponseDto {
    private String title;

    private String description;

    private Long lectureId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public static AssignmentUpdateResponseDto fromEntity(Assignment assignment){
        return AssignmentUpdateResponseDto.builder()
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .lectureId(assignment.getLecture() == null ? null : assignment.getLecture().getId())
                .startDateTime(assignment.getStartDateTime())
                .endDateTime(assignment.getEndDateTime())
                .build();
    }

    public static AssignmentUpdateResponseDto fromDto(AssignmentCreateResponseDto responseDto){
        return AssignmentUpdateResponseDto.builder()
                .title(responseDto.getTitle())
                .description(responseDto.getDescription())
                .lectureId(responseDto.getLectureId())
                .startDateTime(responseDto.getStartDateTime())
                .endDateTime(responseDto.getEndDateTime())
                .build();
    }
}