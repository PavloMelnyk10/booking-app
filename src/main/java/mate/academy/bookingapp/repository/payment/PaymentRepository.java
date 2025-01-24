package mate.academy.bookingapp.repository.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT p FROM Payment p
    WHERE p.status = 'PENDING'
      AND p.createdAt < :cutoffTime
            """)
    List<Payment> findPendingBefore(LocalDateTime cutoffTime);

    Page<Payment> findAllByBookingUserId(Long id, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);

    boolean existsByBookingIdAndStatus(Long id, PaymentStatus paymentStatus);
}
