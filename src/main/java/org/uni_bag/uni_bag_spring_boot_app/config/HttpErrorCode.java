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

    BadRequestAppleError(
            HttpStatus.BAD_REQUEST, "애플 로그인 인증에 필요한 정보가 잘못되었습니다. 정확한 정보를 확인해주세요."
    ),

    BadRequestKakaoError(
            HttpStatus.BAD_REQUEST, "카카오 로그인 인증에 필요한 정보가 잘못되었습니다. 정확한 정보를 확인해주세요."
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
    ),

    // TimeTable
    AlreadyExistSeasonTable(
            HttpStatus.CONFLICT, "해당 년도와 학기의 시간표가 이미 존재합니다."
    ),
    NoSuchTimeTableError(
            HttpStatus.NOT_FOUND, "시간표가 존재하지 않습니다."
    ),
    NoSuchLectureError(
            HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다"
    ),
    SemesterMismatchException(
            HttpStatus.CONFLICT, "선택한 강의와 시간표의 학기가 일치하지 않습니다."
    ),
    OverLappingLectureError(
            HttpStatus.CONFLICT, "추가할 강의가 시간표에 저장된 강의와 겹칩니다."
    ),
    AlreadyExistLectureScheduleError(
            HttpStatus.CONFLICT, "이미 존재하는 강의 스케줄입니다."
    ),
    NoSuchTimeTableScheduleError(
            HttpStatus.NOT_FOUND, "시간표 내에 강의 스케줄이 존재하지 않습니다."
    ),
    AlreadyPrimaryTimeTableError(
            HttpStatus.CONFLICT, "현재 시간표는 이미 primary 시간표입니다."
    ),
    NoPrimaryTimeTableError(
            HttpStatus.NOT_FOUND, "사용자가 설정한 primary 시간표가 없습니다."
    ),


    // Friend
    AlreadyExistFollowError(
            HttpStatus.CONFLICT, "이미 팔로우 관계입니다."
    ),
    NoSuchFollowError(
            HttpStatus.NOT_FOUND, "존재하지 않은 팔로우 관계입니다."
    ),
    AlreadySecondaryFriendError(
            HttpStatus.CONFLICT, "이미 secondary 친구입니다."
    ),
    NoSecondaryFriendError(
            HttpStatus.NOT_FOUND, "secondary 친구가 없습니다."
    ),

    // Assignment
    NotValidAssignmentTimeError(
            HttpStatus.BAD_REQUEST, "과제에 할당된 시간이 올바르지 않습니다."
    ),
    NoSuchAssignmentError(
            HttpStatus.NOT_FOUND, "존재하지 않은 과제입니다."
    ),

    // TOS
    AlreadyAgreeTosError(
            HttpStatus.CONFLICT, "이미 이용약관을 동의하였습니다."
    ),
    AlreadyEmsLoginError(
            HttpStatus.CONFLICT, "이미 사용자가 EMS 서버에 로그인하였습니다."
    )
    ;

    private final HttpStatus httpStatus;
    private final String message;

    HttpErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
