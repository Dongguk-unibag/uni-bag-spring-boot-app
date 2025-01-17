package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

@Getter
@AllArgsConstructor
@Builder
public class UserSearchResponseDto {
    @Schema(example = "1", description = "회원 아이디")
    private Long userId;

    @Schema(example = "2019212001", description = "회원 학번")
    private String studentId;

    @Schema(example = "홍길동", description = "회원 이름")
    private String name;

    public static UserSearchResponseDto fromEntity(final User user){
        return UserSearchResponseDto.builder()
                .userId(user.getId())
                .studentId(user.getStudentId())
                .name(user.getName())
                .build();
    }
}
