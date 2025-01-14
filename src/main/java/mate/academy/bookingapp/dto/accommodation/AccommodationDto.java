package mate.academy.bookingapp.dto.accommodation;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import mate.academy.bookingapp.model.AccommodationType;

@Data
public class AccommodationDto {
    private Long id;
    private String name;
    private String description;
    private AccommodationType accommodationType;
    private Integer availability;
    private String location;
    private String size;
    private Set<String> amenities;
    private BigDecimal dailyRate;
}
