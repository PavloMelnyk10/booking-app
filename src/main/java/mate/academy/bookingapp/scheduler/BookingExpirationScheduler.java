package mate.academy.bookingapp.scheduler;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingExpiredMessage;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.service.notification.TelegramNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {
    private final BookingRepository bookingRepository;
    private final TelegramNotificationService notificationService;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expirePendingBookings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        bookingRepository.findPendingWithoutPaymentsBefore(cutoffTime)
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.EXPIRED);
                    bookingRepository.save(booking);

                    notificationService.sendBookingMessage(buildBookingExpiredMessage(booking));
                });
    }
}
