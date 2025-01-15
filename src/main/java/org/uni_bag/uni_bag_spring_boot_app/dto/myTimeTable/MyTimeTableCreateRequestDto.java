package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTimeTableCreateRequestDto {
    @NotNull(message = "년도가 필요합니다.")
    @Min(value = 2020, message = "년도는 2020년 이상이어야 합니다.")
    @Max(value = 2025, message = "년도는 2025년 이하이어야 합니다.")
    private int year;

    @NotNull(message = "년도와 학기가 필요합니다.")
    @Min(value = 1, message = "학기는 1이상 이어야 합니다.")
    @Max(value = 4, message = "학기는 4이하 이어야 합니다.")
    private int semester;
}
