package org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleJwt {

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("exp")
    private long exp;

    @JsonProperty("iat")
    private long iat;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("at_hash")
    private String atHash;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("auth_time")
    private long authTime;

    @JsonProperty("nonce_supported")
    private boolean nonceSupported;
}
