package mate.academy.bookingapp.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationSummaryDto;
import mate.academy.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Amenity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "amenities", expression = "java(mapAmenities(requestDto.getAmenities()))")
    @Mapping(target = "accommodationType",
            expression = "java(mate.academy.bookingapp.model.AccommodationType"
                    + ".valueOf(requestDto.getAccommodationType().toUpperCase()))")
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    @Mapping(target = "amenities",
            expression = "java(mapAmenityNames(accommodation.getAmenities()))")
    AccommodationDto toDto(Accommodation accommodation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateAccommodationFromDto(UpdateAccommodationRequestDto requestDto,
                                    @MappingTarget Accommodation accommodation);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "accommodationType", source = "accommodationType")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "dailyRate", source = "dailyRate")
    AccommodationSummaryDto toSummaryDto(Accommodation accommodation);

    default Set<Amenity> mapAmenities(Set<String> amenities) {
        return amenities.stream()
                .map(name -> {
                    Amenity amenity = new Amenity();
                    amenity.setName(name);
                    return amenity;
                })
                .collect(Collectors.toSet());
    }

    default Set<String> mapAmenityNames(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
    }
}