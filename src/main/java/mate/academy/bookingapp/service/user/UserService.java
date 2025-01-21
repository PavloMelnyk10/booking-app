package mate.academy.bookingapp.service.user;

import mate.academy.bookingapp.dto.user.UpdateUserRequestDto;
import mate.academy.bookingapp.dto.user.UpdateUserRolesRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
import mate.academy.bookingapp.exception.RegistrationException;
import mate.academy.bookingapp.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    User getCurrentUser();

    UserResponseDto getCurrentUserDetails();

    UserResponseDto updateUserDetails(UpdateUserRequestDto requestDto);

    UserResponseDto updateUserRoles(Long userId, UpdateUserRolesRequestDto requestDto);
}
