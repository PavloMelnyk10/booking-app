package mate.academy.bookingapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "BearerAuth";
    private static final String SCHEME_TYPE = "bearer";
    private static final String SCHEME_FORMAT = "JWT";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(SCHEME_TYPE)
                                        .bearerFormat(SCHEME_FORMAT)))
                .addSecurityItem(new SecurityRequirement()
                        .addList(BEARER_AUTH));
    }
}
