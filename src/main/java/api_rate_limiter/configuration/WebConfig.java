package api_rate_limiter.configuration;

import api_rate_limiter.service.RateLimitAlgorithm;
import api_rate_limiter.service.impl.TokenBucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class WebConfig {
    @Bean
    public RateLimitAlgorithm tokenBucket() {
        return TokenBucket.of(1000, 25, Duration.of(250, ChronoUnit.MILLIS));
    }
    @Bean
    public Bucket bucket4j() {
        Bandwidth limit = Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
