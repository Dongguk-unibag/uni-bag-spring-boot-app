package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.bridge.IMessage;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

@Getter
@AllArgsConstructor
@Builder
public class FollowResponseDto {
    @Schema(example = "1", description = "팔로잉 한 회원 아이디")
    private Long followeeId;

    @Schema(example = "홍길동", description = "팔로잉 한 회원 이름")
    private String followeeName;

    public static FollowResponseDto of(User followee){
        return FollowResponseDto.builder()
                .followeeId(followee.getId())
                .followeeName(followee.getName())
                .build();
    }
}
