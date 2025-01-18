package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentUpdateRequestDto {
    @Schema(example = "알고리즘 과제", description = "과제 제목")
    @NotBlank(message = "과제 제목을 입력해주세요")
    @Size(min = 1, max = 20, message = "과제 제목을 1글자 이상 20글자 이하 이어야 합니다.")
    private String title;

    @Schema(example = "알고리즘 1장 풀기", description = "과제 내용")
    @Size(min = 1, max = 500, message = "과제 제목을 1글자 이상 500글자 이하 이어야 합니다.")
    private String description;

    @Schema(example = "1", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 시작일")
    private LocalDateTime startDateTime;

    @Schema(example = "2025-01-17T15:11:58.340Z", description = "과제 종료일")
    private LocalDateTime endDateTime;
}