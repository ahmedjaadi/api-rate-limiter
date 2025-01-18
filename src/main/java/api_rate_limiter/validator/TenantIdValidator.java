package api_rate_limiter.validator;


import api_rate_limiter.util.Tenant;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TenantIdValidator implements ConstraintValidator<ValidTenantId, String> {
    @Override
    public boolean isValid(String tenantId, ConstraintValidatorContext context) {
        return validateTenantId(tenantId);
    }

    private boolean validateTenantId(final String tenantId) {
        return Tenant.contains(tenantId);
    }
}
