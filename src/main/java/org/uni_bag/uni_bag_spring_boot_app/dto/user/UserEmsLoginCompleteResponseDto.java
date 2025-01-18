package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEmsLoginCompleteResponseDto {
    @Schema(example = "EMS의 일부 계정 정보가 서비스 서버에 반영되었습니다.", description = "성공 메시지")
    private String message;

    public static UserEmsLoginCompleteResponseDto createResponse(){
        return new UserEmsLoginCompleteResponseDto("EMS의 일부 계정 정보가 서비스 서버에 반영되었습니다.");
    }
}
