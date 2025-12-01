package com.levelup.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (login, registro)
                .requestMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()
                
                // Productos: accesible para ADMIN y VENDEDOR (lectura) y ADMIN (escritura)
                .requestMatchers("/api/v1/products/**").permitAll()
                
                // Órdenes: accesible para ADMIN y VENDEDOR
                .requestMatchers("/api/v1/orders/**").permitAll()
                
                // Blogs: público en lectura, ADMIN en escritura
                .requestMatchers("/api/v1/blogs/**").permitAll()
                
                // Usuarios: solo ADMIN
                .requestMatchers("/api/v1/users/**").permitAll()
                
                // Por ahora todo público para desarrollo - implementar autenticación JWT después
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}

