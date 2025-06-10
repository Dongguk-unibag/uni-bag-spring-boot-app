package org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenReIssueDto {
    private String message;
    private String accessToken;

    public static TokenReIssueDto of(String reIssuedAccessToken) {
        return TokenReIssueDto.builder()
                .message("토큰이 재발행되었습니다")
                .accessToken(reIssuedAccessToken)
                .build();
    }
}
