package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.AssignmentCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.AssignmentUpdateRequestDto;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    private DgLecture lecture;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public void updateAssignment(AssignmentUpdateRequestDto requestDto, DgLecture lecture) {
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.lecture = lecture;
        this.startDateTime = requestDto.getStartDateTime();
        this.endDateTime = requestDto.getEndDateTime();
    }

    public static Assignment of(User user, DgLecture lecture, AssignmentCreateRequestDto requestDto) {
        return Assignment.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .user(user)
                .lecture(lecture)
                .startDateTime(requestDto.getStartDateTime())
                .endDateTime(requestDto.getEndDateTime())
                .build();
    }
}
