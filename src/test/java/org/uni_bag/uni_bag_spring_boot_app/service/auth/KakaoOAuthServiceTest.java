package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoAccount;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoProperties;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoUserInfoResponse;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoUserProfile;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class KakaoOAuthServiceTest {
    private static MockWebServer mockWebServer;
    private KakaoOAuthService kakaoOAuthService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setupServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());

        // WebClient를 MockWebServer 주소로 주입
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        kakaoOAuthService = new KakaoOAuthService(webClient);
    }

    @Nested
    @DisplayName("유저 정보 조회")
    class UserInfoGetTest {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String accessToken = "Bearer mockToken";
            KakaoUserInfoResponse mockResponse = createKakaoUserInfoResponse();
            String jsonResponse = objectMapper.writeValueAsString(mockResponse);

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(jsonResponse)
                    .addHeader("Content-Type", "application/json"));

            // when
            KakaoUserInfoResponse response = kakaoOAuthService.getUserInfo(accessToken);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(mockResponse.getId());

            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v2/user/me");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(accessToken);
        }

        @Test
        @DisplayName("실패 - 유저 조회 실패(401)")
        void whenGettingUserInfoFail_MustReturn401() throws Exception {
            // given
            String invalidAccessToken = "Bearer invalidToken";

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setBody("{\"msg\":\"Unauthorized\"}")
                    .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> kakaoOAuthService.getUserInfo(invalidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UnauthorizedKakaoError.getMessage());

            var recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v2/user/me");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(invalidAccessToken);
        }

        @Test
        @DisplayName("실패 - 유저 조회 실패(403)")
        void whenGettingUserInfoFail_MustReturn403() throws Exception {
            // given
            String invalidAccessToken = "Bearer invalidToken";

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(403)
                    .setBody("{\"msg\":\"Forbidden\"}")
                    .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> kakaoOAuthService.getUserInfo(invalidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.ForbiddenKakaoError.getMessage());

            var recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v2/user/me");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(invalidAccessToken);
        }

        KakaoUserInfoResponse createKakaoUserInfoResponse() {
            KakaoUserProfile kakaoUserProfile = new KakaoUserProfile("코린이", null, null, false, false);
            KakaoAccount kakaoAccount = new KakaoAccount(false, false, kakaoUserProfile);

            return new KakaoUserInfoResponse(1L, ZonedDateTime.now(), new KakaoProperties(), kakaoAccount);
        }
    }

    @Nested
    @DisplayName("유저 탈퇴")
    class WithDrawlTest {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String accessToken = "Bearer mockToken";

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200));

            // when
            kakaoOAuthService.deleteUser(accessToken);

            // then
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v1/user/unlink");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(accessToken);
        }

        @Test
        @DisplayName("실패 - 유저 탈퇴 실패")
        void whenWithDrawlFail_MustReturn400() throws Exception {
            // given
            String invalidAccessToken = "Bearer invalidToken";
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(400)
                    .setBody("{\"msg\":\"Bad Request\"}")
                    .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> kakaoOAuthService.deleteUser(invalidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.BadRequestKakaoError.getMessage());

            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v1/user/unlink");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(invalidAccessToken);
        }
    }



}