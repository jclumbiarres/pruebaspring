package org.lumbi.ejercicio.controller;

import java.util.Map;
import org.lumbi.ejercicio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro de usuarios")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema. El password será hasheado con Argon2.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                        {
                                            "message": "Usuario registrado exitosamente",
                                            "userId": 1,
                                            "username": "testuser123"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "409", description = "El nombre de usuario ya existe", content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                        {
                                            "error": "El nombre de usuario ya existe"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                        {
                                            "error": "Username debe tener entre 3 y 50 caracteres"
                                        }
                                        """)))
        })
        @PostMapping("/register")
        public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
                return userService.registerUser(request.username(), request.password())
                                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(Map.of(
                                                                "message", "Usuario registrado exitosamente",
                                                                "userId", user.getId(),
                                                                "username", user.getUsername())))
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "El nombre de usuario ya existe")));
        }

        @Operation(summary = "Autenticar usuario", description = "Autentica un usuario y devuelve un token JWT Bearer válido por 1 hora")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Autenticación exitosa - Token JWT generado", content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                        {
                                            "message": "Autenticación exitosa",
                                            "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsInVzZXJJZCI6MSwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NjE4NzE5MzgsImV4cCI6MTc2MTg3NTUzOH0.d6rYMAag5gTa1QyBAC7vWObfk6gqAuN0w-uhik5DXIg",
                                            "tokenType": "Bearer",
                                            "expiresIn": "1h"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "application/json", schema = @Schema(example = """
                                        {
                                            "error": "Credenciales inválidas"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        })
        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
                return userService.authenticateAndGenerateToken(request.username(), request.password())
                                .map(token -> ResponseEntity.ok()
                                                .body(Map.of(
                                                                "message", "Autenticación exitosa",
                                                                "accessToken", token,
                                                                "tokenType", "Bearer",
                                                                "expiresIn", "1h")))
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(Map.of("error", "Credenciales inválidas")));
        }

        // DTOs con validación
        @Schema(description = "Datos para registro de usuario")
        public record RegistrationRequest(
                        @Schema(description = "Nombre de usuario único", example = "testuser123", requiredMode = Schema.RequiredMode.REQUIRED) @jakarta.validation.constraints.NotBlank(message = "Username no puede estar vacío") @jakarta.validation.constraints.Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres") @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username solo puede contener letras, números, guiones y guiones bajos") String username,

                        @Schema(description = "Contraseña del usuario (será hasheada con Argon2)", example = "MySecureP@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED) @jakarta.validation.constraints.NotBlank(message = "Password no puede estar vacío") @jakarta.validation.constraints.Size(min = 8, max = 100, message = "Password debe tener entre 8 y 100 caracteres") String password) {
        }

        @Schema(description = "Credenciales para autenticación")
        public record AuthRequest(
                        @Schema(description = "Nombre de usuario", example = "testuser123", requiredMode = Schema.RequiredMode.REQUIRED) @jakarta.validation.constraints.NotBlank(message = "Username no puede estar vacío") String username,

                        @Schema(description = "Contraseña del usuario", example = "MySecureP@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED) @jakarta.validation.constraints.NotBlank(message = "Password no puede estar vacío") String password) {
        }
}