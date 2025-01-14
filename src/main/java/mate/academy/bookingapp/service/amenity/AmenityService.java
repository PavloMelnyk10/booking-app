package mate.academy.bookingapp.service.amenity;

import mate.academy.bookingapp.model.Amenity;

public interface AmenityService {
    Amenity findOrCreateByName(String name);
}

