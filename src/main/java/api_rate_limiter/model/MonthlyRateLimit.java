package api_rate_limiter.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MonthlyRateLimit extends RateLimit {
    public static RateLimit of (long capacity, long refillTokens){
        return RateLimit.of(capacity, refillTokens, Duration.of(30, ChronoUnit.DAYS));
    }
}
