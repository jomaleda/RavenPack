package com.jomaleda.ravenpack.interview.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserStatsTest {

    @Test
    void testInitialValues() {
        UserStats stats = new UserStats(0, 0.0);
        assertEquals(0, stats.getTotalMessages());
        assertEquals(0.0f, stats.getAverageScore());
    }

    @Test
    void testAddSingleMessage() {
        UserStats stats = new UserStats(0, 0.0);
        stats.addMessage(5.0);
        assertEquals(1, stats.getTotalMessages());
        assertEquals(5.0f, stats.getAverageScore());
    }

    @Test
    void testAddMultipleMessages() {
        UserStats stats = new UserStats(0, 0.0);
        stats.addMessage(2.0);
        stats.addMessage(4.0);
        stats.addMessage(6.0);
        assertEquals(3, stats.getTotalMessages());
        assertEquals(4.0f, stats.getAverageScore());
    }

    @Test
    void testAverageScoreWithZeroMessages() {
        UserStats stats = new UserStats(0, 0.0);
        assertEquals(0.0f, stats.getAverageScore());
    }

    @Test
    void testConstructorWithInitialValues() {
        UserStats stats = new UserStats(5, 25.0);
        assertEquals(5, stats.getTotalMessages());
        assertEquals(5.0f, stats.getAverageScore());
    }
}