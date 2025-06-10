package org.uni_bag.uni_bag_spring_boot_app.dto.auth.delete;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "DeleteResponse")
public class DeleteResponseDto {
    @Schema(example = "회원탈퇴 되었습니다.")
    private String message;

    public static DeleteResponseDto success() {
        return DeleteResponseDto.builder()
                .message("회원탈퇴 되었습니다.")
                .build();
    }
}
