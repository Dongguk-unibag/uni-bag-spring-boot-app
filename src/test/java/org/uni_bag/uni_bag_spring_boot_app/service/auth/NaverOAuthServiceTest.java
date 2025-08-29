package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.naver.NaverResponseDetails;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.naver.NaverUserInfoResponse;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class NaverOAuthServiceTest {
    private static MockWebServer mockWebServer;
    private NaverOAuthService naverOAuthService;
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
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        naverOAuthService = new NaverOAuthService(webClient);
    }


    @Nested
    @DisplayName("유저 정보 조회")
    class UserInfoGetTest {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String accessToken = "Bearer mockToken";
            NaverUserInfoResponse mockResponse = createNaverUserInfoResponse();
            String jsonResponse = objectMapper.writeValueAsString(mockResponse);

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(jsonResponse)
                    .addHeader("Content-Type", "application/json"));

            // when
            NaverUserInfoResponse response = naverOAuthService.getUserInfo(accessToken);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getResponseDetails().getId()).isEqualTo(mockResponse.getResponseDetails().getId());

            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v1/nid/me");
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
            assertThatThrownBy(() -> naverOAuthService.getUserInfo(invalidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UnauthorizedNaverError.getMessage());

            var recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v1/nid/me");
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
            assertThatThrownBy(() -> naverOAuthService.getUserInfo(invalidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.ForbiddenNaverError.getMessage());

            var recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/v1/nid/me");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(invalidAccessToken);
        }

        NaverUserInfoResponse createNaverUserInfoResponse() {
            NaverResponseDetails naverDetails = new NaverResponseDetails(
                    "1234567890",          // id
                    "nicknameExample",     // nickname
                    "홍길동",               // name
                    "hong@example.com",    // email
                    "M",                   // gender
                    "25",               // age
                    "01-01",               // birthday
                    "https://example.com/profile.jpg", // profileImage
                    "1995",                // birthyear
                    "010-1234-5678"        // mobile
            );

            return new NaverUserInfoResponse("200", "success", naverDetails);
        }
    }

}