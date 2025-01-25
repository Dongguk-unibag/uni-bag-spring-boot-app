package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureColor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class TimeTableLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DgLecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TimeTable timeTable;

    @Column(nullable = false)
    private String lectureColor;

    public static TimeTableLecture of(TimeTable timeTable, LectureColor lectureColor) {
        return TimeTableLecture.builder()
                .timeTable(timeTable)
                .lecture(lectureColor.getLecture())
                .lectureColor(lectureColor.getLectureColor())
                .build();
    }
}
