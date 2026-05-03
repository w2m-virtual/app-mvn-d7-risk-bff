package com.w2m.virtual.risk.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.w2m.virtual.risk")
public class RiskBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskBffApplication.class, args);
    }
}
