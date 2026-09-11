package com.khan.EComm.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimitBucketManager {

    // Why ConcurrentHashMap?
    //   ConcurrentHashMap ensures:
    //     Safe concurrent access
    //     High performance (better than synchronized map)
    private final ConcurrentHashMap<String, LeakyBucket> buckets = new ConcurrentHashMap<>();

    private final int capacity;
    private final double leakRatePerSecond;

    public RateLimitBucketManager(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    // Why computeIfAbsent?
    // 1. Thread-safe
    // 2. Avoids creating multiple buckets for the same key
    /**
     * Returns true if request is allowed for the given key (IP).
     */
    public boolean allowRequest(String key) {
        LeakyBucket bucket = buckets.computeIfAbsent(
                key,
                k -> new LeakyBucket(capacity, leakRatePerSecond)
        );

        return bucket.allowRequest();
    }
}



/*
    LeakyBucket is the algorithm.
    RateLimitBucketManager is how you use that algorithm in a real application.

    In a real application, you don’t rate-limit “the server”.
    You rate-limit:
        Each IP address
        Each user
        Each API key
        Each client

    That means:
        IP A → its own bucket
        IP B → its own bucket
        IP C → its own bucket
        Since bucket is mapped to IP, each client gets its own bucket.

    RateLimitBucketManager answers this question:
    “Given an identifier (IP / userId / API key), which bucket should I use?”

    We will create:
        One manager for login
        One manager for register
 */