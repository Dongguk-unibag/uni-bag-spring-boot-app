package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableDeleteResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "성공적으로 시간표를 삭제하였습니다.", description = "성공 메시지")
    private String message;

    public static MyTimeTableDeleteResponseDto of(Long timeTableId) {
        return MyTimeTableDeleteResponseDto.builder()
                .timeTableId(timeTableId)
                .message("성공적으로 시간표를 삭제하였습니다.")
                .build();
    }
}
