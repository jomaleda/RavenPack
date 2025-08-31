package com.jomaleda.ravenpack.interview.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Generic cache service for storing key-value pairs in memory.
 * Provides thread-safe caching operations using ConcurrentHashMap.
 * Used by services like ScoringService and TranslationService to avoid code duplication.
 */
@Service
public class CacheService {
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * Computes a value for the given key if not already present in cache.
     * Thread-safe operation that ensures the supplier is called only once per key.
     *
     * @param <T> the type of the cached value
     * @param key the cache key
     * @param supplier function to compute the value if key is absent
     * @return the cached or computed value
     */
    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(String key, Function<String, T> supplier) {
        return (T) cache.computeIfAbsent(key, supplier);
    }
}