package org.uni_bag.uni_bag_spring_boot_app.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.user.UserService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "회원 정보")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원 정보 조회")
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.AccessDeniedError),
            @ApiErrorCodeExample(value = HttpErrorCode.NotValidAccessTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.ExpiredAccessTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError)
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class)))
    @GetMapping()
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
        UserInfoDto userInfoDto = userService.getUserInfo(user);
        return new ResponseEntity<>(UserInfoResponseDto.fromDto(userInfoDto), HttpStatus.OK);
    }
}
