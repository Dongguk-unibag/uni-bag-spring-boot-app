package org.uni_bag.uni_bag_spring_boot_app.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.login.LoginResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.logout.LogoutResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue.TokenReIssueDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.tokenReissue.TokenReIssueResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.auth.AuthService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인")
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.ForbiddenKakaoError),
            @ApiErrorCodeExample(value = HttpErrorCode.ForbiddenNaverError),
            @ApiErrorCodeExample(value = HttpErrorCode.UnauthorizedKakaoError),
            @ApiErrorCodeExample(value = HttpErrorCode.UnauthorizedNaverError),
            @ApiErrorCodeExample(value = HttpErrorCode.UnauthorizedAppleError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginDto dto = authService.login(requestDto);
        return new ResponseEntity<>(LoginResponseDto.fromDto(dto), HttpStatus.CREATED);
    }



    @Operation(summary = "로그아웃")
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NotValidAccessTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchRefreshTokenError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = LogoutResponseDto.class)))
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(
            @Schema(example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3...") @RequestHeader("Authorization-refresh")
            String refreshToken
    ) {
        authService.logout(refreshToken);
        return new ResponseEntity<>(LogoutResponseDto.success(), HttpStatus.CREATED);
    }



    @Operation(summary = "토큰 재발급")
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NotValidTokenError, description = "토큰이 유효하지 않을 경우 발생하는 에러입니다."),
            @ApiErrorCodeExample(value = HttpErrorCode.NotValidAccessTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchRefreshTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.ExpiredRefreshTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchAccessTokenError),
            @ApiErrorCodeExample(value = HttpErrorCode.UserNotFoundError, description = "AccessToken의 Payload에서 유저 정보를 추출할 수 없을 경우 발생하는 에러입니다.")
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = TokenReIssueResponseDto.class)))
    @PostMapping("/refreshToken")
    public ResponseEntity<TokenReIssueResponseDto> reIssueToken(
            @Schema(example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIy...") @RequestHeader("Authorization")
            String accessToken,
            @Schema(example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3...") @RequestHeader("Authorization-refresh")
            String refreshToken
    ){
        TokenReIssueDto dto = authService.reIssueToken(accessToken, refreshToken);
        return new ResponseEntity<>(TokenReIssueResponseDto.fromDto(dto), HttpStatus.CREATED);
    }
}
