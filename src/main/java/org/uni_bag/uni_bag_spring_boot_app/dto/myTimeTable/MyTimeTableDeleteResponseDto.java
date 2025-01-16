package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableDeleteResponseDto {
    private Long timeTableId;
    private String message;

    public static MyTimeTableDeleteResponseDto of(Long timeTableId) {
        return MyTimeTableDeleteResponseDto.builder()
                .timeTableId(timeTableId)
                .message("성공적으로 시간표를 삭제하였습니다.")
                .build();
    }
}
