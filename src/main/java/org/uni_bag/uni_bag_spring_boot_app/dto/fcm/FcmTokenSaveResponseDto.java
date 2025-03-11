package org.uni_bag.uni_bag_spring_boot_app.dto.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;

@Getter
@AllArgsConstructor
@Builder
public class FcmTokenSaveResponseDto {
    @Schema(example = "1", description = "회원 아이디")
    private Long userId;
    @Schema(example = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn...", description = "FCM 토큰")
    private String fcmToken;

    public static FcmTokenSaveResponseDto of(Long userId, String fcmToken){
        return FcmTokenSaveResponseDto.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
    }
}
