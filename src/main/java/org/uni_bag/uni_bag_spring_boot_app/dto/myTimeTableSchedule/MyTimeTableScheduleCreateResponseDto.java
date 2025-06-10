package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureColor;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableScheduleCreateResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    private List<NewLectureScheduleDto> lectures;

    public static MyTimeTableScheduleCreateResponseDto fromEntity(TimeTable timeTable, List<LectureColor> lectures){
        return MyTimeTableScheduleCreateResponseDto.builder()
                .timeTableId(timeTable.getId())
                .lectures(lectures.stream().map(NewLectureScheduleDto::of).toList())
                .build();
    }
}

