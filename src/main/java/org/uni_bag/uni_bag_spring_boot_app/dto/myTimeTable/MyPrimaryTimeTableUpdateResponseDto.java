package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyPrimaryTimeTableUpdateResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "1", description = "시간표 순위(1: Primary 시간표, 2: Secondary 시간표, 0: 순위 없음)")
    private boolean isPrimary;

    public static MyPrimaryTimeTableUpdateResponseDto fromEntity(TimeTable timeTable){
        return MyPrimaryTimeTableUpdateResponseDto.builder()
                .timeTableId(timeTable.getId())
                .isPrimary(timeTable.isPrimary())
                .build();
    }
}
