package org.bravatools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class BravaToolsBackend {
    public static void main(String[] args) {
        SpringApplication.run(BravaToolsBackend.class, args);
    }
}