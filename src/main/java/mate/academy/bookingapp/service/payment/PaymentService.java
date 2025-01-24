package mate.academy.bookingapp.service.payment;

import mate.academy.bookingapp.dto.payment.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentDto createPaymentSession(Long bookingId, String successUrl, String cancelUrl);

    String handleSuccess(String sessionId);

    String handleCancel(String sessionId);

    Page<PaymentDto> getCurrentUserPayments(Pageable pageable);

    Page<PaymentDto> findAllByUserId(Long userId, Pageable pageable);

    Page<PaymentDto> getAllPayments(Pageable pageable);
}
