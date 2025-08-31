package com.jomaleda.ravenpack.interview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserStats {
    @Getter
    private int totalMessages;
    private double scoreSum;

    public synchronized void addMessage(double score) {
        this.totalMessages++;
        this.scoreSum += score;
    }

    public float getAverageScore() {
        if (totalMessages == 0) {
            return 0.0f;
        }
        return (float) (scoreSum / totalMessages);
    }
}
