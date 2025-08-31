package com.jomaleda.ravenpack.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application configuration class for Spring Boot.
 * Configures beans and application-wide settings.
 */
@Configuration
public class AppConfig {
    
    /**
     * Creates an ExecutorService using Java 21 virtual threads.
     * Virtual threads provide high concurrency with low resource overhead.
     *
     * @return ExecutorService configured for virtual thread execution
     */
    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}