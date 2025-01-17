package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

@Getter
@AllArgsConstructor
@Builder
public class UnfollowResponseDto {
    @Schema(example = "1", description = "언팔로잉 한 회원 아이디")
    private Long unfolloweeId;

    @Schema(example = "홍길동", description = "언팔로잉 한 회원 이름")
    private String unfolloweeName;

    public static UnfollowResponseDto of(User followee){
        return UnfollowResponseDto.builder()
                .unfolloweeId(followee.getId())
                .unfolloweeName(followee.getName())
                .build();

    }
}
