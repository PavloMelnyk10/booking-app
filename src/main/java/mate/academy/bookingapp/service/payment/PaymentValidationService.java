package mate.academy.bookingapp.service.payment;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.model.PaymentStatus;
import mate.academy.bookingapp.model.Role;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.repository.payment.PaymentRepository;
import mate.academy.bookingapp.repository.user.UserRepository;
import mate.academy.bookingapp.service.discount.DiscountStrategy;
import mate.academy.bookingapp.service.discount.DiscountStrategyFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidationService {
    private final UserRepository userRepository;
    private final DiscountStrategyFactory discountStrategyFactory;

    public Booking getBookingOrThrow(final Long bookingId,
                                     final BookingRepository bookingRepository) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with ID " + bookingId + " not found"));
    }

    public Payment getPaymentOrThrow(final String sessionId,
                                     final PaymentRepository paymentRepository) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session: " + sessionId));
    }

    public void findUserOrThrow(final Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + userId + " not found"));
    }

    public void validateBookingStatus(final Booking booking) {
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Cannot create payment for booking with status " + booking.getStatus());
        }
    }

    public void validatePendingPaymentsForBooking(final Long bookingId,
                                                  final PaymentRepository paymentRepository) {
        boolean hasPendingPayments
                = paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.PENDING);
        if (hasPendingPayments) {
            throw new IllegalArgumentException(
                    "A pending payment already exists for this booking.");
        }
    }

    public BigDecimal calculateTotalPrice(final Booking booking) {
        if (booking.getAccommodation().getDailyRate() == null) {
            throw new IllegalArgumentException("Accommodation daily rate is not set");
        }
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Booking dates are not specified");
        }

        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (days <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking dates: check-out must be after check-in");
        }

        BigDecimal basePrice = booking.getAccommodation()
                .getDailyRate().multiply(BigDecimal.valueOf(days));

        return basePrice.subtract(getDiscount(booking, basePrice));
    }

    public boolean isAdminOrSuperAdmin(final User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ADMIN
                        || role.getName() == Role.RoleName.SUPER_ADMIN);
    }

    private BigDecimal getDiscount(Booking booking, BigDecimal basePrice) {
        DiscountStrategy strategy = discountStrategyFactory
                .getStrategy(booking.getUser().getCompletedBookings());
        return strategy.calculateDiscount(basePrice, booking.getUser().getCompletedBookings());
    }
}
