package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;

import java.util.List;

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
    @Schema(example = "1", description = "팔로우한 사람 아이디")
    private Long followeeId;

    @Schema(example = "홍길동", description = "팔로우한 사람 이름")
    private String followeeName;

    public static Followee of(Follow follow){
        return Followee.builder()
                .followeeId(follow.getFollowee().getId())
                .followeeName(follow.getFollowee().getName())
                .build();
    }
}

