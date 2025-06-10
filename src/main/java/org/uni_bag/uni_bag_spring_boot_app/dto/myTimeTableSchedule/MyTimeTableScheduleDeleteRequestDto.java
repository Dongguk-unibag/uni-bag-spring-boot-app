package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyTimeTableScheduleDeleteRequestDto {
    @Schema(example = "1", description = "시간표 아이디")
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    @Schema(example = "2", description = "강의 아이디")
    @NotNull(message = "강의 아이디가 필요합니다.")
    private Long lectureId;
}
