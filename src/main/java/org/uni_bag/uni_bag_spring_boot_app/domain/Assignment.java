package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    private DgLecture lecture;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isCompleted = false;

    public void updateAssignment(AssignmentUpdateRequestDto requestDto, DgLecture lecture) {
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.lecture = lecture;
        this.startDateTime = requestDto.getStartDateTime();
        this.endDateTime = requestDto.getEndDateTime();
    }

    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
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
