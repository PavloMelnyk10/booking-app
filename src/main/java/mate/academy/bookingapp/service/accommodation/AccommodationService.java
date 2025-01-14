package mate.academy.bookingapp.service.accommodation;

import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationSummaryDto;
import mate.academy.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    Page<AccommodationSummaryDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    void deleteById(Long id);

    AccommodationDto update(Long id, UpdateAccommodationRequestDto requestDto);
}
