package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class FolloweeListReadResponseDto {
    List<Followee> followees;

    public static FolloweeListReadResponseDto of(List<Follow> follows){
        return FolloweeListReadResponseDto.builder()
                .followees(follows.stream().map(Followee::of).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class Followee{
    private Long followeeId;

    private String followeeName;

    public static Followee of(Follow follow){
        return Followee.builder()
                .followeeId(follow.getFollowee().getId())
                .followeeName(follow.getFollowee().getName())
                .build();
    }
}

