package org.uni_bag.uni_bag_spring_boot_app.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.constant.TokenType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    @Mock
    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;

    final String secretKey = Base64.getEncoder().encodeToString("ThisIsASecretKeyForJwtTests1234567890".getBytes());

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secretKey, userRepository);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationPeriod", 1000L * 60); // 1분
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationPeriod", 1000L * 60 * 60); // 1시간
    }


    @Nested
    @DisplayName("액세스 토큰 생성")
    class AccessTokenCreateTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final String snsId = "testSnsId";

            // when
            String token = jwtTokenProvider.generateAccessToken(snsId);

            // then
            /// JWT 구조가 맞는지 확인
            assertThat(token.split("\\.").length).isEqualTo(3);

            Claims claims = parseClaims(token);
            long expMillis = claims.getExpiration().getTime();
            long expectedMillis = System.currentTimeMillis() + 1000L * 60;

            assertTrue(Math.abs(expMillis - expectedMillis) < 2000, "만료 시간이 예상 범위 내에 있어야 합니다.");

            assertThat(claims.getSubject()).isEqualTo(snsId);
        }

    }

    @Nested
    @DisplayName("리프레쉬 토큰 생성")
    class RefreshTokenCreateTest {
        @Test
        @DisplayName("성공")
        void success() {
            // when
            String token = jwtTokenProvider.generateRefreshToken();

            // then
            assertThat(token.split("\\.").length).isEqualTo(3);

            Claims claims = parseClaims(token);
            long expMillis = claims.getExpiration().getTime();
            long expectedMillis = System.currentTimeMillis() + 1000L * 60 * 60;

            assertTrue(Math.abs(expMillis - expectedMillis) < 2000, "만료 시간이 예상 범위 내에 있어야 합니다.");

        }
    }

    @Nested
    @DisplayName("인증 객체 얻기")
    class AuthenticationGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User user = createUser();
            String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0U25zSWQiLCJpYXQiOjE3NTY0MzQ1MDEsImV4cCI6MTc1NjQzNDU2MX0.mBYS0je2-6en4y4aCAdnVeGCwCK_b1N1WBtgKbCC9QU";
            given(userRepository.findBySnsId(any())).willReturn(Optional.of(user));

            // when
            jwtTokenProvider.getAuthentication(accessToken);

            // then
            then(userRepository).should(times(1)).findBySnsId(any());
        }

        @Test
        @DisplayName("실패 - sub가 없을 경우")
        void whenAccessTokenHasNotSub_MustReturnException() {
            // given
            String notValidAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NTY0Mzg1MTB9.10gNhFE1DlOE-dtHpWjjHvj73G-UgT_a0hBGla8N36k";

            // when
            assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(notValidAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAccessTokenError.getMessage());

        }

        @Test
        @DisplayName("실패 - 유저가 존재하지 않을 경우")
        void whenUserNotFound_MustThrowException() {
            // given
            String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0U25zSWQiLCJpYXQiOjE3NTY0MzQ1MDEsImV4cCI6MTc1NjQzNDU2MX0.mBYS0je2-6en4y4aCAdnVeGCwCK_b1N1WBtgKbCC9QU";
            given(userRepository.findBySnsId(any())).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(accessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());


            // then
            then(userRepository).should(times(1)).findBySnsId(any());
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class TokenValidationTest {
        @Test
        @DisplayName("성공 - RefreshToken")
        void whenAccessTokenValidate_MustPass() {
            // given
            TokenType tokenType = TokenType.ACCESS_TOKEN;
            String token = generateAccessToken();

            // when
            jwtTokenProvider.validateToken(tokenType, token);
        }

        @Test
        @DisplayName("성공 - RefreshToken")
        void whenRefreshTokenValidate_MustPass() {
            // given
            TokenType tokenType = TokenType.REFRESH_TOKEN;
            String token = generateRefreshToken();

            // when
            jwtTokenProvider.validateToken(tokenType, token);
        }

        @Test
        @DisplayName("실패 - 만료된 AccessToken")
        void whenAccessTokenIsExpired_MustReturnException() {
            // given
            TokenType tokenType = TokenType.ACCESS_TOKEN;
            String expiredAccessToken = generateExpiredAccessToken();

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(tokenType, expiredAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.ExpiredAccessTokenError.getMessage());
        }

        @Test
        @DisplayName("실패 - 만료된 refreshToken")
        void whenRefreshTokenIsExpired_MustReturnException() {
            // given
            TokenType tokenType = TokenType.REFRESH_TOKEN;
            String expiredAccessToken = generateExpiredRefreshToken();

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(tokenType, expiredAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.ExpiredRefreshTokenError.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 AccessToken")
        void whenAccessTokenIsNotValid_MustReturnException() {
            // given
            TokenType tokenType = TokenType.ACCESS_TOKEN;
            String expiredAccessToken = generateNotValidAccessToken();

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(tokenType, expiredAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAccessTokenError.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 refreshToken")
        void whenRefreshTokenIsNotValid_MustReturnException() {
            // given
            TokenType tokenType = TokenType.REFRESH_TOKEN;
            String expiredAccessToken = generateNotValidRefreshToken();

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(tokenType, expiredAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidRefreshTokenError.getMessage());
        }
    }

    @Nested
    @DisplayName("토큰 만료여부 확인")
    class TokenExpiredTest {
        @Test
        @DisplayName("성공 - 토큰이 만료됨")
        void whenTokenIsExpired_MustReturnTrue() {
            // given
            String accessToken = generateExpiredAccessToken();

            // when
            boolean expiredToken = jwtTokenProvider.isExpiredToken(accessToken);

            // then
            assertThat(expiredToken).isTrue();
        }

        @Test
        @DisplayName("성공 - 토큰이 만료되지 않음")
        void whenTokenIsNotExpired_MustReturnFalse() {
            // given
            String accessToken = generateAccessToken();

            // when
            boolean expiredToken = jwtTokenProvider.isExpiredToken(accessToken);

            // then
            assertThat(expiredToken).isFalse();
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 AceessToken")
        void whenAccessTokenIsNotValid_MustReturnException() {
            // given
            String expiredAccessToken = generateNotValidAccessToken();

            // when
            assertThatThrownBy(() -> jwtTokenProvider.isExpiredToken(expiredAccessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidTokenError.getMessage());
        }
    }
    @Nested
    @DisplayName("토큰 resolve")
    class ResolveTokenTest {

        @Test
        @DisplayName("성공")
        void whenTokenIsValid_MustReturnTokenWithoutBearer() {
            // given
            String token = "Bearer validToken";

            // when
            String result = jwtTokenProvider.resolveToken(token);

            // then
            assertThat(result).isEqualTo("validToken");
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 토큰")
        void whenTokenIsNotValid_MustReturnException() {
            // given
            String token = "InvalidToken";

            // expect
            assertThatThrownBy(() -> jwtTokenProvider.resolveToken(token))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessageContaining(HttpErrorCode.NotValidTokenError.getMessage());
        }
    }

    @Nested
    @DisplayName("엑세스 토큰 resolve")
    class ResolveAccessTokenTest {

        @Test
        @DisplayName("성공")
        void whenAccessTokenIsValid_MustReturnTokenWithoutBearer() {
            // given
            String accessToken = "Bearer accessTokenValue";

            // when
            String result = jwtTokenProvider.resolveAccessToken(accessToken);

            // then
            assertThat(result).isEqualTo("accessTokenValue");
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 엑세스 토큰")
        void whenAccessTokenIsNotValid_MustReturnException() {
            // given
            String accessToken = "InvalidAccessToken";

            // expect
            assertThatThrownBy(() -> jwtTokenProvider.resolveAccessToken(accessToken))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessageContaining(HttpErrorCode.NotValidAccessTokenError.getMessage());
        }
    }

    @Nested
    @DisplayName("토큰 재발행")
    class AccessTokenReIssueTest {

        @Test
        @DisplayName("성공")
        void whenAccessTokenIsValid_MustReturnNewAccessToken() {
            // given
            String accessToken = generateAccessToken();
            User user = createUser();
            given(userRepository.findBySnsId(any())).willReturn(Optional.of(user));

            // when
            String token = jwtTokenProvider.reIssueAccessToken(accessToken);

            // then
            assertThat(token.split("\\.").length).isEqualTo(3);

            Claims claims = parseClaims(token);
            long expMillis = claims.getExpiration().getTime();
            long expectedMillis = System.currentTimeMillis() + 1000L * 60;

            assertTrue(Math.abs(expMillis - expectedMillis) < 2000, "만료 시간이 예상 범위 내에 있어야 합니다.");
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 AccessToken")
        void whenAccessTokenIsNotValid_MustReturnException() {
            // given
            JwtTokenProvider spyProvider = spy(jwtTokenProvider);
            doThrow(new HttpErrorException(HttpErrorCode.NotValidAccessTokenError))
                    .when(spyProvider).getAuthentication(any());

            // when & then
            assertThatThrownBy(() -> spyProvider.reIssueAccessToken("invalidAccessToken"))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NotValidAccessTokenError.getMessage());
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public User createUser() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        return User.from(oAuthUserInfoDto);
    }

    private String buildToken(String keyBase64, String subject, long validityMillis) {
        byte[] keyBytes = Decoders.BASE64.decode(keyBase64);
        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMillis))
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256);

        if (subject != null) {
            builder.setClaims(Jwts.claims().setSubject(subject));
        }

        return builder.compact();
    }

    private String generateAccessToken() {
        return buildToken(secretKey, "testSnsId", 1000L * 60);
    }

    private String generateExpiredAccessToken() {
        return buildToken(secretKey, null, -1000L * 60);
    }

    private String generateNotValidAccessToken() {
        String notValidSecretKey = Base64.getEncoder()
                .encodeToString("ThisIsANotValidSecretKeyForJwtTests1234567890".getBytes());
        return buildToken(notValidSecretKey, "testSnsId", 1000L * 60);
    }

    private String generateRefreshToken() {
        return buildToken(secretKey, null, 1000L * 60 * 60);
    }

    private String generateExpiredRefreshToken() {
        return buildToken(secretKey, null, -1000L * 60 * 60);
    }

    private String generateNotValidRefreshToken() {
        String notValidSecretKey = Base64.getEncoder()
                .encodeToString("ThisIsANotValidSecretKeyForJwtTests1234567890".getBytes());
        return buildToken(notValidSecretKey, null, 1000L * 60 * 60);
    }
}