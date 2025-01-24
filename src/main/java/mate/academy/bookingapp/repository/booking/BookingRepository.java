package mate.academy.bookingapp.repository.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status IN ('PENDING', 'CONFIRMED') "
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    long countOverlappingBookings(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    @Query("SELECT COUNT(b) FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status IN ('PENDING', 'CONFIRMED') "
            + "AND b.id != :bookingId "
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    long countOverlappingBookings(Long accommodationId,
                                  Long bookingId,
                                  LocalDate checkInDate,
                                  LocalDate checkOutDate);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    Page<Booking> findAllByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByStatusAndCheckOutDate(BookingStatus bookingStatus,
                                                 LocalDate checkOutDate);

    @Query("""
    SELECT b FROM Booking b
    WHERE b.status = 'PENDING'
      AND b.createdAt < :cutoffTime
      AND NOT EXISTS (
          SELECT p FROM Payment p
          WHERE p.booking.id = b.id AND p.status = 'PENDING'
      )
            """)
    List<Booking> findPendingWithoutPaymentsBefore(LocalDateTime cutoffTime);

}
