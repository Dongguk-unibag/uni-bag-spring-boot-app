package org.uni_bag.uni_bag_spring_boot_app.service.fcm;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.repository.FcmTokenRepository;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;
    public FcmTokenSaveResponseDto saveFcmToken(User user, FcmTokenSaveRequestDto fcmTokenSaveRequestDto) {
        fcmTokenRepository.save(FcmToken.of(user, fcmTokenSaveRequestDto.getFcmToken()));
        return FcmTokenSaveResponseDto.of(user.getId(), fcmTokenSaveRequestDto.getFcmToken());
    }
}
