package org.uni_bag.uni_bag_spring_boot_app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    /**
     * FCM 설정
     * ClassPathResource : Resource 폴더 이하 파일 경로 부터 읽음. -> 현재 비공개 엑세스 키 파일(Json) 경로 == resources/firebase/파일명.json
     */

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource("/firebase/service-account.json").getInputStream())
                    )
                    .build();

            log.info("Successfully initialized firebase app");
            return FirebaseApp.initializeApp(options);

        } catch (IOException exception) {
            log.error("Fail to initialize firebase app{}", exception.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
