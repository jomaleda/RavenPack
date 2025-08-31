package com.jomaleda.ravenpack.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    @Test
    void computeIfAbsent_NewKey_CallsSupplier() {
        String key = "test-key";
        String expectedValue = "test-value";
        
        String result = cacheService.computeIfAbsent(key, k -> expectedValue);
        
        assertEquals(expectedValue, result);
    }

    @Test
    void computeIfAbsent_ExistingKey_ReturnsCachedValue() {
        String key = "cached-key";
        String firstValue = "first-value";
        String secondValue = "second-value";
        
        String result1 = cacheService.computeIfAbsent(key, k -> firstValue);
        String result2 = cacheService.computeIfAbsent(key, k -> secondValue);
        
        assertEquals(firstValue, result1);
        assertEquals(firstValue, result2);
    }

    @Test
    void computeIfAbsent_DifferentTypes_WorksCorrectly() {
        String stringResult = cacheService.computeIfAbsent("string-key", k -> "string-value");
        Float floatResult = cacheService.computeIfAbsent("float-key", k -> 1.5f);
        
        assertEquals("string-value", stringResult);
        assertEquals(1.5f, floatResult);
    }
}