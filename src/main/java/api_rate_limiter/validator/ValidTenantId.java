package api_rate_limiter.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TenantIdValidator.class)
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
@NotBlank
public @interface ValidTenantId {
    String message() default "Invalid tenant id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
