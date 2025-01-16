package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyTimeTableScheduleCreateRequestDto {
    @Schema(example = "1", description = "시간표 아이디")
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    @Schema(example = "[1, 2, 3]", description = "추가할 강의 아이디")
    private List<Long> lectureIds;
}
