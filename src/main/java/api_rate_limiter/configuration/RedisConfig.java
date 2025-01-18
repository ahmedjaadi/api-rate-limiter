package api_rate_limiter.configuration;

import api_rate_limiter.util.Profiles;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Configuration
@Profile(Profiles.DISTRIBUTED)
public class RedisConfig  {

    @Value(value = "${custom.cacheManager.redis.connection.url}")
    public String redisConnectionUrl;
    @Value(value = "${custom.cacheManager.redis.cache.key}")
    public String redisCacheKey;
    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisConnectionUrl);
        return config;
    }

    @Bean(name="customCacheManager")
    public CacheManager cacheManager(Config config) {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        cacheManager.createCache(redisCacheKey, RedissonConfiguration.fromConfig(config));
        return cacheManager;
    }

    @Bean
    ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache(redisCacheKey));
    }
}
