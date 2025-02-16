package org.uni_bag.uni_bag_spring_boot_app.controller.user;

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
import org.uni_bag.uni_bag_spring_boot_app.dto.user.*;
import org.uni_bag.uni_bag_spring_boot_app.service.user.UserService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "회원 정보 관리")
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

    @Operation(summary = "회원 검색")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError, description = "학번과 이름이 매칭되는 유저 정보를 찾을 없는 경우 발생")
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserSearchResponseDto.class)))
    @GetMapping("/search")
    public ResponseEntity<UserSearchResponseDto> searchUser(@AuthenticationPrincipal User user,
                                                            @Parameter(description = "회원 이름", required = true, example = "홍길동") @RequestParam String name,
                                                            @Parameter(description = "회원 학번", required = true, example = "2019212001") @RequestParam String studentId) {
        UserSearchResponseDto responseDto = userService.searchUser(user, name, studentId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "이용약관 동의")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.AlreadyAgreeTosError)
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserTosAgreementResponseDto.class)))
    @PostMapping("/tos/agreement")
    public ResponseEntity<UserTosAgreementResponseDto> agreeTos(@AuthenticationPrincipal User user) {
        UserTosAgreementResponseDto responseDto = userService.agreeTos(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "TOS 철회 및 EMS 정보 삭제")
    @JwtTokenErrorExample
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserTosRescissionResponseDto.class)))
    @PostMapping("/tos/rescission")
    public ResponseEntity<UserTosRescissionResponseDto> rescindTos(@AuthenticationPrincipal User user) {
        UserTosRescissionResponseDto responseDto = userService.rescindTos(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "EMS 로그인 완료")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.AlreadyEmsLoginError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = UserEmsLoginCompleteResponseDto.class)))
    @PostMapping("/emsLogin/complete")
    public ResponseEntity<UserEmsLoginCompleteResponseDto> completeEmsLogin(@AuthenticationPrincipal User user,
                                                                            @Valid @RequestBody UserEmsLoginCompleteRequestDto requestDto) {
        UserEmsLoginCompleteResponseDto responseDto = userService.completeEmsLogin(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
