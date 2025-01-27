package mate.academy.bookingapp.service.discount;

import java.math.BigDecimal;

public class SilverDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, int completedBookings) {
        return totalAmount.multiply(BigDecimal.valueOf(0.05));
    }
}
