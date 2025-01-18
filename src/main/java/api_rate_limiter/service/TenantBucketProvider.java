package api_rate_limiter.service;

import api_rate_limiter.model.TenantRateLimit;
import io.github.bucket4j.Bucket;

/**
 * This interface is the specification for any service that would provide implementation to provide
 * token bucket based on different rules
 * @author Ahmed Ali Rashid
 */
public interface TenantBucketProvider {
    /**
     * Returns a cumulative bucket that combines all the tenant rules  specified
     * {@link TenantRateLimit}
     * @param tenantRateLimit a business object that represent a valid tenant
     * @return the cumulative bucket that combine all the rules for the tenant
     */
    Bucket getBucketByTenant(TenantRateLimit tenantRateLimit);

    /**
     * Get the bucket that is applicable for every tenant
     * @return system-wide tenant
     */
    Bucket getApiServiceWideBucket();

    /**
     * Attempt to try and consume by combining all the valid rules applicable to the tenant, and if the attempt
     * fails the method should recover all the used tokens
     * @param tenantRateLimit a business object that represent a valid tenant
     * @param tokens tokens to be consumed by the tenant
     * @return true if the tenant can consume and false otherwise
     */
    boolean tryConsumeByTenant(TenantRateLimit tenantRateLimit, long tokens);
}
