package com.dental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dental"})
public class DentalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DentalSystemApplication.class, args);
    }
}
