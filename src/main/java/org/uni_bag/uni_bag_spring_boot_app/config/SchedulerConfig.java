package org.uni_bag.uni_bag_spring_boot_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class SchedulerConfig {

    @Bean
    public Map<String, ScheduledFuture<?>> scheduledTasks() {
        return new ConcurrentHashMap<>();
    }
}
