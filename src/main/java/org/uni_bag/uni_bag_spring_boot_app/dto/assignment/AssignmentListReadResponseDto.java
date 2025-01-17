package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

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
   private List<MyAssignment> assignments;

   public static AssignmentListReadResponseDto fromEntity(List<Assignment> assignments){
       return AssignmentListReadResponseDto.builder()
               .assignments(assignments.stream().map(MyAssignment::fromEntity).toList())
               .build();
   }
}

@Getter
@AllArgsConstructor
@Builder
class MyAssignment {
    private Long assignmentId;

    private String title;

    private String description;

    private Long lectureId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public static MyAssignment fromEntity(Assignment assignment){
        return MyAssignment.builder()
                .assignmentId(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .lectureId(assignment.getLecture() == null ? null : assignment.getLecture().getId())
                .startDateTime(assignment.getStartDateTime())
                .endDateTime(assignment.getEndDateTime())
                .build();
    }
}