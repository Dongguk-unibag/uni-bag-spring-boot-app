package org.uni_bag.uni_bag_spring_boot_app.service.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple.AppleJwt;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple.AppleValidateResponse;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class AppleOAuthService {
    private final WebClient webClient;

    @Value("${apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${apple.login-key}")
    private String APPLE_LOGIN_KEY;

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${apple.key-path}")
    private String APPLE_KEY_PATH;

    public AppleOAuthService(@Qualifier("appleWebClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public AppleJwt getUserInfo(String authorizationCode) {
        return decodeJwt(appleValidateCode(authorizationCode).getId_token());
    }

    public void deleteUser(String authorizationCode) {

        webClient.post()
                .uri(uriBuilder -> {
                    try {
                        return uriBuilder
                                .path("/auth/revoke")
                                .queryParam("client_id", APPLE_CLIENT_ID)
                                .queryParam("client_secret", generateClientSecret())
                                .queryParam("token", appleValidateCode(authorizationCode).getRefresh_token())
                                .queryParam("token_type_hint", "refresh_token")
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .retrieve()
                .onStatus(status -> status.value() == 400,
                        this::handle400Error)
                .bodyToMono(Void.class)
                .block();
    }

    private AppleValidateResponse appleValidateCode(String authorizationCode){
        return webClient.post()
                .uri(uriBuilder -> {
                    try {
                        return uriBuilder
                                .path("/auth/token")
                                .queryParam("client_id", APPLE_CLIENT_ID)
                                .queryParam("client_secret", generateClientSecret())
                                .queryParam("code", authorizationCode)
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("redirect_url")
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .retrieve()
                .onStatus(status -> status.value() == 400,
                        this::handle400Error)
                .bodyToMono(AppleValidateResponse.class)
                .block();
    }

    private AppleJwt decodeJwt(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format.");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        ObjectMapper objectMapper = new ObjectMapper();
        AppleJwt appleJwt;
        try {
            appleJwt = objectMapper.readValue(payload, AppleJwt.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return appleJwt;

    }

    private String generateClientSecret() throws Exception {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        return Jwts.builder()
                .setHeaderParam("alg", "ES256")
                .setHeaderParam(JwsHeader.KEY_ID, APPLE_LOGIN_KEY)
                .setIssuer(APPLE_TEAM_ID)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setAudience("https://appleid.apple.com")
                .setSubject(APPLE_CLIENT_ID)
                .signWith(loadPrivateKey(APPLE_KEY_PATH))
                .compact();
    }

    private PrivateKey loadPrivateKey(String resourcePath) throws Exception {

        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {

            String keyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String keyData = keyContent.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(keyData);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    private Mono<Throwable> handle400Error(ClientResponse response) {
        return Mono.error(new HttpErrorException(HttpErrorCode.BadRequestAppleError));
    }

}
