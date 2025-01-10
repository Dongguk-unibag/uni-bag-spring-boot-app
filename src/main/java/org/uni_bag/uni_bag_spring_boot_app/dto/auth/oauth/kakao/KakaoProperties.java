package org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoProperties {
    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("thumbnail_image")
    private String thumbnailImage;
}