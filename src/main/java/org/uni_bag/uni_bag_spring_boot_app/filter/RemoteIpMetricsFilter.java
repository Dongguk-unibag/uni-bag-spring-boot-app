//package org.uni_bag.uni_bag_spring_boot_app.filter;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tag;
//import io.micrometer.core.instrument.Timer;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class RemoteIpMetricsFilter extends OncePerRequestFilter {
//    private final MeterRegistry meterRegistry;
//
//    public RemoteIpMetricsFilter(MeterRegistry meterRegistry) {
//        this.meterRegistry = meterRegistry;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String remoteIp = request.getRemoteAddr(); // 클라이언트 IP 가져오기
//        String uri = request.getRequestURI(); // 요청 URI 가져오기
//
//        List<Tag> tags = Arrays.asList(
//                Tag.of("remote_ip", remoteIp),
//                Tag.of("uri", uri)
//        );
//
//        Timer timer = Timer.builder("http.server.requests") // Prometheus 기본 메트릭
//                .tags(tags) // remote_ip 및 uri 태그 추가
//                .register(meterRegistry);
//
//        Timer.Sample sample = Timer.start(meterRegistry);
//        try {
//            filterChain.doFilter(request, response);
//        } finally {
//            sample.stop(timer);
//        }
//    }
//}
