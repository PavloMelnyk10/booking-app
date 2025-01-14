package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "Endpoint for checking the service health status")
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "Check service health",
            description = "Returns a message indicating the current health status of the service")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("Service is up and running!");
    }
}
