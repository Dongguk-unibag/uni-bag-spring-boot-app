package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableScheduleCreateResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    private List<NewLecture> lectures;

    public static MyTimeTableScheduleCreateResponseDto fromEntity(TimeTable timeTable, List<DgLecture> lectures){
        return MyTimeTableScheduleCreateResponseDto.builder()
                .timeTableId(timeTable.getId())
                .lectures(lectures.stream().map(dgLecture -> NewLecture.of(dgLecture.getId(), dgLecture.getCourseName())).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class NewLecture {
    @Schema(example = "1", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "강의 이름")
    private String lectureName;

    public static NewLecture of(Long lectureId, String lectureName){
        return NewLecture.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .build();
    }
}
