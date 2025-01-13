package mate.academy.bookingapp.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
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
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
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
}
