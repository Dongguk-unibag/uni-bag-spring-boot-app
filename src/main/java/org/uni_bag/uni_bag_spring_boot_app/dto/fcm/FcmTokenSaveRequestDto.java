package org.uni_bag.uni_bag_spring_boot_app.dto.fcm;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenSaveRequestDto {
    @NotBlank(message = "fcmToken이 필요합니다.")
    private String fcmToken;
}
