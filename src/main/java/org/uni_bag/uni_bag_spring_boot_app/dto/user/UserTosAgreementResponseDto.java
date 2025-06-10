package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTosAgreementResponseDto {
    @Schema(example = "이용약관 동의가 활성화 되었습니다.", description = "성공 메시지")
    private String message;

    public static UserTosAgreementResponseDto createResponse(){
        return new UserTosAgreementResponseDto("이용약관 동의가 활성화 되었습니다.");
    }
}
