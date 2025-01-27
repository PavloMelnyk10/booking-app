package mate.academy.bookingapp.service.notification;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import mate.academy.bookingapp.dto.booking.UpdateBookingRequestDto;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Amenity;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.Payment;

public final class MessageBuilder {
    private MessageBuilder() {
    }

    public record PaymentMessagesDto(String paymentMessage, String bookingMessage) {}

    /* Accommodation */
    public static String buildAccommodationCreatedMessage(Accommodation accommodation) {
        String amenities = accommodation.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.joining(", "));

        return String.format(
                """
                🏨 New Accommodation Created:
                ID: %d
                Name: %s
                Location: %s
                Amenities: %s
                Daily rate: $%.2f
                """,
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getLocation(),
                amenities.isEmpty() ? "No amenities listed" : amenities,
                accommodation.getDailyRate()
        );
    }

    public static String buildAccommodationDeletedMessage(Accommodation accommodation) {
        return String.format(
                """
                ❌ Accommodation Deleted:
                ID: %d
                Name: %s
                """,
                accommodation.getId(),
                accommodation.getName()
        );
    }

    public static String buildAccommodationUpdatedMessage(Accommodation accommodation) {
        return String.format(
                """
                ✏️ Accommodation Updated:
                ID: %d
                Name: %s
                Location: %s
                Price per Night: $%.2f
                """,
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getLocation(),
                accommodation.getDailyRate()
        );
    }

    /* Booking */
    public static String buildBookingCreatedMessage(Booking booking) {
        return String.format(
                """
                🛏️ New Booking:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingUpdatedMessage(
            Booking booking, UpdateBookingRequestDto requestDto) {
        return String.format(
                """
                🔄 Booking Updated:
                ID: %d
                Old Dates: %s to %s
                New Dates: %s to %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingCancelledMessage(Booking booking) {
        return String.format(
                """
                ❌ Booking Cancelled:
                ID: %d
                Status: %s
                """,
                booking.getId(),
                booking.getStatus()
        );
    }

    public static String buildBookingCompletedMessage(Booking booking) {
        return String.format(
                """
                        ✅ Booking Completed:
                        ID: %d
                        Check-out Date: %s
                        Status: %s
                        """,
                booking.getId(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingExpiredMessage(Booking booking) {
        return String.format(
                """
                ❌ Booking Expired:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    /* Payment */

    private static String buildPaymentBookingInfo(Payment payment) {
        Booking booking = payment.getBooking();
        return String.format(
                """
                Payment ID: %d
                Booking ID: %d
                Payment Status: %s
                Booking Status: %s
                """,
                payment.getId(),
                booking.getId(),
                payment.getStatus(),
                booking.getStatus()
        );
    }

    public static String buildPaymentCreatedMessage(Payment payment, BigDecimal totalAmount) {
        return String.format(
                """
                💳 Payment Created:
                %s
                Amount Due: $%.2f
                """,
                buildPaymentBookingInfo(payment),
                totalAmount
        );
    }

    public static PaymentMessagesDto buildPaymentSuccessMessages(Payment payment) {
        String paymentMsg = String.format(
                """
                ✅ Payment Successful:
                %s
                Amount Paid: $%.2f
                """,
                buildPaymentBookingInfo(payment),
                payment.getAmount()
        );

        Booking booking = payment.getBooking();
        String bookingMsg = String.format(
                """
                ✅ Booking confirmed and paid:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );

        return new PaymentMessagesDto(paymentMsg, bookingMsg);
    }

    public static PaymentMessagesDto buildPaymentCanceledMessages(Payment payment) {
        String paymentMsg = String.format(
                """
                ❌ Payment Cancelled:
                %s
                """,
                buildPaymentBookingInfo(payment)
        );

        Booking booking = payment.getBooking();
        String bookingMsg = String.format(
                """
                ❌ Booking Cancelled:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );

        return new PaymentMessagesDto(paymentMsg, bookingMsg);
    }

    public static String buildPaymentExpiredMessage(Payment payment) {
        return String.format(
                """
                ❌ Payment Expired:
                Booking ID: %d
                Payment Status: %s
                """,
                payment.getBooking().getId(),
                payment.getStatus()
        );
    }

    public static String buildBookingExpiredDueToPaymentMessage(Booking booking) {
        return String.format(
                """
                ❌ Booking Expired due to Payment:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }
}
