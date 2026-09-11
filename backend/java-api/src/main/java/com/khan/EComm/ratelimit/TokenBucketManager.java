package com.khan.EComm.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketManager {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final int capacity;
    private final double refillRatePerSecond;

    public TokenBucketManager(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public boolean allowRequest(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(
                key,
                k -> new TokenBucket(capacity, refillRatePerSecond)
        );
        return bucket.allowRequest();
    }
}
