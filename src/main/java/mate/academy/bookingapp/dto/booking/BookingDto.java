package mate.academy.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import mate.academy.bookingapp.model.BookingStatus;

@Data
public class BookingDto {
    private Long id;
    private Long accommodationId;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
}
