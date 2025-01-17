package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

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
    private Long followeeId;

    private String followeeName;

    public static FollowResponseDto of(User followee){
        return FollowResponseDto.builder()
                .followeeId(followee.getId())
                .followeeName(followee.getName())
                .build();
    }
}
