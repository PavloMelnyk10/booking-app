package mate.academy.bookingapp.scheduler;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expirePendingBookings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        bookingRepository.expireOldBookings(
                BookingStatus.EXPIRED,
                BookingStatus.PENDING,
                cutoffTime
        );
    }
}
