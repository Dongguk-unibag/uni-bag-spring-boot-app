package org.uni_bag.uni_bag_spring_boot_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsBaseUrl;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(SnsBaseUrl.KakaoBaseUrl.getUrl())
                .build();
    }

    @Bean
    public WebClient naverWebClient(){
        return WebClient.builder()
                .baseUrl(SnsBaseUrl.NaverBaseUrl.getUrl())
                .build();
    }
}
