package org.uni_bag.uni_bag_spring_boot_app.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao.KakaoUserInfoResponse;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class KakaoOAuthService {
    private final WebClient webClient;

    public KakaoOAuthService(@Qualifier("kakaoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri("/v2/user/me")
                .header("Authorization", accessToken)
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        this::handle401Error)
                .onStatus(status -> status.value() == 403,
                        this::handle403Error)
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }

    private Mono<Throwable> handle401Error(ClientResponse response) {
        return Mono.error(new HttpErrorException(HttpErrorCode.UnauthorizedKakaoError));
    }

    private Mono<Throwable> handle403Error(ClientResponse response) {
        return Mono.error(new HttpErrorException(HttpErrorCode.ForbiddenKakaoError));
    }
}
