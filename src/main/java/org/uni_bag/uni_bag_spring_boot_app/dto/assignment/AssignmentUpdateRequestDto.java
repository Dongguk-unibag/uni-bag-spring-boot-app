package org.uni_bag.uni_bag_spring_boot_app.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentUpdateRequestDto {
    @NotBlank(message = "과제 제목을 입력해주세요")
    @Size(min = 1, max = 20, message = "과제 제목을 1글자 이상 20글자 이하 이어야 합니다.")
    private String title;

    @Size(min = 1, max = 500, message = "과제 제목을 1글자 이상 500글자 이하 이어야 합니다.")
    private String description;

    private Long lectureId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;
}