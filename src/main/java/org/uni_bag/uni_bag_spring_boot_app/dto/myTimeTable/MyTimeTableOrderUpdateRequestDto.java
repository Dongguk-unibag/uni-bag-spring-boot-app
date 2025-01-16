package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTimeTableOrderUpdateRequestDto {
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    @NotNull(message = "시간표의 순위가 필요합니다.")
    @Min(value = 1, message = "시간표 순위는 1이상 이어야 합니다.")
    @Max(value = 2, message = "시간표 순위는 2이하 이어야 합니다.")
    private int order;
}
