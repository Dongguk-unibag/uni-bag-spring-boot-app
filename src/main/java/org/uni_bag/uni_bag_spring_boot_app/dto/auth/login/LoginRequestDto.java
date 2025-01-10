package org.uni_bag.uni_bag_spring_boot_app.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    @Schema(example = "Kakao", description = "토큰 발행 기관")
    @NotNull(message = "소셜 계정의 타입이 필요합니다.")
    private SnsType snsType;

    @Schema(example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIy...", description = "토큰 발행 기관에서 받은 AccessToken")
    @NotNull(message = "accessToken이 필요합니다.")
    private String accessToken;
}
