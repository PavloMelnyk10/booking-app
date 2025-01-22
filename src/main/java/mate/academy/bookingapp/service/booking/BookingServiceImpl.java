package mate.academy.bookingapp.service.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.CreateBookingRequestDto;
import mate.academy.bookingapp.dto.booking.UpdateBookingRequestDto;
import mate.academy.bookingapp.exception.AccommodationFullyBookedException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.BookingMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.model.Role;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.accommodation.AccommodationRepository;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(final CreateBookingRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();
        Accommodation accommodation = getAccommodationOrThrow(requestDto.getAccommodationId());

        checkAvailability(accommodation,
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                null
        );

        Booking booking = bookingMapper.toModel(requestDto);
        booking.setUser(currentUser);
        booking.setAccommodation(accommodation);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public Page<BookingDto> getCurrentUserBookings(final Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<Booking> userBookings = bookingRepository
                .findAllByUserId(currentUser.getId(), pageable);
        return userBookings.map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> findByUserIdAndStatus(final Long userId,
                                                  final BookingStatus status,
                                                  final Pageable pageable) {
        return bookingRepository.findAllByUserIdAndStatus(userId, status, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingDto findBookingById(final Long id) {
        Booking booking = getBookingOrThrow(id);
        checkUserAccessToBooking(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateBooking(final Long bookingId,
                                    final UpdateBookingRequestDto requestDto) {
        Booking booking = getBookingOrThrow(bookingId);
        checkUserAccessToBooking(booking);

        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Cannot update a booking with status " + booking.getStatus()
            );

        }

        checkAvailability(
                booking.getAccommodation(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                booking.getId()
        );

        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public void cancelBookingById(final Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        checkUserAccessToBooking(booking);

        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Booking cannot be canceled as it is already " + booking.getStatus()
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private Accommodation getAccommodationOrThrow(final Long accommodationId) {
        return accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation with ID " + accommodationId + " not found")
                );
    }

    private Booking getBookingOrThrow(final Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with ID " + bookingId + " not found")
                );
    }

    private void checkAvailability(final Accommodation accommodation,
                                   final java.time.LocalDate checkIn,
                                   final java.time.LocalDate checkOut,
                                   final Long excludeBookingId) {
        long overlappingCount = (excludeBookingId == null)
                ? bookingRepository.countOverlappingBookings(
                        accommodation.getId(), checkIn, checkOut)
                : bookingRepository.countOverlappingBookings(
                        accommodation.getId(), excludeBookingId, checkIn, checkOut);

        if (overlappingCount >= accommodation.getAvailability()) {
            throw new AccommodationFullyBookedException(
                    "Accommodation is fully booked for the given dates"
            );
        }
    }

    private void checkUserAccessToBooking(final Booking booking) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ADMIN
                        || role.getName() == Role.RoleName.SUPER_ADMIN)) {
            return;
        }

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You do not have access to booking with ID " + booking.getId()
            );
        }
    }
}
