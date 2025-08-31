package com.jomaleda.ravenpack.interview.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service for scoring messages using a simulated external API.
 * Provides thread-safe caching to ensure idempotent behavior.
 */
@Service
public class ScoringService {
    // Creates a dictionary in memory for keeping the pairs of <message, score> stored
    // Avoid calculating new message for existing messages in cache
    // NOTE: This cache mechanism should be replaced for a caching system like Redis for production execution
    private final ConcurrentHashMap<String, Float> scoreCache = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Gets the offensive content score for a message.
     * Uses caching to ensure idempotent results.
     *
     * @param message the message to score
     * @return score between 0.0 and 1.0
     */
    public float getScore(String message) {
        return scoreCache.computeIfAbsent(message, this::fetchScoreFromApi);
    }

    /**
     * Simulates fetching score from external API with realistic latency.
     *
     * @param message the message to score
     * @return deterministic score based on message hash
     */
    private float fetchScoreFromApi(String message) {
        try {
            // Simulates latency of 50ms to 200ms
            long latency = 50 + secureRandom.nextInt(151);
            TimeUnit.MILLISECONDS.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scoring service call was interrupted", e);
        }
        // Calculates random value to be assigned as
        // score for the input message
        return (float) (Math.abs(message.hashCode() % 1001)) / 1000.0f;
    }
}