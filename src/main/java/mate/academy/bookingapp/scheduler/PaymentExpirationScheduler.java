package mate.academy.bookingapp.scheduler;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingExpiredDueToPaymentMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildPaymentExpiredMessage;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.model.PaymentStatus;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.repository.payment.PaymentRepository;
import mate.academy.bookingapp.service.notification.TelegramNotificationService;
import mate.academy.bookingapp.service.payment.StripeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final StripeService stripeService;
    private final TelegramNotificationService notificationService;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expirePendingPayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        paymentRepository.findPendingBefore(cutoffTime).forEach(payment -> {
            stripeService.expireSession(payment.getSessionId());
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);

                notificationService.sendBookingMessage(
                        buildBookingExpiredDueToPaymentMessage(booking));
            }

            notificationService.sendPaymentMessage(buildPaymentExpiredMessage(payment));
        });
    }
}
