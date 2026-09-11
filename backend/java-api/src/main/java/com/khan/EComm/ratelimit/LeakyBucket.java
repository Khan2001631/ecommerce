package com.khan.EComm.ratelimit;

public class LeakyBucket {

    private final int capacity;           // max requests allowed (Maximum number of pending requests the bucket can hold)
    private final double leakRatePerMs;    // requests leaked per millisecond (How fast requests are processed (removed from the bucket))

    private double currentLevel;           // current water level (number of pending requests currently in the bucket)
    private long lastLeakTimestamp;        // The last time we calculated how much the bucket leaked

    public LeakyBucket(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerMs = leakRatePerSecond / 1000.0;  // (This is important because time is measured in milliseconds)
        this.currentLevel = 0;
        this.lastLeakTimestamp = System.currentTimeMillis();
    }

    /**
     * Attempts to consume 1 token from the bucket.
     * @return true if request is allowed, false if rate-limited
     */
    public synchronized boolean allowRequest() {
        leak();

        // If the bucket has already max requests, we reject the incoming request
        if (currentLevel + 1 > capacity) {
            return false;
        }

        currentLevel++;
        return true;
    }

    /**
     * Applies leakage based on elapsed time.
     */
    private void leak() {
        long now = System.currentTimeMillis();
        long elapsedMs = now - lastLeakTimestamp;

        double leakedAmount = elapsedMs * leakRatePerMs;
        currentLevel = Math.max(0, currentLevel - leakedAmount);

        lastLeakTimestamp = now;
    }
}

/*

    IMP: This code only decides whether a request is allowed or not
    Why synchronized?
    This ensures:
        Only one thread at a time modifies the bucket
        Prevents race conditions in multi-threaded servers (very important for APIs)

    Key takeaway (important)
        The bucket is NOT drained continuously.
        It is drained only when a request arrives, based on elapsed time.

    leak() removes request traces from the bucket based on how much time has passed, simulating a steady allowance rate.
    1. When a request comes, the rate limiter checks whether it should be allowed or rejected.
    2.When another request comes, the previous request may already be fully processed by the application,
      but its trace still exists in the rate limiter (it still counts toward the limit).
    3. Before deciding, we call leak() to remove (or reduce) the traces of older requests based on elapsed time.
        There is no fixed window here.
        Time is continuous, and request traces fade gradually, not abruptly.

    Rate limiting is about remembering past allowed requests and forgetting them over time.

    Your LeakyBucket class:
        Represents one bucket
        Tracks rate limits for one entity
        Is completely unaware of:
        Users
        IPs
        APIs
        Endpoints

 */