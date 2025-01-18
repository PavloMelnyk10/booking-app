package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UpdateUserRequestDto;
import mate.academy.bookingapp.dto.user.UpdateUserRolesRequestDto;
import mate.academy.bookingapp.dto.user.UserResponseDto;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Management", description = "Endpoints for managing user profiles and roles")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user details",
            description = "Retrieve the profile details of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    public UserResponseDto getCurrentUserDetails() {
        return userService.getCurrentUserDetails();
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user details",
            description = "Update the profile information of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "User details updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public UserResponseDto updateUserDetails(
            @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        return userService.updateUserDetails(updateUserRequestDto);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Update user roles",
            description = "Allows users with SUPER_ADMIN role to update roles of a specific user. "
                    + "Allowed roles: ADMIN, USER.")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public UserResponseDto updateUserRoles(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRolesRequestDto requestDto) {
        return userService.updateUserRoles(id, requestDto);
    }
}
