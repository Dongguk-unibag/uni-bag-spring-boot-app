package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewNdrimsLectureSchedule {
    @NotNull(message = "학수번호 필요합니다.")
    @Schema(example = "COM40122", description = "학수번호")
    private String sbjNo;

    @NotNull(message = "강의를 표시할 색상 정보가 필요합니다.")
    @Schema(example = "#ffffff", description = "강의를 구분할 색상 코드")
    private String lectureColor;
}
