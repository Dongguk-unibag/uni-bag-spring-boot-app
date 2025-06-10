package org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class KakaoAccount {
    @JsonProperty("profile_needs_agreement")
    private boolean profileNeedsAgreement;

    @JsonProperty("email_needs_agreement")
    private boolean emailNeedsAgreement;

    private KakaoUserProfile profile;
}
