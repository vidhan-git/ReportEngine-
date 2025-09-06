package reportEngine.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

        @Autowired
        private OpenApiProperties openApiProperties;

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";
                OpenAPI openAPI = new OpenAPI()
                                .info(new Info()
                                                .title("ReportEngine API")
                                                .version("1.0")
                                                .description("ReportEngine API Documentation"))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .in(SecurityScheme.In.HEADER)));
                if (openApiProperties.getServers() != null) {
                        List<Server> servers = openApiProperties.getServers().stream()
                                        .map(s -> new Server().url(s.getUrl()).description(s.getDescription()))
                                        .collect(Collectors.toList());
                        openAPI.setServers(servers);
                }
                return openAPI;
        }
}