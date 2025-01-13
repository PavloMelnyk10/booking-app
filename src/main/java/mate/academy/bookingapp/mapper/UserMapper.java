package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
import mate.academy.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto dto);

    UserResponseDto toUserResponse(User user);
}
