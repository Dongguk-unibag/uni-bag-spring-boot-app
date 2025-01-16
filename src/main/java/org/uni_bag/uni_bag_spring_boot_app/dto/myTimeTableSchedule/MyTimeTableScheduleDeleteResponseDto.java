package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableScheduleDeleteResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
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
    @Schema(example = "2", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "강의 이름")
    private String lectureName;

    public static Lecture of(Long lectureId, String lectureName) {
        return Lecture.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .build();
    }
}

