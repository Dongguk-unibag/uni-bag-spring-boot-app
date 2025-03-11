package org.uni_bag.uni_bag_spring_boot_app.controller.fcm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.fcm.FcmService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {
    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<FcmTokenSaveResponseDto> saveFcmToken(@AuthenticationPrincipal User user,
                                                                @Valid @RequestBody FcmTokenSaveRequestDto fcmTokenSaveRequestDto){
        FcmTokenSaveResponseDto fcmTokenSaveResponseDto = fcmService.saveFcmToken(user, fcmTokenSaveRequestDto);
        return new ResponseEntity<>(fcmTokenSaveResponseDto, HttpStatus.CREATED);
    }
}
