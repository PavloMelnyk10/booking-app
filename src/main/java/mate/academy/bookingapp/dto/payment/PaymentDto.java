package mate.academy.bookingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.bookingapp.model.PaymentStatus;

@Data
public class PaymentDto {
    private Long id;
    private Long bookingId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;
    private PaymentStatus status;
}
