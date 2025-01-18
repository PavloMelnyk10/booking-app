package mate.academy.bookingapp.service.amenity;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.mapper.AmenityMapper;
import mate.academy.bookingapp.model.Amenity;
import mate.academy.bookingapp.repository.amenity.AmenityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    public Amenity findOrCreateByName(final String name) {
        return amenityRepository.findByName(name)
                .orElseGet(() -> amenityRepository.save(amenityMapper.mapFromName(name)));
    }

    @Override
    public Set<Amenity> findOrCreateByNames(final Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(this::findOrCreateByName)
                .collect(Collectors.toSet());
    }
}
