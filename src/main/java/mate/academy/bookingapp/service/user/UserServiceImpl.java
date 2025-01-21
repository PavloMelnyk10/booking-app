package mate.academy.bookingapp.service.user;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UpdateUserRequestDto;
import mate.academy.bookingapp.dto.user.UpdateUserRolesRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
import mate.academy.bookingapp.exception.DuplicateEntityException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.exception.InvalidOperationException;
import mate.academy.bookingapp.exception.RegistrationException;
import mate.academy.bookingapp.mapper.UserMapper;
import mate.academy.bookingapp.model.Role;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.role.RoleRepository;
import mate.academy.bookingapp.repository.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(final UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user. Email already exists");
        }

        Role role = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        User user = userMapper.toModel(requestDto);
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UserResponseDto getCurrentUserDetails() {
        return userMapper.toUserResponse(getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponseDto updateUserDetails(final UpdateUserRequestDto requestDto) {
        User user = getCurrentUser();

        if (userRepository.existsByEmailAndIdNot(requestDto.getEmail(), user.getId())) {
            throw new DuplicateEntityException(
                    "Another user with the same email already exists");
        }

        userMapper.updateUserFromDto(requestDto, user);

        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto updateUserRoles(
            final Long userId,
            final UpdateUserRolesRequestDto requestDto) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(userId)) {
            throw new InvalidOperationException("SUPER_ADMIN cannot change their own role.");
        }

        if (requestDto.getRoles().contains(Role.RoleName.SUPER_ADMIN)) {
            throw new InvalidOperationException(
                    "SUPER_ADMIN cannot assign the SUPER_ADMIN role to others.");
        }

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + userId + " not found."));

        Set<Role> updatedRoles = new HashSet<>();
        for (Role.RoleName roleName : requestDto.getRoles()) {
            Role roleEntity = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Role " + roleName + " not found."));
            updatedRoles.add(roleEntity);
        }

        userToUpdate.setRoles(updatedRoles);
        userRepository.save(userToUpdate);

        return userMapper.toUserResponse(userToUpdate);
    }
}
