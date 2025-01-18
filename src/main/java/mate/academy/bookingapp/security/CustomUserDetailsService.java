package mate.academy.bookingapp.security;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Can't find user by email: " + email));
    }

    public User loadUserById(Long id) throws UsernameNotFoundException {
        return userRepository.findWithRolesById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Can't find user by ID: " + id));
    }
}
