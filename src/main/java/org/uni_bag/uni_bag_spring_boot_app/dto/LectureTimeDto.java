package org.uni_bag.uni_bag_spring_boot_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

import java.sql.Time;

@Getter
@AllArgsConstructor
@Builder
public class LectureTimeDto {
    @Schema(example = "목", description = "강의 요일")
    private String dayOfWeek;

    @Schema(example = "13:00:00", description = "강의 시작시간")
    private Time startTime;

    @Schema(example = "17:00:00", description = "강의 종료시간")
    private Time endTime;

    public static LectureTimeDto of(DgLectureTime lectureTime) {
        return LectureTimeDto.builder()
                .dayOfWeek(lectureTime.getDayOfWeek())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}
