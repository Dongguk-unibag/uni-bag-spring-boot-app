package org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class KakaoUserInfoResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("connected_at")
    private ZonedDateTime connectedAt;

    @JsonProperty("properties")
    private KakaoProperties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
}