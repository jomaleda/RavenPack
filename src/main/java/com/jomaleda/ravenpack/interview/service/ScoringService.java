package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.annotation.SimulateLatency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for scoring messages using a simulated external API.
 * Provides thread-safe caching to ensure idempotent behavior.
 */
@Service
@RequiredArgsConstructor
public class ScoringService {
    private final CacheService cacheService;

    /**
     * Gets the offensive content score for a message.
     * Uses caching to ensure idempotent results.
     *
     * @param message the message to score
     * @return score between 0.0 and 1.0
     */
    public float getScore(String message) {
        return cacheService.computeIfAbsent(message, this::fetchScoreFromApi);
    }

    /**
     * Simulates fetching score from external API with realistic latency.
     *
     * @param message the message to score
     * @return deterministic score based on message hash
     */
    @SimulateLatency
    private float fetchScoreFromApi(String message) {
        // Calculates random value to be assigned as
        // score for the input message
        return (float) (Math.abs(message.hashCode() % 1001)) / 1000.0f;
    }
}