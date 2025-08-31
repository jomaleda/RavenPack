package com.jomaleda.ravenpack.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceTest {

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();
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
        
        long start1 = System.currentTimeMillis();
        scoringService.getScore(message);
        long duration1 = System.currentTimeMillis() - start1;
        
        long start2 = System.currentTimeMillis();
        scoringService.getScore(message);
        long duration2 = System.currentTimeMillis() - start2;
        
        assertTrue(duration2 < duration1);
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