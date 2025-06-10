package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;

@Getter
@AllArgsConstructor
@Builder
public class SecondaryFriendUpdateResponseDto {
    private Long friendId;
    private boolean isSecondaryFriend;

    public static SecondaryFriendUpdateResponseDto from(Follow follow){
        return SecondaryFriendUpdateResponseDto.builder()
                .friendId(follow.getFollowee().getId())
                .isSecondaryFriend(follow.isSecondaryFriend())
                .build();
    }
}
