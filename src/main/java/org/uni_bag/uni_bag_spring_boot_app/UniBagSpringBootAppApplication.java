package org.uni_bag.uni_bag_spring_boot_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//@EnableWebSecurity(debug = true)
@SpringBootApplication
public class UniBagSpringBootAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniBagSpringBootAppApplication.class, args);
    }

}
