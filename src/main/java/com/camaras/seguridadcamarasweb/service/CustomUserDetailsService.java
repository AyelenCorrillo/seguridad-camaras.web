package com.camaras.seguridadcamarasweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.camaras.seguridadcamarasweb.model.User;
import com.camaras.seguridadcamarasweb.repository.UserRepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Método central de Spring Security: Carga el usuario por su nombre de usuario (email).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar la entidad User en la base de datos
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // 2. Mapear la entidad User a la estructura UserDetails de Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) // La contraseña debe estar cifrada
                .roles(user.getRole().name()) // Asigna el rol (ej. "CLIENTE")
                .build();
    }
}
