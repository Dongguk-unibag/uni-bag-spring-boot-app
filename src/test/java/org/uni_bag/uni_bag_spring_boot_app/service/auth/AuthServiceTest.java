package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple.AppleJwt;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoAccount;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoProperties;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoUserInfoResponse;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoUserProfile;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.naver.NaverResponseDetails;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.naver.NaverUserInfoResponse;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.provider.JwtTokenProvider;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.redis.RedisService;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private KakaoOAuthService kakaoOAuthService;

    @Mock
    private NaverOAuthService naverOAuthService;

    @Mock
    private AppleOAuthService appleOAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        this.user = User.from(oAuthUserInfoDto);
    }

    @Nested
    @DisplayName("로그인")
    class LoginTest {
        @Test
        @DisplayName("성공 - 카카오")
        void whenKakaoLogin_success() {
            // given
            String kakaoAccessToken = "Bearer 12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Kakao, kakaoAccessToken);

            KakaoUserInfoResponse kakaoUserInfoResponse = createKakaoUserInfoResponse();
            given(kakaoOAuthService.getUserInfo(eq(kakaoAccessToken))).willReturn(kakaoUserInfoResponse);
            given(userRepository.findBySnsId(eq(String.valueOf(kakaoUserInfoResponse.getId())))).willReturn(Optional.of(user));
            given(jwtTokenProvider.generateAccessToken(any())).willReturn(serviceAccessToken);
            given(jwtTokenProvider.generateRefreshToken()).willReturn(serviceRefreshToken);

            // when
            authService.login(request);

            // then
            then(userRepository).should(times(1)).findBySnsId(any());
            then(jwtTokenProvider).should(times(1)).generateAccessToken(any());
            then(jwtTokenProvider).should(times(1)).generateRefreshToken();
            then(redisService).should(times(1)).save(eq(serviceRefreshToken), eq(serviceAccessToken));
        }

        @Test
        @DisplayName("성공 - 네이버")
        void whenNaverLogin_success() {
            // given
            String naverAccessToken = "Bearer 12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Naver, naverAccessToken);

            NaverUserInfoResponse naverUserInfoResponse = createNaverUserInfoResponse();
            given(naverOAuthService.getUserInfo(eq(naverAccessToken))).willReturn(naverUserInfoResponse);
            given(userRepository.findBySnsId(naverUserInfoResponse.getResponseDetails().getId())).willReturn(Optional.of(user));
            given(jwtTokenProvider.generateAccessToken(any())).willReturn(serviceAccessToken);
            given(jwtTokenProvider.generateRefreshToken()).willReturn(serviceRefreshToken);

            // when
            authService.login(request);

            // then
            then(userRepository).should(times(1)).findBySnsId(any());
            then(jwtTokenProvider).should(times(1)).generateAccessToken(any());
            then(jwtTokenProvider).should(times(1)).generateRefreshToken();
            then(redisService).should(times(1)).save(eq(serviceRefreshToken), eq(serviceAccessToken));
        }

        @Test
        @DisplayName("성공 - 애플")
        void whenAppleLogin_success() {
            // given
            String appleAccessToken = "12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Apple, appleAccessToken);

            AppleJwt appleJwt = createAppleJwt();
            given(appleOAuthService.getUserInfo(eq(appleAccessToken))).willReturn(appleJwt);
            given(userRepository.findBySnsId(eq(appleJwt.getSub()))).willReturn(Optional.of(user));
            given(jwtTokenProvider.generateAccessToken(any())).willReturn(serviceAccessToken);
            given(jwtTokenProvider.generateRefreshToken()).willReturn(serviceRefreshToken);

            // when
            authService.login(request);

            // then
            then(userRepository).should(times(1)).findBySnsId(any());
            then(jwtTokenProvider).should(times(1)).generateAccessToken(any());
            then(jwtTokenProvider).should(times(1)).generateRefreshToken();
            then(redisService).should(times(1)).save(eq(serviceRefreshToken), eq(serviceAccessToken));
        }

        @Test
        @DisplayName("성공 - 회원가입")
        void whenNonRegisteredUserLogsIn_ThenUserIsRegistered() {
            // given
            String kakaoAccessToken = "Bearer 12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Kakao, kakaoAccessToken);

            KakaoUserInfoResponse kakaoUserInfoResponse = createKakaoUserInfoResponse();
            given(kakaoOAuthService.getUserInfo(eq(kakaoAccessToken))).willReturn(kakaoUserInfoResponse);
            given(userRepository.findBySnsId(eq(String.valueOf(kakaoUserInfoResponse.getId())))).willReturn(Optional.empty());
            given(jwtTokenProvider.generateAccessToken(any())).willReturn(serviceAccessToken);
            given(jwtTokenProvider.generateRefreshToken()).willReturn(serviceRefreshToken);

            // when
            authService.login(request);

            // then
            then(userRepository).should(times(1)).findBySnsId(any());
            then(userRepository).should(times(1)).save(any());
            then(jwtTokenProvider).should(times(1)).generateAccessToken(any());
            then(jwtTokenProvider).should(times(1)).generateRefreshToken();
            then(redisService).should(times(1)).save(eq(serviceRefreshToken), eq(serviceAccessToken));
        }

        @Test
        @DisplayName("실패 - Oauth 로그인 실패")
        void whenOauthLoginFail_MustThrowException() {
            // given
            String appleAccessToken = "12345";
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";

            LoginRequestDto request = new LoginRequestDto(SnsType.Kakao, appleAccessToken);

            HttpErrorCode badRequestKakaoError = HttpErrorCode.BadRequestKakaoError;
            given(kakaoOAuthService.getUserInfo(eq(appleAccessToken))).willThrow(new HttpErrorException(badRequestKakaoError));

            // when
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(badRequestKakaoError.getMessage());

            // then
            then(userRepository).should(never()).findBySnsId(any());
            then(jwtTokenProvider).should(never()).generateAccessToken(any());
            then(jwtTokenProvider).should(never()).generateRefreshToken();
            then(redisService).should(never()).save(eq(serviceRefreshToken), eq(serviceAccessToken));
        }

        KakaoUserInfoResponse createKakaoUserInfoResponse() {
            KakaoUserProfile kakaoUserProfile = new KakaoUserProfile("코린이", null, null, false, false);
            KakaoAccount kakaoAccount = new KakaoAccount(false, false, kakaoUserProfile);

            return new KakaoUserInfoResponse(1L, ZonedDateTime.now(), new KakaoProperties(), kakaoAccount);
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

        AppleJwt createAppleJwt() {
            return new AppleJwt(
                    "https://appleid.apple.com",  // iss
                    "com.example.app",            // aud
                    1735689600L,                  // exp (예: 2025-12-01 00:00:00 UTC)
                    1735603200L,                  // iat (예: 2025-11-30 00:00:00 UTC)
                    "000123.456789abcdef",        // sub
                    "xyz123abc456",               // atHash
                    "user@example.com",           // email
                    true,                         // emailVerified
                    1735603200L,                  // authTime
                    true                          // nonceSupported
            );
        }
    }


    @Nested
    @DisplayName("회원 탈퇴")
    class WithdrawalTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            String oathAccessToken = "oathAccessToken";
            String savedAccessToken = "Bearer 12345";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedRefreshToken = "34567";

            given(jwtTokenProvider.resolveToken(serviceRefreshToken)).willReturn(resolvedRefreshToken);
            given(redisService.get(eq(resolvedRefreshToken))).willReturn(Optional.of(savedAccessToken));

            // when
            authService.deleteUser(oathAccessToken, serviceRefreshToken, user);

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(kakaoOAuthService).should(times(1)).deleteUser(eq(oathAccessToken));
            then(redisService).should(times(1)).get(eq(resolvedRefreshToken));
            then(redisService).should(times(1)).delete(eq(resolvedRefreshToken));
            then(userRepository).should(times(1)).delete(eq(user));

        }

        @Test
        @DisplayName("실패 - Oauth 탈퇴 실패")
        void whenOauthWithdrawalFail_MustThrowException() {
            // given
            String oathAccessToken = "oathAccessToken";
            String savedAccessToken = "Bearer 12345";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedRefreshToken = "34567";
            HttpErrorCode badRequestKakaoError = HttpErrorCode.BadRequestKakaoError;

            given(jwtTokenProvider.resolveToken(serviceRefreshToken)).willReturn(resolvedRefreshToken);
            given(redisService.get(eq(resolvedRefreshToken))).willReturn(Optional.of(savedAccessToken));
            doThrow(new HttpErrorException(badRequestKakaoError))
                    .when(kakaoOAuthService).deleteUser(eq(oathAccessToken));

            // when
            assertThatThrownBy(() -> authService.deleteUser(oathAccessToken, serviceRefreshToken, user))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(badRequestKakaoError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(resolvedRefreshToken));
            then(kakaoOAuthService).should(times(1)).deleteUser(eq(oathAccessToken));
            then(redisService).should(never()).delete(any());
            then(userRepository).should(never()).delete(any());

        }

        @Test
        @DisplayName("실패 - 유효하지 않은 RefreshToken으로 요청할 경우 에러 반환")
        void whenInvalidRefreshToken_MustThrowException() {
            // given
            String oathAccessToken = "oathAccessToken";
            String serviceRefreshToken = "Bearer 34567";
            String invalidServiceRefreshToken = "invalidServiceRefreshToken";

            given(jwtTokenProvider.resolveToken(serviceRefreshToken)).willReturn(invalidServiceRefreshToken);
            given(redisService.get(eq(invalidServiceRefreshToken))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> authService.deleteUser(oathAccessToken, serviceRefreshToken, user))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchRefreshTokenError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(invalidServiceRefreshToken);
            then(kakaoOAuthService).should(never()).deleteUser(eq(oathAccessToken));
            then(redisService).should(never()).delete(any());
            then(userRepository).should(never()).delete(any());
        }
    }

    @Nested
    @DisplayName("토큰 재발행")
    class TokenReissueTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedAccessToken = "23456";
            String resolvedRefreshToken = "34567";
            String reIssuedAccessToken = "Bearer 34567";


            given(jwtTokenProvider.resolveToken(eq(serviceAccessToken))).willReturn(resolvedAccessToken);
            given(jwtTokenProvider.resolveToken(eq(serviceRefreshToken))).willReturn(resolvedRefreshToken);
            given(redisService.get(eq(resolvedRefreshToken))).willReturn(Optional.of(resolvedAccessToken));
            given(jwtTokenProvider.isExpiredToken(eq(resolvedRefreshToken))).willReturn(false);
            given(jwtTokenProvider.isExpiredToken(eq(resolvedAccessToken))).willReturn(true);
            given(jwtTokenProvider.reIssueAccessToken(resolvedAccessToken)).willReturn(reIssuedAccessToken);

            // when
            authService.reIssueToken(serviceAccessToken, serviceRefreshToken);

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceAccessToken));
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(resolvedAccessToken));
            then(jwtTokenProvider).should(times(1)).reIssueAccessToken(eq(resolvedAccessToken));
        }


        @Test
        @DisplayName("실패 - 존재하지 않는 refreshToken으로 요청한 경우 에러 반환")
        void whenNonExistRefreshToken_MustThrowException() {
            // given
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedAccessToken = "23456";
            String nonExistRefreshToken = "34567";

            given(jwtTokenProvider.resolveToken(eq(serviceAccessToken))).willReturn(resolvedAccessToken);
            given(jwtTokenProvider.resolveToken(eq(serviceRefreshToken))).willReturn(nonExistRefreshToken);
            given(redisService.get(eq(nonExistRefreshToken))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> authService.reIssueToken(serviceAccessToken, serviceRefreshToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchRefreshTokenError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceAccessToken));
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(nonExistRefreshToken));
            then(jwtTokenProvider).should(never()).isExpiredToken(eq(nonExistRefreshToken));
            then(jwtTokenProvider).should(never()).isExpiredToken(eq(resolvedAccessToken));
            then(jwtTokenProvider).should(never()).reIssueAccessToken(eq(resolvedAccessToken));

        }

        @Test
        @DisplayName("실패 - 만료된 refreshToken으로 요청한 경우 에러 반환")
        void whenExpiredRefreshToken_MustThrowException() {
            // given
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedAccessToken = "23456";
            String expiredRefreshToken = "34567";

            given(jwtTokenProvider.resolveToken(eq(serviceAccessToken))).willReturn(resolvedAccessToken);
            given(jwtTokenProvider.resolveToken(eq(serviceRefreshToken))).willReturn(expiredRefreshToken);
            given(redisService.get(eq(expiredRefreshToken))).willReturn(Optional.of(resolvedAccessToken));
            given(jwtTokenProvider.isExpiredToken(eq(expiredRefreshToken))).willReturn(true);

            // when
            assertThatThrownBy(() -> authService.reIssueToken(serviceAccessToken, serviceRefreshToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.ExpiredRefreshTokenError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceAccessToken));
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(expiredRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(expiredRefreshToken));
            then(jwtTokenProvider).should(never()).isExpiredToken(eq(resolvedAccessToken));
            then(jwtTokenProvider).should(never()).reIssueAccessToken(eq(resolvedAccessToken));
        }

        @Test
        @DisplayName("실패 - RefreshToken이 탈취 당한 경우 에러 반환")
        void whenStolenRefreshToken_MustThrowException() {
            // given
            String savedAccessToken = "23456";
            String invalidAccessToken = "invalidAccessToken";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedInvalidAccessToken = "resolvedInvalidAccessToken";
            String resolvedRefreshToken = "34567";


            given(jwtTokenProvider.resolveToken(eq(invalidAccessToken))).willReturn(resolvedInvalidAccessToken);
            given(jwtTokenProvider.resolveToken(eq(serviceRefreshToken))).willReturn(resolvedRefreshToken);
            given(redisService.get(eq(resolvedRefreshToken))).willReturn(Optional.of(savedAccessToken));
            given(jwtTokenProvider.isExpiredToken(eq(resolvedRefreshToken))).willReturn(false);

            // when
            assertThatThrownBy(() -> authService.reIssueToken(invalidAccessToken, serviceRefreshToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchAccessTokenError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(invalidAccessToken));
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(never()).isExpiredToken(eq(resolvedInvalidAccessToken));
            then(jwtTokenProvider).should(never()).reIssueAccessToken(eq(resolvedInvalidAccessToken));

        }

        @Test
        @DisplayName("실패 - 만료되지않는 accessToken으로 요청한 경우 에러 반환")
        void whenNotExpiredAccessToken_MustThrowException() {
            // given
            String serviceAccessToken = "Bearer 23456";
            String serviceRefreshToken = "Bearer 34567";
            String resolvedNotExpiredAccessToken = "23456";
            String resolvedRefreshToken = "34567";


            given(jwtTokenProvider.resolveToken(eq(serviceAccessToken))).willReturn(resolvedNotExpiredAccessToken);
            given(jwtTokenProvider.resolveToken(eq(serviceRefreshToken))).willReturn(resolvedRefreshToken);
            given(redisService.get(eq(resolvedRefreshToken))).willReturn(Optional.of(resolvedNotExpiredAccessToken));
            given(jwtTokenProvider.isExpiredToken(eq(resolvedRefreshToken))).willReturn(false);
            given(jwtTokenProvider.isExpiredToken(eq(resolvedNotExpiredAccessToken))).willReturn(false);

            // when
            assertThatThrownBy(() -> authService.reIssueToken(serviceAccessToken, serviceRefreshToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotExpiredAccessTokenError.getMessage());

            // then
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceAccessToken));
            then(jwtTokenProvider).should(times(1)).resolveToken(eq(serviceRefreshToken));
            then(redisService).should(times(1)).get(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(resolvedRefreshToken));
            then(jwtTokenProvider).should(times(1)).isExpiredToken(eq(resolvedNotExpiredAccessToken));
            then(jwtTokenProvider).should(never()).reIssueAccessToken(eq(resolvedNotExpiredAccessToken));
        }
    }
}