package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NdrimsTimeTableScheduleCreateRequestDto {
    @Schema(example = "1", description = "시간표 아이디")
    @NotNull(message = "시간표 아이디가 필요합니다.")
    private Long timeTableId;

    private List<NewNdrimsLectureSchedule> newLectureSchedules;
}