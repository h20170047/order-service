package com.svj.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PaymentValidator implements ConstraintValidator<PaymentValidation, String>{

    @Value("#{'${app.paymentMethods}'.split('\\s*,\\s*')}")
    private Set<String> validPaymentMethods;
    @Override
    public void initialize(PaymentValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        if(validPaymentMethods.contains(input))
            return true;
        else
            return false;
    }
}
