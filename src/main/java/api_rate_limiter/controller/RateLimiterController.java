package api_rate_limiter.controller;

import api_rate_limiter.model.TenantRateLimit;
import api_rate_limiter.service.ApiServerSender;
import api_rate_limiter.service.TenantBucketProvider;
import api_rate_limiter.validator.ValidTenantId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@Validated
@RequestMapping("/api/v1/**")
class RateLimiterController {
    private final TenantBucketProvider tenantBucketProvider;
    private final ApiServerSender apiServerSender;

    public RateLimiterController(TenantBucketProvider tenantBucketProvider, ApiServerSender apiServerSender) {
        this.tenantBucketProvider = tenantBucketProvider;
        this.apiServerSender = apiServerSender;
    }

    @GetMapping
    public ResponseEntity<Object> getHandler(
            @ValidTenantId @RequestHeader(value = "X-tenant-id") String tenantId
    ) {
        TenantRateLimit tenantRateLimit = TenantRateLimit.builder().tenantId(tenantId).build();
        if (tenantBucketProvider.tryConsumeByTenant(tenantRateLimit, 1)) {
            String endpoint = this.getEndpoint();
            Object apiServiceResponse = apiServerSender.send(endpoint, "GET");
            return ResponseEntity.ok(apiServiceResponse);
        }
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }

    private String getEndpoint() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getRequest().getServletPath();
    }
}
