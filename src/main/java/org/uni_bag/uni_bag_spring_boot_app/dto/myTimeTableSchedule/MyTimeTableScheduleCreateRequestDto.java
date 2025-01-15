package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyTimeTableScheduleCreateRequestDto {
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    private List<Long> lectureIds;
}
