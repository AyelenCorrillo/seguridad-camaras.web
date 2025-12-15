package com.camaras.seguridadcamarasweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity // Habilita la seguridad web de Spring
public class SecurityConfig {

    /**
     * Define el bean para el cifrado de contraseñas.
     * BCrypt es el estándar recomendado.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define las reglas de autorización y los formularios de login/logout.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // RUTAS PÚBLICAS (accesibles sin login)
                .requestMatchers("/", "/login", "/register", "/contact", 
                                 "/css/**", "/js/**", "/images/**").permitAll()
                
                // RUTAS DE ACCESO RESTRINGIDO (SOLO CLIENTES y ADMIN)
                // /orders, /catalogo, /installation requieren login
                .requestMatchers("/orders/**", "/catalogo/**", "/installation/**").hasAnyRole("CLIENTE", "ADMIN")
                
                // RUTAS DE ADMINISTRACIÓN (SOLO ADMIN)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")        // URL de tu vista de login (Thymeleaf)
                .defaultSuccessUrl("/catalogo", true) // Redirección exitosa
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")       // URL para el logout (POST)
                .logoutSuccessUrl("/")      // Redirección después del logout
                .permitAll()
            );

        // Si usas un motor de plantillas como Thymeleaf, CSRF suele ser manejado
        // automáticamente y no necesita ser deshabilitado.

        return http.build();
    }
}
