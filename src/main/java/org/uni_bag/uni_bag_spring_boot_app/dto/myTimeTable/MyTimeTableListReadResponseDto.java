package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

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
    private Long timeTableId;
    private int year;
    private int semester;
    private int order;

    public static TimeTableInfo of(TimeTable timeTable){
        return TimeTableInfo.builder()
                .timeTableId(timeTable.getId())
                .year(timeTable.getYear())
                .semester(timeTable.getSemester())
                .order(timeTable.getTableOrder())
                .build();
    }
}
