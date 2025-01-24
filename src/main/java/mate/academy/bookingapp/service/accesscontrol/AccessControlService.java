package mate.academy.bookingapp.service.accesscontrol;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.Role;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public void validateBookingOwnership(final Long bookingId) {
        User currentUser = userService.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with ID " + bookingId + " not found"));

        if (currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ADMIN
                        || role.getName() == Role.RoleName.SUPER_ADMIN)) {
            return;
        }

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this booking.");
        }
    }
}
