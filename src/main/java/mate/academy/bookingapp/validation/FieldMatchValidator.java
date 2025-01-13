package mate.academy.bookingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object firstValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object secondValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

        boolean valid = Objects.equals(firstValue, secondValue);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }

        return valid;
    }
}
