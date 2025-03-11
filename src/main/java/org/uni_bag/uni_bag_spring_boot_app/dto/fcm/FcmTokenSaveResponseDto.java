package org.uni_bag.uni_bag_spring_boot_app.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;

@Getter
@AllArgsConstructor
@Builder
public class FcmTokenSaveResponseDto {
    private Long userId;
    private String fcmToken;

    public static FcmTokenSaveResponseDto of(Long userId, String fcmToken){
        return FcmTokenSaveResponseDto.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
    }
}
