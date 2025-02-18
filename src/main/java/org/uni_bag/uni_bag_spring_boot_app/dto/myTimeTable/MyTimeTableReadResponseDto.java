package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.dto.LectureDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.TimeTableInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableReadResponseDto {
    private TimeTableInfoDto timeTableInfo;
    List<LectureDto> lectures;

    public static MyTimeTableReadResponseDto of(TimeTable timeTable, Map<DgLecture, LectureTimeColor> lecturesTimeMap) {
        return MyTimeTableReadResponseDto.builder()
                .timeTableInfo(TimeTableInfoDto.from(timeTable))
                .lectures(lecturesTimeMap.entrySet().stream()
                        .map(dgLectureListEntry -> LectureDto.of(dgLectureListEntry.getKey(), dgLectureListEntry.getValue()))
                        .toList()
                )
                .build();
    }
}
