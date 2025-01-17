package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfollowRequestDto {
    @Schema(example = "1", description = "언팔로잉 할 회원 아이디")
    private Long unfolloweeId;
}
