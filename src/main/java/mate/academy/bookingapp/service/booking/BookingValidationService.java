package mate.academy.bookingapp.service.booking;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.exception.AccommodationFullyBookedException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.repository.accommodation.AccommodationRepository;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;

    public Booking getBookingOrThrow(final Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with ID " + bookingId + " not found"));
    }

    public Accommodation getAccommodationOrThrow(final Long accommodationId) {
        return accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation with ID " + accommodationId + " not found"));
    }

    public void checkAvailability(final Accommodation accommodation,
                                  final LocalDate checkIn,
                                  final LocalDate checkOut,
                                  final Long excludeBookingId) {
        long overlappingCount = (excludeBookingId == null)
                ? bookingRepository.countOverlappingBookings(
                        accommodation.getId(), checkIn, checkOut)
                : bookingRepository.countOverlappingBookings(
                        accommodation.getId(), excludeBookingId, checkIn, checkOut);

        if (overlappingCount >= accommodation.getAvailability()) {
            throw new AccommodationFullyBookedException(
                    "Accommodation is fully booked for the given dates");
        }
    }
}
