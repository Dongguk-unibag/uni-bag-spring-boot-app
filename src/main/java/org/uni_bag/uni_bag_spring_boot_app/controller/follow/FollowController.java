package org.uni_bag.uni_bag_spring_boot_app.controller.follow;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.*;
import org.uni_bag.uni_bag_spring_boot_app.service.follow.FollowService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FollowController {
    private final FollowService followService;

    @GetMapping()
    public ResponseEntity<FolloweeListReadResponseDto> getFolloweeList(@AuthenticationPrincipal User user){
        FolloweeListReadResponseDto responseDto = followService.getFolloweeList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/follow")
    public ResponseEntity<FollowResponseDto> follow(@AuthenticationPrincipal User user,
                                                    @Valid @RequestBody FollowRequestDto followRequestDto){
        FollowResponseDto responseDto = followService.follow(user, followRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<UnfollowResponseDto> unfollow(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody UnfollowRequestDto unfollowRequestDto){
        UnfollowResponseDto responseDto = followService.unfollow(user, unfollowRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
