package org.uni_bag.uni_bag_spring_boot_app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.uni_bag.uni_bag_spring_boot_app.config.CustomHttpRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        CustomHttpRequestWrapper requestWrapper = new CustomHttpRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        requestWrapper.getInputStream();

        boolean isShowingRequestLog = checkRequestLogShow(requestWrapper);

        if(isShowingRequestLog) {
            // 요청 로그 출력
            log.info("[REQUEST] {} {} | IP: {} | User-Agent: {} | RequestBody:{}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    getRequestBody(requestWrapper)
            );
        }

        // 필터 체인 호출
        filterChain.doFilter(requestWrapper, responseWrapper);

        long duration = System.currentTimeMillis() - startTime;

        boolean isShowingResponseLog = checkResponseLogShow(requestWrapper);

        if(isShowingResponseLog) {
            // 응답 로그 출력
            log.info("[RESPONSE] {} {} | Status: {} | Time Taken: {}ms | ResponseBody:{}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8)
            );
        }

        responseWrapper.copyBodyToResponse(); // 요청을 전달
    }

    private String getRequestBody(CustomHttpRequestWrapper requestWrapper) {
        byte[] content = requestWrapper.getRequestBody();
        if (content.length == 0) {
            return "";
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object json = objectMapper.readValue(content, Object.class);
            return objectMapper.writeValueAsString(json); // 한 줄 JSON 문자열 반환
        } catch (IOException e) {
            return new String(content, StandardCharsets.UTF_8);
        }
    }

    private boolean checkRequestLogShow(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/actuator");
    }

    private boolean checkResponseLogShow(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/actuator");
    }
}
