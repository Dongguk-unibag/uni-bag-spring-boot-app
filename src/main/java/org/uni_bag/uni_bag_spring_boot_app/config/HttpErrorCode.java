package org.uni_bag.uni_bag_spring_boot_app.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HttpErrorCode {
    // ----- Common ------
    NotValidRequestError(
            HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."
    ),
    QueryParamTypeMismatchError(
            HttpStatus.BAD_REQUEST, "쿼리 파라미터의 타입이 올바르지 않습니다."
    ),
    MissingQueryParamError(
            HttpStatus.BAD_REQUEST, "파라미터의 값이 존재하지 않습니다."
    ),
    MissingRequestHeaderError(
            HttpStatus.BAD_REQUEST, "헤더의 값이 존재하지 않습니다."
    ),
    AccessDeniedError(
            HttpStatus.FORBIDDEN, "접근할 수 없는 권한을 가진 사용자입니다."
    ),
    InternalServerError(
            HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생하였습니다. 문제가 지속되면 관리자에게 문의하세요."
    ),

    // ----- User ------
    DuplicatedNicknameError(
            HttpStatus.CONFLICT, "중복된 닉네임입니다"
    ),
    UserNotFoundError(
            HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."
    ),
    UserPermissionDeniedError(
            HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."
    ),
    AlreadyExistUserError(
            HttpStatus.CONFLICT, "이미 존재하는 유저입니다."
    ),

    // ----- OAuth ------
    UnauthorizedKakaoError(
            HttpStatus.UNAUTHORIZED, "카카오를 통한 인증에 실패하였습니다."
    ),

    ForbiddenKakaoError(
            HttpStatus.FORBIDDEN, "허가되지 않은 카카오 접근입니다."
    ),
    UnauthorizedNaverError(
            HttpStatus.UNAUTHORIZED, "네이버를 통한 인증에 실패하였습니다."
    ),

    ForbiddenNaverError(
            HttpStatus.FORBIDDEN, "허가되지 않은 네이버 접근입니다."
    ),

    // ----- Token ------
    NotValidTokenError(
            HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."
    ),
    NotValidAccessTokenError(
            HttpStatus.UNAUTHORIZED, "유효하지 않은 AccessToken입니다."
    ),
    NotExpiredAccessTokenError(
            HttpStatus.UNAUTHORIZED, "만료되지 않은 AccessToken입니다."
    ),
    ExpiredAccessTokenError(
            HttpStatus.UNAUTHORIZED, "만료된 AccessToken입니다."
    ),
    NoSuchAccessTokenError(
            HttpStatus.UNAUTHORIZED, "존재하지 않은 AccessToken입니다."
    ),
    NotValidRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken입니다."
    ),
    NotExpiredRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "만료되지 않은 RefreshToken입니다."
    ),
    ExpiredRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "만료된 RefreshToken입니다."
    ),
    NoSuchRefreshTokenError(
            HttpStatus.UNAUTHORIZED, "존재하지 않은 RefreshToken입니다."
    );

    private final HttpStatus httpStatus;
    private final String message;

    HttpErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}