package mate.academy.bookingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.bookingapp.validation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class UpdateUserRequestDto {
    @Email(message = "Invalid email format")
    @Size(min = 6, max = 64, message = "Email must be between 8 and 35 characters")
    private String email;

    @Size(min = 8, max = 35, message = "Password must be between 8 and 35 characters")
    private String password;

    @Size(min = 8, max = 35, message = "Password confirmation must be between 8 and 35 characters")
    private String confirmPassword;

    @Size(min = 2, max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
}
