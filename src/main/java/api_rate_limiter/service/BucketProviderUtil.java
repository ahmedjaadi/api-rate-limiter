package api_rate_limiter.service;

import api_rate_limiter.model.RateLimit;
import api_rate_limiter.model.TenantRateLimit;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

public class BucketProviderUtil {

    public static Bandwidth getMonthlyRateLimit(TenantRateLimit tenantRateLimit) {
        return getRateLimit(tenantRateLimit.getMonthlyRateLimit());
    }
    public static Bandwidth getTimeWindowRateLimit(TenantRateLimit tenantRateLimit) {
        return getRateLimit(tenantRateLimit.getTimeWindowRateLimit());
    }
    private static Bandwidth getRateLimit(RateLimit rateLimit) {
        return Bandwidth.classic(rateLimit.getCapacity(), Refill.intervally(rateLimit.getRefillTokens(), rateLimit.getPeriod()));
    }
}
