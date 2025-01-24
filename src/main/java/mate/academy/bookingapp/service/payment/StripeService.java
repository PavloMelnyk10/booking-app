package mate.academy.bookingapp.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import mate.academy.bookingapp.model.Booking;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public Session retrieveSession(final String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new RuntimeException("Error retrieving session from Stripe", e);
        }
    }

    public void expireSession(final String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("open".equals(session.getStatus())) {
                session.expire();
            }
        } catch (StripeException e) {
            throw new RuntimeException("Error expiring session in Stripe", e);
        }
    }

    public Session createStripeSession(final Booking booking,
                                       final BigDecimal totalAmount,
                                       final String successUrl,
                                       final String cancelUrl) {
        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(totalAmount.movePointRight(2).longValue())
                                .setProductData(SessionCreateParams.LineItem
                                        .PriceData.ProductData.builder()
                                        .setName("Booking #" + booking.getId())
                                        .build())
                                .build())
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error creating Stripe session", e);
        }
    }
}
