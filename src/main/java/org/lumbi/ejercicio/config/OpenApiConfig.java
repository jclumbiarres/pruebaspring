package org.lumbi.ejercicio.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Books API", version = "1.0", description = "API REST para gestión de libros con autenticación JWT", contact = @Contact(name = "Juan Carlos Lumbiarres", email = "jclumbiarres@protonmail.com")), servers = {
        @Server(url = "http://localhost:8080", description = "Servidor local"),
        @Server(url = "https://tu-dominio.com", description = "Servidor producción")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Introduce el token JWT obtenido del endpoint /api/user/auth")
public class OpenApiConfig {
}