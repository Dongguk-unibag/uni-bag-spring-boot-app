package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnfollowRequestDto {
    @Schema(example = "1", description = "언팔로잉 할 회원 아이디")
    private Long unfolloweeId;
}
