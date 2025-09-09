package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyTimeTableCreateRequestDto {
    @Schema(example = "2024", description = "시간표 년도")
    @NotNull(message = "년도가 필요합니다.")
    @Min(value = 2020, message = "년도는 2020년 이상이어야 합니다.")
    @Max(value = 2025, message = "년도는 2025년 이하이어야 합니다.")
    private int year;

    @Schema(example = "1", description = "시간표 학기(1: 봄 학기, 2: 여름 계절학기, 3: 가을 학기, 4: 겨울 계절학기)")
    @NotNull(message = "년도와 학기가 필요합니다.")
    @Min(value = 1, message = "학기는 1이상 이어야 합니다.")
    @Max(value = 4, message = "학기는 4이하 이어야 합니다.")
    private int semester;
}
