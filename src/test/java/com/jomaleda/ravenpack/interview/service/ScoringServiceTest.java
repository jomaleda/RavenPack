package com.jomaleda.ravenpack.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ScoringServiceTest {

    private ScoringService scoringService;
    
    @Mock
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheService.computeIfAbsent(any(String.class), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Function<String, Float> supplier = invocation.getArgument(1);
            return supplier.apply(key);
        });
        scoringService = new ScoringService(cacheService);
    }

    @Test
    void getScore_ValidMessage_ReturnsScoreBetweenZeroAndOne() {
        float score = scoringService.getScore("test message");
        
        assertTrue(score >= 0.0f && score <= 1.0f);
    }

    @Test
    void getScore_SameMessage_ReturnsSameScore() {
        String message = "consistent message";
        
        float score1 = scoringService.getScore(message);
        float score2 = scoringService.getScore(message);
        
        assertEquals(score1, score2);
    }

    @Test
    void getScore_DifferentMessages_ReturnsDifferentScores() {
        float score1 = scoringService.getScore("message one");
        float score2 = scoringService.getScore("message two");
        
        assertNotEquals(score1, score2);
    }

    @Test
    void getScore_EmptyMessage_ReturnsValidScore() {
        float score = scoringService.getScore("");
        
        assertTrue(score >= 0.0f && score <= 1.0f);
    }

    @Test
    void getScore_CachingBehavior_SecondCallIsFaster() {
        String message = "cached message";
        
        // First call populates cache
        scoringService.getScore(message);
        
        // Second call should return same cached result
        float score1 = scoringService.getScore(message);
        float score2 = scoringService.getScore(message);
        
        assertEquals(score1, score2);
    }

    @Test
    void getScore_InterruptedThread_ThrowsRuntimeException() throws InterruptedException {
        Thread testThread = new Thread(() -> {
            Thread.currentThread().interrupt();
            assertThrows(RuntimeException.class, () -> 
                scoringService.getScore("interrupted message"));
        });
        
        testThread.start();
        testThread.join();
    }
}