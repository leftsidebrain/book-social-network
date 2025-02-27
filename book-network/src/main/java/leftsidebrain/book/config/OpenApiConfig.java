package leftsidebrain.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "leftsidebrain",
						email = "ruddikhai10@gmail.com"
				),
				description = "OpenApi documentation for Spring security",
				title = "OpenApi specification",
				version = "1.0",
				license = @License(
						name = "Me",
						url = "http//licence.com"
				),
				termsOfService = "Term of service"
		),
		servers = {
				@Server(
						description = "Local ENV",
						url = "http://localhost:8088/api/v1"
				),
				@Server(
						description = "PROD ENV",
						url = "https://example.com"
				)
		},
		security = {
				@SecurityRequirement(
						name = "bearerAuth"
				)
		}
)
@SecurityScheme(
		name = "bearerAuth",
		description = "JWT auth desc",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
