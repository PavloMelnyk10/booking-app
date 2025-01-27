package mate.academy.bookingapp.scheduler;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingCompletedMessage;

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
public class BookingCompletionScheduler {
    private final BookingRepository bookingRepository;
    private final TelegramNotificationService notificationService;

    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void completeBookings() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        bookingRepository.findAllByStatusAndCheckOutDate(
                        BookingStatus.CONFIRMED, today.toLocalDate())
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);

                    notificationService.sendBookingMessage(buildBookingCompletedMessage(booking));
                });
    }
}
