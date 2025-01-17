package org.uni_bag.uni_bag_spring_boot_app.controller.friendTimeTable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable.FriendTimeTableService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend/timeTable")
@Tag(name = "친구 시간표 정보 조회")
public class FriendTimeTableController {
    private final FriendTimeTableService friendTimeTableService;

    @Operation(summary = "친구 시간표 리스트 조회")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError, description = "친구 아이디에 해당하는 회원을 찾을 수 없을 경우 발생"),
            @ApiErrorCodeExample(value = HttpErrorCode.AccessDeniedError, description = "서로 친구 관계가 아닐 경우 발생"),
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FriendTimeTableListReadResponseDto.class)))
    @GetMapping()
    public ResponseEntity<FriendTimeTableListReadResponseDto> getFriendTimeTableList(@AuthenticationPrincipal User user,
                                                                                     @Parameter(description = "친구 아이디", required = true, example = "1") @RequestParam Long friendId) {
        FriendTimeTableListReadResponseDto responseDto = friendTimeTableService.getFriendTimeTableList(user, friendId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "친구 특정 시간표 조회")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.AccessDeniedError, description = "서로 친구 관계가 아닐 경우 발생"),
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FriendTimeTableListReadResponseDto.class)))
    @GetMapping("/{timeTableId}")
    public ResponseEntity<FriendTimeTableReadResponseDto> getFriendTimeTable(@AuthenticationPrincipal User user,
                                                                             @Parameter(description = "친구 시간표 아이디", required = true, example = "1")  @PathVariable Long timeTableId) {
        FriendTimeTableReadResponseDto responseDto = friendTimeTableService.getFriendTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
