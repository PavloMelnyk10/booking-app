package mate.academy.bookingapp.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.model.Amenity;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AmenityMapper {

    default Amenity mapFromName(String name) {
        Amenity amenity = new Amenity();
        amenity.setName(name);
        return amenity;
    }

    default Set<Amenity> mapAmenities(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(this::mapFromName)
                .collect(Collectors.toSet());
    }
}
