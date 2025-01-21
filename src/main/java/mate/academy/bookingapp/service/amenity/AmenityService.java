package mate.academy.bookingapp.service.amenity;

import java.util.Set;
import mate.academy.bookingapp.model.Amenity;

public interface AmenityService {
    Amenity findOrCreateByName(String name);

    Set<Amenity> findOrCreateByNames(Set<String> names);
}

