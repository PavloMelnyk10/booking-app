package mate.academy.bookingapp.security;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserLoginRequestDto;
import mate.academy.bookingapp.dto.user.UserLoginResponseDto;
import mate.academy.bookingapp.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getId());

        return new UserLoginResponseDto(token, user.getId());
    }
}
