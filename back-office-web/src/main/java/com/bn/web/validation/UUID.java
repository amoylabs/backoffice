package com.bn.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

/**
 * Validation constraint for a UUID in string format.
 */
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
@Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$", message = "Invalid UUID", flags = CASE_INSENSITIVE)
@Constraint(validatedBy = {})
@Documented
public @interface UUID {
    String message() default "{validation.uuid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
