package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private TimeTable timeTable;

    public static TimeTableLecture of(TimeTable timeTable, DgLecture lecture){
        return TimeTableLecture.builder()
                .timeTable(timeTable)
                .lecture(lecture)
                .build();
    }
}
