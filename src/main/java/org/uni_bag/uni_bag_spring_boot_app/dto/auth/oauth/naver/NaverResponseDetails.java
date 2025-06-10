package org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverResponseDetails {
    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private String age;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("birthyear")
    private String birthyear;

    @JsonProperty("mobile")
    private String mobile;
}