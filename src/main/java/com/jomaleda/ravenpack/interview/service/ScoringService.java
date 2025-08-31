package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.annotation.SimulateLatency;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

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
    @SimulateLatency
    private float fetchScoreFromApi(String message) {
        // Calculates random value to be assigned as
        // score for the input message
        return (float) (Math.abs(message.hashCode() % 1001)) / 1000.0f;
    }
}