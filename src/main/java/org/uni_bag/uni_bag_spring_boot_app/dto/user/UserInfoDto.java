package org.uni_bag.uni_bag_spring_boot_app.dto.user;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoDto {
    private String snsId;

    private SnsType snsType;

    private String studentId;

    private String name;

    private boolean isTosAccepted;

    private boolean isEmsLoggedIn;

    public static UserInfoDto fromEntity(User user) {
        return UserInfoDto.builder()
                .snsId(user.getSnsId())
                .snsType(user.getSnsType())
                .studentId(user.getStudentId())
                .name(user.getName())
                .isTosAccepted(user.isTosAccepted())
                .isEmsLoggedIn(user.isEmsLoggedIn())
                .build();
    }
}
