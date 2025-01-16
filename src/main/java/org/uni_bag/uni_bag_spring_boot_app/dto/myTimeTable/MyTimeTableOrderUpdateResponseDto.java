package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableOrderUpdateResponseDto {
    private Long timeTableId;
    private int order;

    public static MyTimeTableOrderUpdateResponseDto fromEntity(TimeTable timeTable){
        return MyTimeTableOrderUpdateResponseDto.builder()
                .timeTableId(timeTable.getId())
                .order(timeTable.getTableOrder())
                .build();
    }
}
