package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

@Getter
@AllArgsConstructor
@Builder
public class UnfollowResponseDto {
    private Long followeeId;

    private String followeeName;

    public static UnfollowResponseDto of(User followee){
        return UnfollowResponseDto.builder()
                .followeeId(followee.getId())
                .followeeName(followee.getName())
                .build();

    }
}
