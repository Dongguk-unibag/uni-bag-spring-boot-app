package org.uni_bag.uni_bag_spring_boot_app.dto.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenSaveRequestDto {
    @Schema(example = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn...", description = "FCM 토큰")
    @NotBlank(message = "fcmToken이 필요합니다.")
    private String fcmToken;
}
