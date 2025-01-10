package org.uni_bag.uni_bag_spring_boot_app.exception;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;

import java.util.List;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ApiExceptionHandler {
    @ExceptionHandler(HttpErrorException.class)
    @JsonView(CustomJsonView.Summary.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomErrorException(HttpErrorException e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.from(e.getHttpErrorCode());
        return new ResponseEntity<>(errorResponseDto, e.getHttpErrorCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @JsonView(CustomJsonView.Entire.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponseDto.ErrorDescription errorDescription
                = ErrorResponseDto.ErrorDescription.of(HttpErrorCode.QueryParamTypeMismatchError.getMessage());
        List<ErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        ErrorResponseDto notValidRequestErrorResponseDto
                = ErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @JsonView(CustomJsonView.Entire.class)
    protected ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorResponseDto.ErrorDescription errorDescription
                = ErrorResponseDto.ErrorDescription.of(HttpErrorCode.MissingQueryParamError.getMessage());
        List<ErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        ErrorResponseDto notValidRequestErrorResponseDto
                = ErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    @JsonView(CustomJsonView.Summary.class)
    protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.from(HttpErrorCode.AccessDeniedError);

        return new ResponseEntity<>(errorResponseDto, HttpErrorCode.AccessDeniedError.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    @JsonView(CustomJsonView.Entire.class)
    protected ResponseEntity<ErrorResponseDto> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<ErrorResponseDto.ErrorDescription> errorDescriptions =
                fieldErrors.stream().map(ErrorResponseDto.ErrorDescription::of).toList();

        ErrorResponseDto notValidRequestErrorResponseDto =
                ErrorResponseDto.of(e, errorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @JsonView(CustomJsonView.Summary.class)
    public ResponseEntity<ErrorResponseDto> handleMissingHeader(MissingRequestHeaderException e) {
        HttpErrorCode missingRequestHeaderError = HttpErrorCode.MissingRequestHeaderError;
        return new ResponseEntity<>(ErrorResponseDto.from(missingRequestHeaderError), missingRequestHeaderError.getHttpStatus());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @JsonView(CustomJsonView.Summary.class)
    protected ResponseEntity<ErrorResponseDto> HandleInternalAuthenticationServiceException(Exception e) {
        return new ResponseEntity<>(ErrorResponseDto.from(HttpErrorCode.InternalServerError), HttpErrorCode.InternalServerError.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    @JsonView(CustomJsonView.Summary.class)
    protected ResponseEntity<ErrorResponseDto> HandleGeneralException(Exception e) {
        return new ResponseEntity<>(ErrorResponseDto.from(HttpErrorCode.InternalServerError), HttpErrorCode.InternalServerError.getHttpStatus());
    }
}
