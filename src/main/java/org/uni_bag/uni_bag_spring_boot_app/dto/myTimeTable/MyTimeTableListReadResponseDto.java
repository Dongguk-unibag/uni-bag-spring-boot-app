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
                .timeTables(timeTables.stream()
                        .map(timeTable -> TimeTableInfo.of(timeTable.getId(), timeTable.getYear(), timeTable.getSemester()))
                        .toList()
                ).build();
    }

}

@Getter
@AllArgsConstructor
@Builder
class TimeTableInfo{
    private Long timeTableId;
    private int year;
    private int semester;

    public static TimeTableInfo of(Long timeTableId, int year, int semester){
        return TimeTableInfo.builder()
                .timeTableId(timeTableId)
                .year(year)
                .semester(semester)
                .build();
    }
}
