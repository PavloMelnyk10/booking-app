package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationSummaryDto;
import mate.academy.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.bookingapp.service.accommodation.AccommodationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation Management", description = "Endpoints for managing accommodations")
@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping
    @Operation(summary = "Get all accommodations",
            description = "Retrieve a paginated list of all available accommodations")
    public Page<AccommodationSummaryDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find accommodation by ID",
            description = "Retrieve details of a specific accommodation by ID")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new accommodation",
            description = "Add a new accommodation to the system")
    public AccommodationDto createAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update accommodation details",
            description = "Update details of an existing accommodation by ID")
    public AccommodationDto updateAccommodationDetails(
            @PathVariable Long id, @RequestBody @Valid UpdateAccommodationRequestDto requestDto) {
        return accommodationService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete accommodation",
            description = "Mark an accommodation as deleted in the system by ID")
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
