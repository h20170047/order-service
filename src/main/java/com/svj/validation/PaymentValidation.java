package com.svj.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentValidator.class)
public @interface PaymentValidation {
    String message() default "{Invalid payment method}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
