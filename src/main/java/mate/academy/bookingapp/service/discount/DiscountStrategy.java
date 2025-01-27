package mate.academy.bookingapp.service.discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateDiscount(BigDecimal totalAmount, int completedBookings);
}
