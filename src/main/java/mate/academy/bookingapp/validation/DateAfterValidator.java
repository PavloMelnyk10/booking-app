package mate.academy.bookingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.bookingapp.dto.booking.CreateBookingRequestDto;

public class DateAfterValidator implements ConstraintValidator<DateAfter, CreateBookingRequestDto> {

    @Override
    public boolean isValid(CreateBookingRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
            return false;
        }
        return dto.getCheckOutDate().isAfter(dto.getCheckInDate());
    }
}

