package mate.academy.bookingapp.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import mate.academy.bookingapp.model.Role;

@Getter
@Setter
public class UpdateUserRolesRequestDto {
    @NotEmpty(message = "Roles cannot be empty")
    private Set<Role.RoleName> roles;
}
