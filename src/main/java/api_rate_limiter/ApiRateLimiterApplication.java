package api_rate_limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiRateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRateLimiterApplication.class, args);
    }

}
