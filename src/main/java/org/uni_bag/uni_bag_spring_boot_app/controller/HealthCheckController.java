package org.uni_bag.uni_bag_spring_boot_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/server")
@Tag(name = "Health Check")
public class HealthCheckController {
    @Value("${server.env}")
    private String env;

    @Operation(summary = "blue/green 조회")
    @GetMapping("/env")
    public String getEnv() {
        return env;
    }
}
