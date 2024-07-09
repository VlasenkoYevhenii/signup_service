package application.distributedsignup.fieldvalidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
            Object firstValue = beanWrapper.getPropertyValue(firstFieldName);
            Object secondValue = beanWrapper.getPropertyValue(secondFieldName);

            return Objects.equals(firstValue, secondValue);
        } catch (Exception e) {
            throw new RuntimeException("Field match validation failed", e);
        }
    }
}
