package org.uni_bag.uni_bag_spring_boot_app.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginToken;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue.TokenReIssueDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.auth.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("로그인")
    class LoginTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String kakaoAccessToken = "Bearer 12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Kakao, kakaoAccessToken);
            LoginDto loginDto = new LoginDto(new LoginToken(serviceAccessToken, serviceRefreshToken));

            given(authService.login(any(LoginRequestDto.class))).willReturn(loginDto);

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.token").exists());
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class LogoutTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String serviceRefreshToken = "Bearer 34567";

            // when & then
            mockMvc.perform(post("/api/auth/logout")
                            .header("Authorization-refresh", serviceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 유효하지않은 RefreshToken으로 요청할 경우 에러 반환.")
        void whenInvalidRefreshToken_MustReturn401() throws Exception {
            // given
            String invalidServiceRefreshToken = "invalidServiceRefreshToken";
            HttpErrorCode noSuchRefreshTokenError = HttpErrorCode.NoSuchRefreshTokenError;
            doThrow(new HttpErrorException(noSuchRefreshTokenError))
                    .when(authService).logout(any());


            // when & then
            mockMvc.perform(post("/api/auth/logout")
                            .header("Authorization-refresh", invalidServiceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(noSuchRefreshTokenError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchRefreshTokenError.getMessage()));
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class TokenReissueTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            TokenReIssueDto tokenReIssueDto = new TokenReIssueDto(serviceAccessToken, serviceRefreshToken);

            given(authService.reIssueToken(any(), any())).willReturn(tokenReIssueDto);

            // when & then
            mockMvc.perform(post("/api/auth/refreshToken")
                            .header("Authorization", serviceAccessToken)
                            .header("Authorization-refresh", serviceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.accessToken").exists());

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않는 refeshToken으로 요청한 경우 에러 반환")
        void whenNonExistRefreshToken_MustReturn401() throws Exception {
            // given
            String serviceAccessToken = "Bearer 23456";
            String nonExistServiceRefreshToken = "nonExistServiceRefreshToken";

            HttpErrorCode noSuchRefreshTokenError = HttpErrorCode.NoSuchRefreshTokenError;
            given(authService.reIssueToken(any(), any())).willThrow(new HttpErrorException(noSuchRefreshTokenError));

            // when & then
            mockMvc.perform(post("/api/auth/refreshToken")
                            .header("Authorization", serviceAccessToken)
                            .header("Authorization-refresh", nonExistServiceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(noSuchRefreshTokenError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchRefreshTokenError.getMessage()));

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 만료된 refeshToken으로 요청한 경우 에러 반환")
        void whenExpiredRefreshToken_MustReturn401() throws Exception {
            // given
            String serviceAccessToken = "Bearer 23456";
            String expiredServiceRefreshToken = "expiredServiceRefreshToken";

            HttpErrorCode expiredRefreshTokenError = HttpErrorCode.ExpiredRefreshTokenError;
            given(authService.reIssueToken(any(), any())).willThrow(new HttpErrorException(expiredRefreshTokenError));

            // when & then
            mockMvc.perform(post("/api/auth/refreshToken")
                            .header("Authorization", serviceAccessToken)
                            .header("Authorization-refresh", expiredServiceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(expiredRefreshTokenError.name()))
                    .andExpect(jsonPath("$.message").value(expiredRefreshTokenError.getMessage()));

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - RefreshToken이 탈취 당한 경우 에러 반환")
        void whenStolenRefreshToken_MustReturn404() throws Exception {
            // given
            String serviceAccessToken = "Bearer 23456";
            String invalidServiceRefreshToken = "invalidServiceRefreshToken";

            HttpErrorCode noSuchAccessTokenError = HttpErrorCode.NoSuchAccessTokenError;
            given(authService.reIssueToken(any(), any())).willThrow(new HttpErrorException(noSuchAccessTokenError));

            mockMvc.perform(post("/api/auth/refreshToken")
                            .header("Authorization", serviceAccessToken)
                            .header("Authorization-refresh", invalidServiceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(noSuchAccessTokenError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchAccessTokenError.getMessage()));

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 만료되지않는 accessToken으로 요청한 경우 에러 반환")
        void whenNotExpiredAccessToken_MustReturn401() throws Exception {
            // given
            String notExpiredServiceAccessToken = "notExpiredServiceAccessToken";
            String serviceRefreshToken = "Bearer 34567";

            HttpErrorCode notExpiredAccessTokenError = HttpErrorCode.NotExpiredAccessTokenError;
            given(authService.reIssueToken(any(), any())).willThrow(new HttpErrorException(notExpiredAccessTokenError));

            // when & then
            mockMvc.perform(post("/api/auth/refreshToken")
                            .header("Authorization", notExpiredServiceAccessToken)
                            .header("Authorization-refresh", serviceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(notExpiredAccessTokenError.name()))
                    .andExpect(jsonPath("$.message").value(notExpiredAccessTokenError.getMessage()));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class WithdrawalTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            String Oauth2AccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            // when & then
            mockMvc.perform(post("/api/auth/delete")
                            .header("OAuthAccessToken", Oauth2AccessToken)
                            .header("Authorization-refresh", serviceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 유효하지않은 RefreshToken으로 요청할 경우 에러 반환.")
        void whenNotValidRefreshToken_MustReturn401() throws Exception {
            String Oauth2AccessToken = "Bearer 23456";
            String invalidServiceRefreshToken = "invalidServiceRefreshToken";

            HttpErrorCode noSuchRefreshTokenError = HttpErrorCode.NoSuchRefreshTokenError;
            doThrow(new HttpErrorException(noSuchRefreshTokenError))
                    .when(authService).deleteUser(any(), any(), any());

            // when & then
            mockMvc.perform(post("/api/auth/delete")
                            .header("OAuthAccessToken", Oauth2AccessToken)
                            .header("Authorization-refresh", invalidServiceRefreshToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value(noSuchRefreshTokenError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchRefreshTokenError.getMessage()));
        }
    }
}