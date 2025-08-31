package com.jomaleda.ravenpack.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TranslationServiceTest {

    private TranslationService translationService;
    
    @Mock
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheService.computeIfAbsent(any(String.class), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Function<String, String> supplier = invocation.getArgument(1);
            return supplier.apply(key);
        });
        translationService = new TranslationService(cacheService);
    }

    @Test
    void translate_ValidMessage_ReturnsReversedMessage() {
        String message = "hello";
        String expected = "olleh";
        
        String result = translationService.translate(message);
        
        assertEquals(expected, result);
    }

    @Test
    void translate_SameMessage_ReturnsSameResult() {
        String message = "consistent";
        
        String result1 = translationService.translate(message);
        String result2 = translationService.translate(message);
        
        assertEquals(result1, result2);
        assertEquals("tnetsisnoc", result1);
    }

    @Test
    void translate_EmptyMessage_ReturnsEmptyString() {
        String result = translationService.translate("");
        
        assertEquals("", result);
    }

    @Test
    void translate_SingleCharacter_ReturnsSameCharacter() {
        String message = "a";
        
        String result = translationService.translate(message);
        
        assertEquals("a", result);
    }

    @Test
    void translate_MultipleWords_ReversesEntireString() {
        String message = "hello world";
        String expected = "dlrow olleh";
        
        String result = translationService.translate(message);
        
        assertEquals(expected, result);
    }

    @Test
    void translate_SpecialCharacters_ReversesCorrectly() {
        String message = "test!@#";
        String expected = "#@!tset";
        
        String result = translationService.translate(message);
        
        assertEquals(expected, result);
    }
}