package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple.AppleJwt;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple.AppleValidateResponse;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AppleOAuthServiceTest {

    private static MockWebServer mockWebServer;
    private AppleOAuthService appleOAuthService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void init() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        appleOAuthService = new AppleOAuthService(webClient);

        ReflectionTestUtils.setField(appleOAuthService, "APPLE_TEAM_ID", "TEST_TEAM");
        ReflectionTestUtils.setField(appleOAuthService, "APPLE_LOGIN_KEY", "TEST_KEY");
        ReflectionTestUtils.setField(appleOAuthService, "APPLE_CLIENT_ID", "TEST_CLIENT");
        ReflectionTestUtils.setField(appleOAuthService, "APPLE_KEY_PATH", "fake-key.p8");

    }

   @Nested
   @DisplayName("유저정보 조회")
   class UserInfoTest {
       @Test
       @DisplayName("성공")
       void success() throws Exception {
           // given: Mock 서버가 /auth/token 요청에 응답
           AppleValidateResponse mockResponse = new AppleValidateResponse();
           mockResponse.setId_token(generateDummyJwt());
           mockResponse.setRefresh_token("refresh-token");

           mockWebServer.enqueue(new MockResponse()
                   .setBody(objectMapper.writeValueAsString(mockResponse))
                   .addHeader("Content-Type", "application/json")
                   .setResponseCode(200));

           // when
           AppleJwt appleJwt = appleOAuthService.getUserInfo("dummy-code");

           // then
           assertThat(appleJwt).isNotNull();
           assertThat(appleJwt.getEmail()).isEqualTo("unibag@unibag.com"); // generateDummyJwt에 맞게 검증
       }
   }

    @Nested
    @DisplayName("회원 탈퇴")
    class WithDrawlTest {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // 첫번째 호출: /auth/token
            AppleValidateResponse mockResponse = new AppleValidateResponse();
            mockResponse.setId_token(generateDummyJwt());
            mockResponse.setRefresh_token("refresh-token");

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(mockResponse))
                    .addHeader("Content-Type", "application/json")
                    .setResponseCode(200));

            // 두번째 호출: /auth/revoke
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200));

            // when & then
            appleOAuthService.deleteUser("dummy-code");

            assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("실패 - 유저 조회 실패")
        void whenGettingUserInfoFail_MustReturn400() throws Exception {
            // 첫번째 호출: /auth/token
            AppleValidateResponse mockResponse = new AppleValidateResponse();
            mockResponse.setId_token(generateDummyJwt());
            mockResponse.setRefresh_token("refresh-token");

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(mockResponse))
                    .addHeader("Content-Type", "application/json")
                    .setResponseCode(200));

            // 두번째 호출: /auth/revoke
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(400));

            // when & then
            assertThatThrownBy(() -> appleOAuthService.deleteUser("dummy-code"))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.BadRequestAppleError.getMessage());
        }
    }


    /**
     * 단순히 payload만 Base64로 인코딩한 더미 JWT 생성
     */
    private String generateDummyJwt() {
        String header = "{\"alg\":\"none\"}";
        String payload = "{\"email\":\"unibag@unibag.com\",\"sub\":\"12345\"}";
        String encodedHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        return encodedHeader + "." + encodedPayload + "."; // signature 없음
    }
}
