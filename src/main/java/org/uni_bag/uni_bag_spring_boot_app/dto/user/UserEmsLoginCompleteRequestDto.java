package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmsLoginCompleteRequestDto {
    @Schema(example = "홍길동", description = "학생 이름")
    private String name;

    @Schema(example = "2019212111", description = "학생 학번")
    private String studentId;
}
