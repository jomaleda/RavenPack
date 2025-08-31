package com.jomaleda.ravenpack.interview.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class AppConfigTest {

    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
    }

    @Test
    void taskExecutor_ReturnsExecutorService() {
        ExecutorService executor = appConfig.taskExecutor();
        
        assertNotNull(executor);
        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());
        
        executor.shutdown();
    }

    @Test
    void taskExecutor_SupportsTaskExecution() {
        ExecutorService executor = appConfig.taskExecutor();
        
        assertDoesNotThrow(() -> {
            executor.submit(() -> "test task");
        });
        
        executor.shutdown();
    }

    @Test
    void taskExecutor_MultipleCallsReturnDifferentInstances() {
        ExecutorService executor1 = appConfig.taskExecutor();
        ExecutorService executor2 = appConfig.taskExecutor();
        
        assertNotSame(executor1, executor2);
        
        executor1.shutdown();
        executor2.shutdown();
    }
}