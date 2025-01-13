package mate.academy.bookingapp.repository.role;

import java.util.Optional;
import mate.academy.bookingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);
}
