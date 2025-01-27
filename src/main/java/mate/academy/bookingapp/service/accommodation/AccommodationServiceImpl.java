package mate.academy.bookingapp.service.accommodation;

import static mate.academy.bookingapp.service.notification.MessageBuilder.buildAccommodationCreatedMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildAccommodationDeletedMessage;
import static mate.academy.bookingapp.service.notification.MessageBuilder.buildAccommodationUpdatedMessage;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationSummaryDto;
import mate.academy.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.bookingapp.exception.DuplicateEntityException;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.AccommodationMapper;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Amenity;
import mate.academy.bookingapp.repository.accommodation.AccommodationRepository;
import mate.academy.bookingapp.service.amenity.AmenityService;
import mate.academy.bookingapp.service.notification.TelegramNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AmenityService amenityService;
    private final TelegramNotificationService notificationService;

    @Override
    @Transactional
    public AccommodationDto save(final CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);

        if (accommodationRepository.existsByNameAndLocation(
                accommodation.getName(), accommodation.getLocation())) {
            throw new DuplicateEntityException(
                    "Accommodation with the same name and location already exists!");
        }

        Set<Amenity> amenities = amenityService.findOrCreateByNames(requestDto.getAmenities());
        accommodation.setAmenities(amenities);

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        notificationService.sendAccommodationMessage(
                buildAccommodationCreatedMessage(savedAccommodation));

        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    @Transactional
    public AccommodationDto update(final Long id, final UpdateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Accommodation with id " + id + " not found"));

        if (accommodationRepository.existsByNameAndLocationAndIdNot(
                requestDto.getName(), requestDto.getLocation(), id)) {
            throw new DuplicateEntityException(
                    "Another accommodation with the same name and location already exists");
        }

        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);

        if (requestDto.getAmenities() != null) {
            Set<Amenity> amenities = amenityService.findOrCreateByNames(requestDto.getAmenities());
            accommodation.setAmenities(amenities);
        }

        notificationService.sendAccommodationMessage(
                buildAccommodationUpdatedMessage(accommodation));

        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationSummaryDto> findAll(final Pageable pageable) {
        return accommodationRepository.findAll(pageable).map(accommodationMapper::toSummaryDto);
    }

    @Override
    public AccommodationDto findById(Long id) {
        Accommodation accommodation = accommodationRepository.findWithAmenitiesById(id).orElseThrow(
                () -> new EntityNotFoundException("Accommodation with id " + id + " not found")
        );
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Accommodation with id " + id + " not found")
        );

        accommodation.setDeleted(true);

        notificationService.sendAccommodationMessage(
                buildAccommodationDeletedMessage(accommodation));

        accommodationRepository.save(accommodation);
    }
}
