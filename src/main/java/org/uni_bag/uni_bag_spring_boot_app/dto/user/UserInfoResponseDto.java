package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponseDto {
    private String snsId;

    private SnsType snsType;

    private String studentId;

    private String name;

    private boolean isTosAccepted;

    private boolean isEmsLoggedIn;

    public static UserInfoResponseDto fromDto(UserInfoDto dto) {
        return UserInfoResponseDto.builder()
                .snsId(dto.getSnsId())
                .snsType(dto.getSnsType())
                .studentId(dto.getStudentId())
                .name(dto.getName())
                .isTosAccepted(dto.isTosAccepted())
                .isEmsLoggedIn(dto.isEmsLoggedIn())
                .build();
    }
}
