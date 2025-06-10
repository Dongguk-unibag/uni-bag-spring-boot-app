package org.uni_bag.uni_bag_spring_boot_app.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.TokenType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    /**
     * 사용자의 snsId를 이용하여 AccessToken을 생성한다.
     * @param snsId
     * @return accessToken
     */
    public String generateAccessToken(String snsId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(snsId)); // subject
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * refreshToken을 생성한다.
     * @return refreshToken
     */
    public String generateRefreshToken() {
        Date now = new Date();

        // Refresh Token 생성
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * accessToken을 파싱하여 인증 객체(Authentication) 반환한다
     * @param accessToken
     * @return Authentication
     * @throws HttpErrorException
     */
    public Authentication getAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);

        if(claims.get("sub") == null){
            throw new HttpErrorException(HttpErrorCode.NotValidAccessTokenError);
        }

        String username = claims.get("sub").toString(); // 복호화된 accessToken 에서 사용자 id 추출
        User user = userRepository.findBySnsId(username).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    /**
     * 토큰 정보를 검증한다
     * @param tokenType
     * @param token
     * @throws HttpErrorException
     */
    public void validateToken(TokenType tokenType, String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (SignatureException | SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new HttpErrorException(HttpErrorCode.NotValidAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new HttpErrorException(HttpErrorCode.NotValidRefreshTokenError);
        } catch (ExpiredJwtException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new HttpErrorException(HttpErrorCode.ExpiredAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new HttpErrorException(HttpErrorCode.ExpiredRefreshTokenError);
        }
    }

    /**
     * 토큰 만료여부를 확인한다.
     * @param token
     * @return boolean
     * @throws HttpErrorException
     */
    public boolean isExpiredToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new HttpErrorException(HttpErrorCode.NotValidTokenError);
        }

        return false;
    }

    /**
     * token의 인증타입(Bearer)을 제거하여 반환
     * @param token
     * @return 인증타입(Bearer)을 제거한 token
     * @throws HttpErrorException 토큰이 유효하지 않을 경우 에러를 던진다.
     */
    public String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.substring(7);
        }

        throw new HttpErrorException(HttpErrorCode.NotValidTokenError);
    }

    /**
     * accessToken의 인증타입(Bearer)을 제거하여 반환
     * @param accessToken
     * @return 인증타입(Bearer)을 제거한 token
     * @throws HttpErrorException 토큰이 유효하지 않을 경우 에러를 던진다.
     */
    public String resolveAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            return accessToken.substring(7);
        }

        throw new HttpErrorException(HttpErrorCode.NotValidAccessTokenError);
    }

    /**
     * AccessToken 재발급
     * @param accessToken
     * @return 재발급된 accessToken
     */
    public String reIssueAccessToken(String accessToken) {
        Authentication authentication = getAuthentication(accessToken);
        Claims claims = Jwts.claims().setSubject(String.valueOf(((User) authentication.getPrincipal()).getSnsId())); // subject
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
