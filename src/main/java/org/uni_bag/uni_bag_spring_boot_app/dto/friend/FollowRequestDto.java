package org.uni_bag.uni_bag_spring_boot_app.dto.friend;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequestDto {
    private Long followee;
}
