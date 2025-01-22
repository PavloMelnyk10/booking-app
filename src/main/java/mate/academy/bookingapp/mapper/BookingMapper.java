package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.CreateBookingRequestDto;
import mate.academy.bookingapp.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Booking toModel(CreateBookingRequestDto requestDto);

    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "userId", source = "user.id")
    BookingDto toDto(Booking booking);
}
