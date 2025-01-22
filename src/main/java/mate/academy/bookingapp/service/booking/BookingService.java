package mate.academy.bookingapp.service.booking;

import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.CreateBookingRequestDto;
import mate.academy.bookingapp.dto.booking.UpdateBookingRequestDto;
import mate.academy.bookingapp.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequestDto requestDto);

    Page<BookingDto> getCurrentUserBookings(Pageable pageable);

    Page<BookingDto> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    BookingDto findBookingById(Long id);

    void cancelBookingById(Long id);

    BookingDto updateBooking(Long id, UpdateBookingRequestDto requestDto);
}
