package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.annotation.SimulateLatency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for translating messages using a simulated external API.
 * Provides thread-safe caching to ensure idempotent behavior.
 */
@Service
@RequiredArgsConstructor
public class TranslationService {
    private final CacheService cacheService;

    /**
     * Translates a message using cached results for performance.
     * Uses caching to ensure idempotent results.
     *
     * @param message the message to translate
     * @return the translated message
     */
    public String translate(String message) {
        return cacheService.computeIfAbsent("translate<->" + message, (key) -> this.fetchTranslationFromApi(message));
    }

    /**
     * Simulates fetching translation from external API with realistic latency.
     * Currently, reverses the message as a simple translation simulation.
     *
     * @param message the message to translate
     * @return the translated message (reversed)
     */
    @SimulateLatency
    private String fetchTranslationFromApi(String message) {
        // Simulates translation by reversing the message
        if (!message.isEmpty()) {
            message = new StringBuilder(message).reverse().toString();
        }
        return message;
    }
}