package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTimeTableOrderUpdateRequestDto {
    @Schema(example = "1", description = "시간표 아이디")
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    @Schema(example = "1", description = "시간표 순위(1: Primary 시간표, 2: Secondary 시간표, 0: 순위 없음)")
    @NotNull(message = "시간표의 순위가 필요합니다.")
    @Min(value = 1, message = "시간표 순위는 1이상 이어야 합니다.")
    @Max(value = 2, message = "시간표 순위는 2이하 이어야 합니다.")
    private int order;
}
