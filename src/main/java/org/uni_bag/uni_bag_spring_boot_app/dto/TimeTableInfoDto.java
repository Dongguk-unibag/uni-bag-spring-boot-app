package org.uni_bag.uni_bag_spring_boot_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class TimeTableInfoDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "2024", description = "년도")
    private int year;

    @Schema(example = "3", description = "학기")
    private int semester;

    @Schema(example = "true", description = "primary 시간표 여부")
    private boolean isPrimary;

    public static TimeTableInfoDto from(TimeTable timeTable) {
        return TimeTableInfoDto.builder()
                .timeTableId(timeTable.getId())
                .year(timeTable.getYear())
                .semester(timeTable.getSemester())
                .isPrimary(timeTable.isPrimary())
                .build();
    }
}
