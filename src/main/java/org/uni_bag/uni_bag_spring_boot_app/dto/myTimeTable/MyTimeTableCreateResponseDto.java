package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableCreateResponseDto {
    private int year;
    private int semester;
    private String message;

    public static MyTimeTableCreateResponseDto fromEntity(TimeTable timeTable){
        return MyTimeTableCreateResponseDto.builder()
                .year(timeTable.getYear())
                .semester(timeTable.getSemester())
                .message("성공적으로 시간표를 생성하였습니다")
                .build();
    }
}
