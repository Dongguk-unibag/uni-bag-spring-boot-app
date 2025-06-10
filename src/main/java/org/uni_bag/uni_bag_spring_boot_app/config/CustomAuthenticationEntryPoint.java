package org.uni_bag.uni_bag_spring_boot_app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.uni_bag.uni_bag_spring_boot_app.exception.ErrorResponseDto;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        HttpErrorCode httpErrorCode = HttpErrorCode.AccessDeniedError;
        response.setStatus(httpErrorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = new ObjectMapper().writeValueAsString(ErrorResponseDto.from(httpErrorCode));
        response.getWriter().write(json);
    }
}