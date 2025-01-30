package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.dto.LectureDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class MyTimetableGetResponseDto {
    MyTimeTableInfoDto timeTableInfo;
    List<LectureDto> lectures;

    public static MyTimetableGetResponseDto of(TimeTable timeTable, Map<DgLecture, LectureTimeColor> lecturesTimeMap){
        return MyTimetableGetResponseDto.builder()
                .timeTableInfo(MyTimeTableInfoDto.from(timeTable))
                .lectures(lecturesTimeMap.entrySet().stream()
                        .map(dgLectureListEntry -> LectureDto.of(dgLectureListEntry.getKey(), dgLectureListEntry.getValue()))
                        .toList()
                )
                .build();

    }
}

@Getter
@AllArgsConstructor
@Builder
class MyTimeTableInfoDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "2024", description = "년도")
    private int year;

    @Schema(example = "3", description = "학기")
    private int semester;

    @Schema(example = "1", description = "시간표 순위")
    private int tableOrder;

    public static MyTimeTableInfoDto from(TimeTable timeTable){
        return MyTimeTableInfoDto.builder()
                .timeTableId(timeTable.getId())
                .year(timeTable.getYear())
                .semester(timeTable.getSemester())
                .tableOrder(timeTable.getTableOrder())
                .build();
    }
}
