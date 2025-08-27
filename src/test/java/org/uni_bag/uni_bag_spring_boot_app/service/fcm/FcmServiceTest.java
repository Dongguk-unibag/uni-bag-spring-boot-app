package org.uni_bag.uni_bag_spring_boot_app.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.repository.FcmTokenRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class FcmServiceTest {
    @InjectMocks
    private FcmService fcmService;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    private User user;

    @BeforeEach
    void setUp() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        this.user = User.from(oAuthUserInfoDto);
    }

    @Nested
    @DisplayName("알림 전송")
    class NotificationSend {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            FcmToken fcmToken1 = new FcmToken(1L, user, "fcmTokenValue1");
            FcmToken fcmToken2 = new FcmToken(2L, user, "fcmTokenVale2");
            given(fcmTokenRepository.findByUser(eq(user))).willReturn(List.of(fcmToken1, fcmToken2));

            // when
            fcmService.sendNotification(user, "알고리즘 1장 과제", "알고리즘 1장 과제 끝내기");

            // then
            then(fcmTokenRepository).should(times(1)).findByUser(eq(user));
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 토큰으로 FCM 메시지 전달 시 로그 출력")
        void whenInvalidTokenProvided_MustLogError() throws FirebaseMessagingException {
            // given
            FcmToken fcmToken1 = new FcmToken(1L, user, "invalidToken"); // 유효하지 않은
            FcmToken fcmToken2 = new FcmToken(2L, user, "fcmTokenVale2");

            given(fcmTokenRepository.findByUser(eq(user))).willReturn(List.of(fcmToken1, fcmToken2));
            given(firebaseMessaging.send(any())).willThrow(FirebaseMessagingException.class);

            // when & then
            assertThatCode(() -> fcmService.sendNotification(user, "알고리즘 1장 과제", "알고리즘 1장 과제 끝내기"))
                    .doesNotThrowAnyException(); // 예외가 서비스 레이어 밖으로 던져지지 않음

            // then
            then(fcmTokenRepository).should(times(1)).findByUser(eq(user));
            then(firebaseMessaging).should(times(2)).send(any());
        }
    }

    @Nested
    @DisplayName("FCM 토큰 저장")
    class FcmTokenSave {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            FcmTokenSaveRequestDto fcmTokenSaveRequestDto = new FcmTokenSaveRequestDto("fcmTokenValue");

            // when
            FcmTokenSaveResponseDto result = fcmService.saveFcmToken(user, fcmTokenSaveRequestDto);

            // then
            assertThat(result.getFcmToken()).isEqualTo(fcmTokenSaveRequestDto.getFcmToken());
            then(fcmTokenRepository).should(times(1)).save(any(FcmToken.class));
        }
    }
}