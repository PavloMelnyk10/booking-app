package mate.academy.bookingapp.service.booking;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingCancelledMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingCreatedMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildBookingUpdatedMessage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.CreateBookingRequestDto;
import mate.academy.bookingapp.dto.booking.UpdateBookingRequestDto;
import mate.academy.bookingapp.mapper.BookingMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.service.accesscontrol.AccessControlService;
import mate.academy.bookingapp.service.notification.TelegramNotificationService;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccessControlService accessControlService;
    private final BookingValidationService bookingValidationService;
    private final TelegramNotificationService notificationService;

    @Override
    public BookingDto createBooking(final CreateBookingRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();
        Accommodation accommodation =
                bookingValidationService.getAccommodationOrThrow(requestDto.getAccommodationId());

        bookingValidationService.checkAvailability(accommodation,
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                null
        );

        Booking booking = bookingMapper.toModel(requestDto);
        booking.setUser(currentUser);
        booking.setAccommodation(accommodation);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.sendBookingMessage(buildBookingCreatedMessage(savedBooking));

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
        Booking booking = bookingValidationService.getBookingOrThrow(id);
        accessControlService.validateBookingOwnership(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateBooking(final Long bookingId,
                                    final UpdateBookingRequestDto requestDto) {
        Booking booking = bookingValidationService.getBookingOrThrow(bookingId);
        accessControlService.validateBookingOwnership(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot update a booking with status " + booking.getStatus()
            );

        }

        bookingValidationService.checkAvailability(
                booking.getAccommodation(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                booking.getId()
        );

        notificationService.sendBookingMessage(buildBookingUpdatedMessage(booking, requestDto));

        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public void cancelBookingById(final Long bookingId) {
        Booking booking = bookingValidationService.getBookingOrThrow(bookingId);
        accessControlService.validateBookingOwnership(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Booking cannot be canceled as it is already " + booking.getStatus()
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        notificationService.sendBookingMessage(buildBookingCancelledMessage(booking));

        bookingRepository.save(booking);
    }
}
