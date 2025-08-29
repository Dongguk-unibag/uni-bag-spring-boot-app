package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableCreateResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long timetableId;

    @Schema(example = "2024", description = "시간표 년도")
    private int year;

    @Schema(example = "1", description = "시간표 학기(1: 봄 학기, 2: 여름 계절학기, 3: 가을 학기, 4: 겨울 계절학기)")
    private int semester;

    @Schema(example = "성공적으로 시간표를 생성하였습니다", description = "성공 메시지")
    private String message;

    public static MyTimeTableCreateResponseDto fromEntity(TimeTable timeTable){
        return MyTimeTableCreateResponseDto.builder()
                .timetableId(timeTable.getId())
                .year(timeTable.getAcademicYear())
                .semester(timeTable.getSemester())
                .message("성공적으로 시간표를 생성하였습니다")
                .build();
    }
}
