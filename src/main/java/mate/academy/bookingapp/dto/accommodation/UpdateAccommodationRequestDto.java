package mate.academy.bookingapp.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import mate.academy.bookingapp.model.AccommodationType;
import mate.academy.bookingapp.validation.EnumValidator;

@Data
public class UpdateAccommodationRequestDto {
    @NotBlank(message = "Accommodation name cannot be blank during update")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "The description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Accommodation type is required")
    @EnumValidator(enumClass = AccommodationType.class,
            message = "Invalid accommodation type. "
                    + "Allowed values: HOUSE, APARTMENT, CONDO, VACATION HOME")
    private String accommodationType;

    @NotNull(message = "Availability is required")
    @PositiveOrZero(message = "Availability must be zero or a positive number")
    private Integer availability;

    @NotBlank(message = "Location cannot be blank during update")
    @Size(max = 200, message = "The location cannot exceed 200 characters")
    private String location;

    @NotBlank(message = "Size cannot be blank during update")
    @Size(max = 50, message = "Size cannot exceed 50 characters")
    private String size;

    private Set<String> amenities;

    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be greater than zero")
    private BigDecimal dailyRate;
}
