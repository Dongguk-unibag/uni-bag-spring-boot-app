package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.FcmToken;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FcmTokenRepositoryTest {
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("유저가 등록한 FCM 토큰 조회")
    void testFindByFcmToken(){
        // given
        String token1 = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn1";
        String token2 = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn2";
        String token3 = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn3";

        User user1 = createUser("123");
        User user2 = createUser("234");
        entityManager.persist(user1);
        entityManager.persist(user2);

        FcmToken fcmToken1 = FcmToken.of(user1, token1);
        FcmToken fcmToken2 = FcmToken.of(user1, token2);
        FcmToken fcmToken3 = FcmToken.of(user2, token3);
        entityManager.persist(fcmToken1);
        entityManager.persist(fcmToken2);
        entityManager.persist(fcmToken3);

        // when
        List<FcmToken> result = fcmTokenRepository.findByUser(user1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(fcmToken1, fcmToken2);
    }


    private User createUser(String snsId) {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto(snsId, SnsType.Kakao, "민수", "unibag@unibag.com");
        return User.from(oAuthUserInfoDto);
    }
}