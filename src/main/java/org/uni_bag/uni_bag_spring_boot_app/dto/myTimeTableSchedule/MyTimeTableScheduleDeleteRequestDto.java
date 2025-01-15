package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyTimeTableScheduleDeleteRequestDto {
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    @NotNull(message = "강의 아이디가 필요합니다.")
    private Long lectureId;
}
