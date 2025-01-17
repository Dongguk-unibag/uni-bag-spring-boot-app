package org.uni_bag.uni_bag_spring_boot_app.dto.user;

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
    private Long userId;
    private String studentId;
    private String name;

    public static UserSearchResponseDto fromEntity(final User user){
        return UserSearchResponseDto.builder()
                .userId(user.getId())
                .studentId(user.getStudentId())
                .name(user.getName())
                .build();
    }
}
