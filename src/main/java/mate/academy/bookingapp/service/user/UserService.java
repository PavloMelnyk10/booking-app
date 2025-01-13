package mate.academy.bookingapp.service.user;

import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
import mate.academy.bookingapp.exception.RegistrationException;
import mate.academy.bookingapp.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    User getCurrentUser();
}
