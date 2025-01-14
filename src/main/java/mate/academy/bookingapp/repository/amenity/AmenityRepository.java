package mate.academy.bookingapp.repository.amenity;

import java.util.Optional;
import mate.academy.bookingapp.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByName(String name);
}
