package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableListReadResponseDto {
    private List<TimeTableInfo> timeTables;

    public static MyTimeTableListReadResponseDto fromEntity(List<TimeTable> timeTables){
        return MyTimeTableListReadResponseDto.builder()
                .timeTables(timeTables.stream().map(TimeTableInfo::of).toList())
                .build();
    }

}

@Getter
@AllArgsConstructor
@Builder
class TimeTableInfo{
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "2024", description = "년도")
    private int year;

    @Schema(example = "3", description = "학기(1: 봄 학기, 2: 여름 계절학기, 3: 가을 학기, 4: 겨울 계절학기)")
    private int semester;

    @Schema(example = "true", description = "primary 시간표 여부")
    private boolean isPrimary;

    public static TimeTableInfo of(TimeTable timeTable){
        return TimeTableInfo.builder()
                .timeTableId(timeTable.getId())
                .year(timeTable.getAcademicYear())
                .semester(timeTable.getSemester())
                .isPrimary(timeTable.isPrimary())
                .build();
    }
}
