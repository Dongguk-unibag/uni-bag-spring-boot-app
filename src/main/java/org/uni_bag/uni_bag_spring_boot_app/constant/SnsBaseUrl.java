package org.uni_bag.uni_bag_spring_boot_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SnsBaseUrl {
    KakaoBaseUrl("https://kapi.kakao.com"),
    NaverBaseUrl("https://openapi.naver.com"),
    AppleBaseUrl("https://appleid.apple.com"),
    TestBaseUrl("http://localhost");

    private final String url;
}
