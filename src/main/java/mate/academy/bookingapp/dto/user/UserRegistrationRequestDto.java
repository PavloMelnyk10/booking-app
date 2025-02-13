package mate.academy.bookingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.bookingapp.validation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    @Size(min = 6, max = 64, message = "Email must be between 8 and 35 characters")
    private String email;

    @NotBlank
    @Size(min = 8, max = 35, message = "Password must be between 8 and 35 characters")
    private String password;

    @NotBlank
    @Size(min = 8, max = 35, message = "Password confirmation must be between 8 and 35 characters")
    private String confirmPassword;

    @NotBlank
    @Size(min = 2, max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
}
