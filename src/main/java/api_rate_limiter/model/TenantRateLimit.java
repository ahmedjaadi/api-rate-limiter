package api_rate_limiter.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode( exclude = {"monthlyRateLimit", "timeWindowRateLimit"})
public class TenantRateLimit {
    private final  String tenantId;
    private final RateLimit monthlyRateLimit;
    private final RateLimit timeWindowRateLimit;

}
