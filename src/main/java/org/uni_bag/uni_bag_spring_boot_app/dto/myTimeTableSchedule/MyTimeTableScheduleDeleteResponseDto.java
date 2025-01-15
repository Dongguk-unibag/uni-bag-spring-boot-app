package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableScheduleDeleteResponseDto {
    private Long timeTableId;

    private Lecture lectures;

    public static MyTimeTableScheduleDeleteResponseDto of(TimeTable timeTable, DgLecture dgLecture) {
        return MyTimeTableScheduleDeleteResponseDto.builder()
                .timeTableId(timeTable.getId())
                .lectures(Lecture.of(dgLecture.getId(), dgLecture.getCourseName()))
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class Lecture {
    private Long lectureId;

    private String lectureName;

    public static Lecture of(Long lectureId, String lectureName) {
        return Lecture.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .build();
    }
}

