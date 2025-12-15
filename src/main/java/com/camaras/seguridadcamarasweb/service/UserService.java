package com.camaras.seguridadcamarasweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.camaras.seguridadcamarasweb.enums.UserRole;
import com.camaras.seguridadcamarasweb.model.User;
import com.camaras.seguridadcamarasweb.repository.UserRepository;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el cifrador

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Registra un nuevo usuario cliente.
     */
    public User registerNewClient(User user) {
        // Cifrar la contraseña antes de guardarla en la base de datos
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CLIENTE);
        
        return userRepository.save(user);
    }
}
