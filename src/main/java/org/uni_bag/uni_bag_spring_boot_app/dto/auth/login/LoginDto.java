package org.uni_bag.uni_bag_spring_boot_app.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginDto {
    private LoginToken token;

    public static LoginDto of(String accessToken, String refreshToken) {
        return LoginDto.builder()
                .token(LoginToken.of(accessToken, refreshToken))
                .build();
    }
}
