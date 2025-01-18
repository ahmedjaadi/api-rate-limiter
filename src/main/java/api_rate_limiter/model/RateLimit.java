package api_rate_limiter.model;

import lombok.*;

import java.time.Duration;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RateLimit {
    private  long capacity;
    private  long refillTokens;
    private Duration period;

}
