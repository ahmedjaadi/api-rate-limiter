package api_rate_limiter.util;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Tenant {
    AMAZON("amazon"),
    META("meta"),
    GOOGLE("google"),
    ;
    private final String tenantId;

    Tenant(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return tenantId;
    }

    public static boolean contains(String tenantId) {
        return Arrays.stream(Tenant.values())
                .map(Tenant::getTenantId)
                .anyMatch(foundTenantId -> foundTenantId.equals(tenantId));
    }
}
