package mate.academy.bookingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.bookingapp.dto.booking.UpdateBookingRequestDto;

public class UpdateDateAfterValidator implements
        ConstraintValidator<UpdateDateAfter, UpdateBookingRequestDto> {

    @Override
    public boolean isValid(UpdateBookingRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
            return false;
        }
        return dto.getCheckOutDate().isAfter(dto.getCheckInDate());
    }
}
