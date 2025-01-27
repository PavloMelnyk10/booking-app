package mate.academy.bookingapp.service.payment;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildPaymentCanceledMessages;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildPaymentCreatedMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildPaymentSuccessMessages;

import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.mapper.PaymentMapper;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.model.PaymentStatus;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.booking.BookingRepository;
import mate.academy.bookingapp.repository.payment.PaymentRepository;
import mate.academy.bookingapp.service.accesscontrol.AccessControlService;
import mate.academy.bookingapp.service.notification.MessageBuilder;
import mate.academy.bookingapp.service.notification.TelegramNotificationService;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserService userService;
    private final AccessControlService accessControlService;
    private final PaymentValidationService paymentValidationService;
    private final StripeService stripeService;
    private final TelegramNotificationService notificationService;

    @Override
    public PaymentDto createPaymentSession(final Long bookingId,
                                           final String successUrl,
                                           final String cancelUrl) {
        Booking booking = paymentValidationService.getBookingOrThrow(bookingId, bookingRepository);

        accessControlService.validateBookingOwnership(bookingId);
        paymentValidationService.validateBookingStatus(booking);
        paymentValidationService.validatePendingPaymentsForBooking(
                booking.getId(), paymentRepository
        );

        BigDecimal totalAmount = paymentValidationService.calculateTotalPrice(booking);
        Session session = stripeService.createStripeSession(
                booking, totalAmount, successUrl, cancelUrl
        );

        Payment payment = savePayment(booking, totalAmount, session);

        booking.setCreatedAt(payment.getCreatedAt());
        bookingRepository.save(booking);

        notificationService.sendPaymentMessage(buildPaymentCreatedMessage(payment, totalAmount));

        return paymentMapper.toDto(payment);
    }

    @Override
    public String handleSuccess(final String sessionId) {
        Payment payment = paymentValidationService.getPaymentOrThrow(sessionId, paymentRepository);
        accessControlService.validateBookingOwnership(payment.getBooking().getId());

        Session session = stripeService.retrieveSession(sessionId);

        if ("paid".equals(session.getPaymentStatus())) {
            payment.setStatus(PaymentStatus.PAID);
            Booking booking = payment.getBooking();
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.CONFIRMED);
            }
            paymentRepository.save(payment);

            MessageBuilder.PaymentMessagesDto messages = buildPaymentSuccessMessages(payment);
            notificationService.sendPaymentMessage(messages.paymentMessage());
            notificationService.sendBookingMessage(messages.bookingMessage());

            return "Payment successfully completed for session: " + sessionId;
        }

        return "Payment not completed yet for session: " + sessionId
                + ". Current status: " + session.getPaymentStatus();
    }

    @Override
    public String handleCancel(final String sessionId) {
        Payment payment = paymentValidationService.getPaymentOrThrow(sessionId, paymentRepository);
        stripeService.expireSession(sessionId);

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.EXPIRED);
        }

        MessageBuilder.PaymentMessagesDto messages = buildPaymentCanceledMessages(payment);
        notificationService.sendPaymentMessage(messages.paymentMessage());
        notificationService.sendBookingMessage(messages.bookingMessage());

        bookingRepository.save(booking);
        return "Payment session with ID " + sessionId
                + " has been canceled, and booking marked as EXPIRED.";
    }

    @Override
    public Page<PaymentDto> getCurrentUserPayments(final Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return findAllByUserId(currentUser.getId(), pageable);
    }

    @Override
    public Page<PaymentDto> findAllByUserId(final Long userId, Pageable pageable) {
        paymentValidationService.findUserOrThrow(userId);
        return paymentRepository.findAllByBookingUserId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    public Page<PaymentDto> getAllPayments(final Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        if (paymentValidationService.isAdminOrSuperAdmin(currentUser)) {
            return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
        }
        return findAllByUserId(currentUser.getId(), pageable);
    }

    private Payment savePayment(final Booking booking,
                                final BigDecimal totalAmount,
                                final Session session) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }
}
