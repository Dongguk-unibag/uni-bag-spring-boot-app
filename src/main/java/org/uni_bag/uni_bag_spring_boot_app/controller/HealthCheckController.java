package org.uni_bag.uni_bag_spring_boot_app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/server")
public class HealthCheckController {
    @Value("${server.env}")
    private String env;

    @GetMapping("/env")
    public String getEnv() {
        return env;
    }
}
