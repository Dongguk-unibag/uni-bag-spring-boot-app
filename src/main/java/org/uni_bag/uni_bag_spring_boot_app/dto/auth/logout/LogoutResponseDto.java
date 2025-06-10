package org.uni_bag.uni_bag_spring_boot_app.dto.auth.logout;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "LogoutResponse")
public class LogoutResponseDto {
    @Schema(example = "로그아웃 되었습니다.")
    private String message;

    public static LogoutResponseDto success() {
        return LogoutResponseDto.builder()
                .message("로그아웃 되었습니다.")
                .build();
    }
}
