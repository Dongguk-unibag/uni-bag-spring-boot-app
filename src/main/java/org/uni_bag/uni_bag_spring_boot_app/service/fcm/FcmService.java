package org.uni_bag.uni_bag_spring_boot_app.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.repository.FcmTokenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    public void sendNotification(User user, String title, String body) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByUser(user);

        fcmTokens.forEach(fcmToken -> {
            log.info("Attempting to send Notification (title: {}, body: {}, fcmToken: {})", title, body, fcmToken.getFcmToken());
            send(createMessage(title, body, fcmToken.getFcmToken()));
        });
    }

    public FcmTokenSaveResponseDto saveFcmToken(User user, FcmTokenSaveRequestDto fcmTokenSaveRequestDto) {
        fcmTokenRepository.save(FcmToken.of(user, fcmTokenSaveRequestDto.getFcmToken()));
        return FcmTokenSaveResponseDto.of(user.getId(), fcmTokenSaveRequestDto.getFcmToken());
    }


    private void send(Message message) {
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully send Notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send Notification : {}", e.getMessage());
        }
    }

    private Message createMessage(String title, String body, String fcmToken) {
        return Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(fcmToken)
                .build();
    }
}
