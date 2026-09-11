package com.khan.EComm.ratelimit;

public class TokenBucket {

    private final int capacity;
    private final double refillRatePerMs;

    private double tokens;
    private long lastRefillTimestamp;

    public TokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
        this.tokens = capacity; // token bucket starts FULL
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens < 1) {
            return false;
        }

        tokens -= 1;
        return true;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsedMs = now - lastRefillTimestamp;

        double refillAmount = elapsedMs * refillRatePerMs;
        tokens = Math.min(capacity, tokens + refillAmount);

        lastRefillTimestamp = now;
    }
}
