package org.uni_bag.uni_bag_spring_boot_app.exception;

import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;

@Getter
public class HttpErrorException extends RuntimeException{
    private final HttpErrorCode httpErrorCode;

    public HttpErrorException(HttpErrorCode httpErrorCode){
        super(httpErrorCode.getMessage());
        this.httpErrorCode = httpErrorCode;
    }
}

