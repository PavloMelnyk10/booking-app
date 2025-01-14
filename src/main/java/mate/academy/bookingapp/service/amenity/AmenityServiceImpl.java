package mate.academy.bookingapp.service.amenity;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.model.Amenity;
import mate.academy.bookingapp.repository.amenity.AmenityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;

    public Amenity findOrCreateByName(String name) {
        return amenityRepository.findByName(name)
                .orElseGet(() -> {
                    Amenity amenity = new Amenity();
                    amenity.setName(name);
                    return amenityRepository.save(amenity);
                });
    }
}
