package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

@Getter
@AllArgsConstructor
@Builder
public class SecondaryFriendReadResponseDto {
    @Schema(example = "1", description = "친구 아이디")
    private Long friendId;

    @Schema(example = "홍길동", description = "친구 이름")
    private String friendName;

    public static SecondaryFriendReadResponseDto fromEntity(User friend){
        return SecondaryFriendReadResponseDto.builder()
                .friendId(friend.getId())
                .friendName(friend.getName())
                .build();
    }
}
