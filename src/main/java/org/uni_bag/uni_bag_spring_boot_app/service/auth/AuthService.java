package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue.TokenReIssueDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.provider.JwtTokenProvider;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.redis.RedisService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final AppleOAuthService appleOAuthService;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public LoginDto login(LoginRequestDto requestDto) {
        OAuthUserInfoDto userInfo = getUserInfo(requestDto.getSnsType(), requestDto.getAccessToken());
        Optional<User> user = userRepository.findBySnsId(userInfo.getSnsId());
        if (user.isEmpty()) {
            signup(userInfo);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getSnsId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        redisService.save(refreshToken, accessToken);

        return LoginDto.of(accessToken, refreshToken);
    }


    private OAuthUserInfoDto getUserInfo(SnsType snsType, String accessToken) {
        return switch (snsType) {
            case Kakao -> OAuthUserInfoDto.from(kakaoOAuthService.getUserInfo(accessToken));
            case Naver -> OAuthUserInfoDto.from(naverOAuthService.getUserInfo(accessToken));
            case Apple -> OAuthUserInfoDto.from(appleOAuthService.getUserInfo(accessToken));
        };
    }

    private void signup(OAuthUserInfoDto userInfo) {
        userRepository.save(User.from(userInfo));
    }

    public void logout(String refreshToken) {
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        Optional<String> savedAccessToken = redisService.get(resolvedRefreshToken);
        if (savedAccessToken.isEmpty()) {
            throw new HttpErrorException(HttpErrorCode.NoSuchRefreshTokenError);
        }

        redisService.delete(resolvedRefreshToken);
    }

    public void deleteUser(String accessToken, String refreshToken, User user) {
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        Optional<String> savedAccessToken = redisService.get(resolvedRefreshToken);
        if (savedAccessToken.isEmpty()) {
            throw new HttpErrorException(HttpErrorCode.NoSuchRefreshTokenError);
        }
        switch (user.getSnsType()) {
            case Kakao -> kakaoOAuthService.deleteUser(accessToken);
            case Apple -> appleOAuthService.deleteUser(accessToken);
        }

        redisService.delete(resolvedRefreshToken);

        userRepository.delete(user);

    }

    public TokenReIssueDto reIssueToken(String accessToken, String refreshToken) {
        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        String savedAccessToken = redisService.get(resolvedRefreshToken).orElseThrow(
                () -> new HttpErrorException(HttpErrorCode.NoSuchRefreshTokenError)
        );

        // RefreshToken 유효성 및 만료여부 확인
        boolean isExpiredRefreshToken = jwtTokenProvider.isExpiredToken(resolvedRefreshToken);
        if (isExpiredRefreshToken) {
            redisService.delete(resolvedRefreshToken);
            throw new HttpErrorException(HttpErrorCode.ExpiredRefreshTokenError);
        }

        // RefreshToken이 탈취 당한 경우
        if (!resolvedAccessToken.equals(savedAccessToken)) {
            redisService.delete(resolvedRefreshToken);
            throw new HttpErrorException(HttpErrorCode.NoSuchAccessTokenError);
        }

        // AccessToken 유효성 및 만료여부 확인
        boolean isExpiredAccessToken = jwtTokenProvider.isExpiredToken(resolvedAccessToken);
        if (!isExpiredAccessToken) {
            redisService.delete(resolvedRefreshToken);
            throw new HttpErrorException(HttpErrorCode.NotExpiredAccessTokenError);
        }

        // 토큰 재발행
        String reIssuedAccessToken = jwtTokenProvider.reIssueAccessToken(resolvedAccessToken);
        redisService.delete(resolvedRefreshToken);
        redisService.save(resolvedRefreshToken, reIssuedAccessToken);
        return TokenReIssueDto.of(reIssuedAccessToken);

    }
}
