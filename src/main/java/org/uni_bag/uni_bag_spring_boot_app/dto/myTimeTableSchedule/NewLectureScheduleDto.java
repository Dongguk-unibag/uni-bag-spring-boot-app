package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTableLecture;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureColor;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewLectureScheduleDto {
    @NotNull(message = "강의 아이디가 필요합니다.")
    @Schema(example = "1", description = "강의 아이디")
    private Long lectureId;

    @NotNull(message = "강의를 표시할 색상 정보가 필요합니다.")
    @Schema(example = "#ffffff", description = "강의를 구분할 색상 코드")
    private String lectureColor;

    public static NewLectureScheduleDto of(LectureColor lectureColor){
        return NewLectureScheduleDto.builder()
                .lectureId(lectureColor.getLecture().getId())
                .lectureColor(lectureColor.getLectureColor())
                .build();
    }
}
