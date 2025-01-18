package api_rate_limiter.service.impl;

import api_rate_limiter.model.TenantRateLimit;
import api_rate_limiter.service.BucketProviderUtil;
import api_rate_limiter.util.Profiles;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConfigurationBuilder;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This Implementation for rate limits rules/quotas that are in a distributed environment and use Redis as the
 * distributed cache provider
 *
 * @author Ahmed Ali Rashid
 */

@Service
@Profile(Profiles.DISTRIBUTED)
public class RedisCacheTenantBucketProvider extends SimpleTenantBucketProvider {
    private final ProxyManager<String> bucketsProxyManager;
    private static final Map<String, Supplier<BucketConfiguration>> cache = new ConcurrentHashMap<>();

    static{
        HardCodedTenantRules.tenantRateLimits.forEach(tenantRateLimit -> {
            ConfigurationBuilder configurationBuilder = BucketConfiguration.builder();
            if (Objects.nonNull(tenantRateLimit.getMonthlyRateLimit())) {
                configurationBuilder.addLimit(BucketProviderUtil.getMonthlyRateLimit(tenantRateLimit));
            }
            if (Objects.nonNull(tenantRateLimit.getTimeWindowRateLimit())) {
                configurationBuilder.addLimit(BucketProviderUtil.getTimeWindowRateLimit(tenantRateLimit));
            }
            cache.put(tenantRateLimit.getTenantId(), configurationBuilder::build);
        });

    }
    public RedisCacheTenantBucketProvider(ProxyManager<String> bucketsProxyManager) {
        this.bucketsProxyManager = bucketsProxyManager;
        this.setApiServiceWideBucket();
    }

    private void setApiServiceWideBucket() {
        ConfigurationBuilder configurationBuilder = BucketConfiguration.builder();
        configurationBuilder.addLimit(HardCodedTenantRules.apiServiceWideLimit);
        Supplier<BucketConfiguration> configSupplier = configurationBuilder::build;
        apiServiceWideBucket = bucketsProxyManager.builder().build(HardCodedTenantRules.API_SERVICE_WIDE_BUCKET_KEY, configSupplier);
    }

    @Override
    public Bucket getApiServiceWideBucket() {
        return apiServiceWideBucket;
    }

    @Override
    public Bucket getBucketByTenant(TenantRateLimit tenantRateLimit) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierByTenant(tenantRateLimit.getTenantId());

        return bucketsProxyManager.builder().build(tenantRateLimit.getTenantId(), configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierByTenant(String tenantId) {
        return cache.get(tenantId);
    }
}
