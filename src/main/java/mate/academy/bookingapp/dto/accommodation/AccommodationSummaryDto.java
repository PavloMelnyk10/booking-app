package mate.academy.bookingapp.dto.accommodation;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.bookingapp.model.AccommodationType;

@Data
public class AccommodationSummaryDto {
    private Long id;
    private String name;
    private AccommodationType accommodationType;
    private String location;
    private String size;
    private BigDecimal dailyRate;
}
