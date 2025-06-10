package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTosRescissionResponseDto {
    @Schema(example = "이용약관 동의가 철회 되었습니다.", description = "성공 메시지")
    private String message;

    public static UserTosRescissionResponseDto createResponse(){
        return new UserTosRescissionResponseDto("이용약관 동의가 철회 되었습니다.");
    }
}
