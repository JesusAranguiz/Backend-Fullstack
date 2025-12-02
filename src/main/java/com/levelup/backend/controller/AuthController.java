package com.levelup.backend.controller;

import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.LoginResponse;
import com.levelup.backend.model.User;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Autenticación", description = "Endpoints para autenticación JWT")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Operation(
        summary = "Iniciar sesión y obtener token JWT",
        description = "Autentica las credenciales y retorna un token JWT válido por 24 horas. " +
                      "Usuarios disponibles: user/1234 (USER), admin/admin (ADMIN)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso - Token JWT generado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"username\":\"user\",\"role\":\"ROLE_USER\"}")
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Cargar detalles del usuario
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // Obtener el usuario completo de la base de datos
            User user = userRepository.findByEmail(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Generar token JWT
            final String jwt = jwtUtil.generateToken(userDetails);

            // Determinar rol según el tipo de usuario
            String role;
            switch (user.getTipo()) {
                case 0:
                    role = "ROLE_CLIENTE";
                    break;
                case 1:
                    role = "ROLE_VENDEDOR";
                    break;
                case 2:
                    role = "ROLE_ADMIN";
                    break;
                default:
                    role = "ROLE_CLIENTE";
            }

            // Retornar respuesta con token, username y role
            return ResponseEntity.ok(new LoginResponse(jwt, user.getEmail(), role));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    // Endpoint temporal para generar contraseñas BCrypt (ELIMINAR en producción)
    @Operation(summary = "Generar hash BCrypt", description = "Endpoint temporal para generar contraseñas encriptadas")
    @GetMapping("/hash/{password}")
    public ResponseEntity<String> hashPassword(@PathVariable String password) {
        String hashed = passwordEncoder.encode(password);
        return ResponseEntity.ok(hashed);
    }

    // Endpoint para migrar contraseñas de texto plano a BCrypt (EJECUTAR UNA SOLA VEZ)
    @Operation(summary = "Migrar contraseñas a BCrypt", description = "Convierte todas las contraseñas de texto plano a BCrypt")
    @PostMapping("/migrate-passwords")
    public ResponseEntity<?> migratePasswords() {
        try {
            var users = userRepository.findAll();
            int updated = 0;
            
            for (User user : users) {
                String plainPassword = user.getPassword();
                
                // Solo encriptar si no está ya encriptada (BCrypt empieza con $2a$)
                if (!plainPassword.startsWith("$2a$")) {
                    String hashedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(hashedPassword);
                    userRepository.save(user);
                    updated++;
                }
            }
            
            return ResponseEntity.ok("Migración completada. " + updated + " contraseñas actualizadas.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en migración: " + e.getMessage());
        }
    }
}
