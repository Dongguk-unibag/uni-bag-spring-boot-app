package org.uni_bag.uni_bag_spring_boot_app.controller.follow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyPrimaryTimeTableUpdateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.follow.FollowService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
@Tag(name = "팔로우")
public class FollowController {
    private final FollowService followService;

    @Operation(summary = "팔로우 한 친구들 조회")
    @JwtTokenErrorExample
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FolloweeListReadResponseDto.class)))
    @GetMapping()
    public ResponseEntity<FolloweeListReadResponseDto> getFolloweeList(@AuthenticationPrincipal User user) {
        FolloweeListReadResponseDto responseDto = followService.getFolloweeList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @Operation(summary = "팔로우")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError, description = "팔로우 할 회원을 찾을 수 없을 경우 발생"),
            @ApiErrorCodeExample(value = HttpErrorCode.AlreadyExistFollowError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = FollowResponseDto.class)))
    @PostMapping("/follow")
    public ResponseEntity<FollowResponseDto> follow(@AuthenticationPrincipal User user,
                                                    @Valid @RequestBody FollowRequestDto followRequestDto) {
        FollowResponseDto responseDto = followService.follow(user, followRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "언팔로우")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError, description = "언팔로우 할 회원을 찾을 수 없을 경우 발생"),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchFollowError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = UnfollowResponseDto.class)))
    @PostMapping("/unfollow")
    public ResponseEntity<UnfollowResponseDto> unfollow(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody UnfollowRequestDto unfollowRequestDto) {
        UnfollowResponseDto responseDto = followService.unfollow(user, unfollowRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Secondary 친구 등록")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoPrimaryTimeTableError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyPrimaryTimeTableUpdateResponseDto.class)))
    @PutMapping("/secondary/{friendId}")
    public ResponseEntity<SecondaryFriendUpdateResponseDto> updateSecondaryFriend(@AuthenticationPrincipal User user,
                                                                                  @Parameter(example = "1", required = true, description = "secondary로 등록할 친구 아이디") @PathVariable Long friendId) {
        SecondaryFriendUpdateResponseDto responseDto = followService.updateSecondaryFriend(user, friendId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    @Operation(summary = "Secondary 친구 삭제")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoPrimaryTimeTableError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyPrimaryTimeTableUpdateResponseDto.class)))
    @DeleteMapping("/secondary")
    public ResponseEntity<SecondaryFriendUpdateResponseDto> deleteSecondaryFriend(@AuthenticationPrincipal User user) {
        SecondaryFriendUpdateResponseDto responseDto = followService.deleteSecondaryFriend(user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Secondary 친구 조회")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSecondaryFriendError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = SecondaryFriendReadResponseDto.class)))
    @GetMapping("/secondary")
    public ResponseEntity<SecondaryFriendReadResponseDto> getSecondaryFriend(@AuthenticationPrincipal User user) {
        SecondaryFriendReadResponseDto responseDto = followService.getSecondaryFriend(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
