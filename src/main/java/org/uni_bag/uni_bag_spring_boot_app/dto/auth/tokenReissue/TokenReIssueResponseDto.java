package org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "TokenReIssueResponse")
public class TokenReIssueResponseDto {
    @Schema(example = "토큰이 재발행되었습니다")
    private String message;
    @Schema(description = "accessToken", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIy...")
    private String accessToken;

    public static TokenReIssueResponseDto fromDto(TokenReIssueDto dto) {
        return TokenReIssueResponseDto.builder()
                .message(dto.getMessage())
                .accessToken(dto.getAccessToken())
                .build();
    }
}
