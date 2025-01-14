package mate.academy.bookingapp.repository.accommodation;

import java.util.Optional;
import mate.academy.bookingapp.model.Accommodation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = "amenities")
    Optional<Accommodation> findWithAmenitiesById(Long id);

    boolean existsByNameAndLocation(String name, String location);

    boolean existsByNameAndLocationAndIdNot(String name, String location, Long id);
}
