package api_rate_limiter.service.impl;

import api_rate_limiter.service.RateLimitAlgorithm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "of")
public class TokenBucket implements RateLimitAlgorithm {
    private final int capacity;
    private final int refillTokens;
    private final Duration refillInterval;
    private int tokens;
    private Instant lastRefillTime = Instant.now();

    @Override
    public synchronized boolean tryConsume(int numTokens) {
        refill();
        if (tokens >= numTokens) {
            tokens -= numTokens;
            return true;
        }
        return false;
    }

    private void refill() {
        Instant now = Instant.now();
        long timeElapsed = Duration.between(lastRefillTime, now).toMillis();
        int tokensToAdd = (int) (timeElapsed / refillInterval.toMillis()) * refillTokens;
        tokens = Math.min(tokens + tokensToAdd, capacity);
        lastRefillTime = now;
    }

}
