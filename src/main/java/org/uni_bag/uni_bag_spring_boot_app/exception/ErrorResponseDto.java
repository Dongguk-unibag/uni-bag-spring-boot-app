package org.uni_bag.uni_bag_spring_boot_app.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    @JsonView(CustomJsonView.Summary.class)
    private String errorCode;
    @JsonView(CustomJsonView.Summary.class)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView(CustomJsonView.Entire.class)
    private List<ErrorDescription> errorDescriptions;

    public static ErrorResponseDto of(List<ErrorDescription> errorDescription){
        return ErrorResponseDto.builder()
                .errorCode(HttpErrorCode.NotValidRequestError.name())
                .message(HttpErrorCode.NotValidRequestError.getMessage())
                .errorDescriptions(errorDescription)
                .build();
    }

    public static ErrorResponseDto of(BindException e, List<ErrorDescription> errorDescription){
        return ErrorResponseDto.builder()
                .errorCode(HttpErrorCode.NotValidRequestError.name())
                .message(HttpErrorCode.NotValidRequestError.getMessage())
                .errorDescriptions(errorDescription)
                .build();
    }

    public static ErrorResponseDto from(HttpErrorCode httpErrorCode){
        return ErrorResponseDto.builder()
                .errorCode(httpErrorCode.name())
                .message(httpErrorCode.getMessage())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ErrorDescription{
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonView(CustomJsonView.Entire.class)
        private String message;

        public static ErrorDescription of(String message){
            return ErrorDescription.builder()
                    .message(message)
                    .build();
        }


        public static ErrorDescription of(FieldError error){
            return ErrorDescription.builder()
                    .message(error.getDefaultMessage())
                    .build();
        }
    }


}


//package org.uni_bag.uni_bag_spring_boot_app.exception;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@Builder
//@Schema(title = "API 응답 - 실패 및 에러")
//public class ErrorResponseDto {
//    @Schema(description = "응답 코드", example = "400")
//    private String errorCode;
//
//    @Schema(description = "응답 메시지", example = "에러 이유")
//    private String message;
//
//    public static ErrorResponseDto fromException(HttpErrorException e){
//        return ErrorResponseDto.builder()
//                .errorCode(e.getHttpErrorCode().name())
//                .message(e.getMessage())
//                .build();
//    }
//
//    public static ErrorResponseDto from(HttpErrorCode httpErrorCode){
//        return ErrorResponseDto.builder()
//                .errorCode(httpErrorCode.name())
//                .message(httpErrorCode.getMessage())
//                .build();
//    }
//
//    public static ErrorResponseDto of(String errorCode, String message){
//        return ErrorResponseDto.builder()
//                .errorCode(errorCode)
//                .message(message)
//                .build();
//    }
//}
