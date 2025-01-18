package api_rate_limiter.service.impl;

import api_rate_limiter.model.TenantRateLimit;
import api_rate_limiter.service.TenantBucketProvider;
import io.github.bucket4j.Bucket;

public abstract class SimpleTenantBucketProvider implements TenantBucketProvider {
    protected  Bucket apiServiceWideBucket;
    @Override
    public boolean tryConsumeByTenant(TenantRateLimit tenantRateLimit, long tokens) {
        if (getApiServiceWideBucket().tryConsume(tokens)) {
            if (getBucketByTenant(tenantRateLimit).tryConsume(tokens)) {
                return true;
            }
            apiServiceWideBucket.addTokens(tokens);
            return false;
        }
        return false;
    }
}
